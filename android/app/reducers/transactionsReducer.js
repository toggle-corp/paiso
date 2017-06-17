const getUniqueId = (transactions) => {
    let id = -1;
    while (transactions.find(t => t.id == id)) {
        --id;
    }
    return id;
};


export default function transactionsReducer(state=[], action) {
    switch (action.type) {
        case 'ADD_TRANSACTION':
            return [
                ...state,
                {
                    id: action.id ? action.id : getUniqueId(state),
                    title: action.title,
                    amount: action.amount,
                    contact: action.contact,
                    transactionType: action.transactionType,
                    user: action.user,
                    approvalStatus: action.approvalStatus,
                    deleted: action.deleted,
                    createdAt: action.createdAt ? action.createdAt : new Date(),
                    editedAt: action.editedAt ? action.editedAt : new Date(),
                    status: action.status,
                }
            ];
        case 'EDIT_TRANSACTION':
            return state.map(transaction => {
                if (transaction.id != action.id) {
                    return transaction;
                }

                return Object.assign({}, transaction, {
                    id: action.newId ? action.newId : transaction.id,
                    title: action.title,
                    amount: action.amount,
                    contact: action.contact,
                    transactionType: action.transactionType,
                    user: action.user,
                    approvalStatus: action.approvalStatus ? action.approvalStatus : transaction.approvalStatus,
                    deleted: action.deleted,
                    createdAt: action.createdAt ? action.createdAt : transaction.createdAt,
                    editedAt: action.editedAt ? action.editedAt : new Date(),
                    status: action.status,
                });
            });

        case 'EDIT_CONTACT':
            if (!action.newId) {
                return state;
            }
            return state.map(transaction => {
                if (transaction.contact != action.id) {
                    return transaction;
                }

                return Object.assign({}, transaction, {
                    contact: action.newId,
                });
            });

        case 'CLEAR_TRANSACTIONS':
            return [];

        default:
            return state;
    }
}
