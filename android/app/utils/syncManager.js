import { request } from '../utils';
import { addContact, editContact } from '../actions/contacts';
import { addTransaction, editTransaction } from '../actions/transactions';
import { clearUsers, saveUser } from '../actions/users';
import { saveMyId } from '../actions/auth';
import { saveToken } from '../actions/fcm';


const syncManager = {
    init: function(store) {
        this.unsubscribe = store.subscribe(() => {
            if (this.onSync) {
                return;
            }

            if (!store.getState().auth.token) {
                return;
            }

            this.onSync = true;

            // Start by synchronizing data of logged in user
            this.syncSelf(store).then(() => {
                // Then synchronize fcm token if exists and
                return this.syncFcm(store);
            }).then(() => {
                // synchronize contacts, users and transactions
                return this.sync(store);
            }).then(() => {
                this.onSync = false;
            });
        });
    },

    syncFcm: function(store) {
        let state = store.getState();
        let dispatch = store.dispatch;

        const data = {
            user: state.auth.myId,
            token: state.fcm.token,
        };

        if (state.fcm.token && state.auth.myId) {
            (() => {
                if (state.fcm.id) {
                    return request('fcm-token/' + state.fcm.id + '/', data, 'PUT', state.auth.token);
                } else {
                    return request('fcm-token/', data, 'POST', state.auth.token);
                }
            })().then(json => {
                dispatch(saveToken(json.pk, json.token));
            })
                .catch(error => console.log(error));
        }
    },

    syncSelf: function(store) {
        let state = store.getState();
        let dispatch = store.dispatch;

        return request('user/me/', null, 'GET', state.auth.token)
            .then(json => {
                // Save self id
                dispatch(saveMyId(json.pk));

                // We may clear all users since we will soon be getting fresh data
                dispatch(clearUsers());

                // Then save self data as a user
                dispatch(saveUser(json.pk, json.username, json.first_name, json.last_name));
            })
            .catch(error => console.log(error));
    },

    sync: function(store) {

        let state = store.getState();
        let dispatch = store.dispatch;

        // Sync edited contacts
        const contactPosts = [];
        state.contacts.forEach(contact => {
            if (contact.status != 'edited') {
                return;
            }

            const data = {
                name: contact.name,
                user: contact.user,
            };
            let method = 'POST';
            let url = 'contact/';

            if (contact.id > 0) {
                url += contact.id + '/';
                method = 'PUT';
            }

            contactPosts.push(request(url, data, method, state.auth.token)
                .then(json => {
                    dispatch(editContact(contact.id,
                        json.name, json.user, json.pk, json.created_at,
                        json.edited_at, 'sync'));
                }).catch(error => console.log(error)));
        });

        // Fetch contact list from server
        const contactsFetched = Promise.all(contactPosts).then(() => {
            return request('contact/', null, 'GET', state.auth.token);
        }).then(json => {
            state = store.getState();

            json.forEach(contact => {
                if (state.contacts.find(c => c.id == contact.pk)) {
                    dispatch(editContact(contact.pk,
                        contact.name, contact.user, null, contact.created_at,
                        contact.edited_at, 'sync'));
                }
                else {
                    dispatch(addContact(contact.name, contact.user, contact.pk,
                        contact.created_at, contact.edited_at, 'sync'));
                }
            });
        }).then(() => {
            state = store.getState();

            // Next fetch related users
            const usersFetched = [];
            state.contacts.forEach(contact => {
                if (!contact.user) {
                    return;
                }

                usersFetched.push(
                    request('user/' + contact.user + '/', null, 'GET', state.auth.token)
                    .then(json => {
                        dispatch(saveUser(json.pk, json.username, json.first_name, json.last_name));
                    })
                );
            });

            return Promise.all(usersFetched);
        }).catch(error => console.log(error));

        // Next sync edited transactions
        const transactionsFetched = contactsFetched.then(() => {
            state = store.getState();
            const transactionPosts = [];
            state.transactions.forEach(transaction => {
                if (transaction.status != 'edited') {
                    return;
                }

                const data = {
                    transaction_type: transaction.transactionType,
                    user: transaction.user,
                    contact: transaction.contact,
                    title: transaction.title,
                    amount: transaction.amount,
                    status: transaction.approvalStatus,
                    deleted: transaction.deleted,
                    created_at: transaction.createdAt.toISOString().split('.')[0],
                    edited_at: transaction.editedAt.toISOString().split('.')[0],
                };
                let method = 'POST';
                let url = 'transaction/';

                if (transaction.id > 0) {
                    url += transaction.id + '/';
                    method = 'PUT';
                }

                transactionPosts.push(request(url, data, method, state.auth.token)
                    .then(json => {
                        dispatch(editTransaction(transaction.id,
                            json.title, json.amount, json.contact, json.transactionType,
                            json.user, json.status, json.deleted, json.pk,
                            new Date(json.created_at), new Date(json.edited_at), 'sync'));
                    }).catch(error => console.log(error)));
            });

            // Finally fetch transaction list from server
            return Promise.all(transactionPosts).then(() => {
                return request('transaction/', null, 'GET', state.auth.token);
            }).then(json => {
                state = store.getState();

                json.forEach(transaction => {
                    // const addedBy = (transaction.user == state.auth.myId ? 'self' : 'other');
                    if (state.transactions.find(t => t.id == transaction.pk)) {
                        dispatch(editTransaction(transaction.pk,
                            transaction.title, transaction.amount, transaction.contact, transaction.transaction_type,
                            transaction.user, transaction.status, transaction.deleted, null,
                            new Date(transaction.created_at), new Date(transaction.edited_at), 'sync'));
                    }
                    else {
                        dispatch(addTransaction(transaction.title, transaction.amount,
                            transaction.contact, transaction.transaction_type,
                            transaction.user, transaction.status, transaction.deleted,
                            transaction.pk,
                            new Date(transaction.created_at), new Date(transaction.edited_at), 'sync'));
                    }
                });
            }).catch(error => console.log(error));
        });

        return transactionsFetched;
    },

    finish: function() {
        this.unsubscribe();
    },
};


export default syncManager;
