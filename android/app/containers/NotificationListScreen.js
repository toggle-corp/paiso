import React, { Component } from 'react';
import {
    View,
    ListView,
    Alert,
} from 'react-native';
import Icon from 'react-native-vector-icons/MaterialIcons';
import { Toolbar } from 'react-native-material-ui';
import { connect } from 'react-redux';

import FcmController from './FcmController';

import { getAmount } from '../utils';
import { PendingTransaction } from '../components/NotificationItem';
import styles from '../styles/notification';

import { editTransaction } from '../actions/transactions';


class NotificationListScreen extends Component {
    static navigationOptions = {
        tabBarIcon: ({ tintColor }) => (
            <Icon name="notifications" size={24} color={tintColor} />
        ),
    };

    constructor(props) {
        super(props);
        this.ds = new ListView.DataSource({ rowHasChanged: (r1, r2) => r1 !== r2 });
    }

    render() {
        const dataSource = this.ds.cloneWithRows(this.props.pendingTransactions);
        return (
            <View>
                <Toolbar centerElement='Notifications' leftElement='notifications' />
                <ListView
                    dataSource={dataSource}
                    enableEmptySections={true}
                    renderRow={data => <PendingTransaction transaction={data} onAccept={() => this.accept(data)} onReject={() => this.reject(data)} />}
                    renderSeparator={() => <View style={styles.separator} />}
                />
                <FcmController />
            </View>
        );
    }

    accept(transaction) {
        this.props.accept(transaction);
    }

    reject(transaction) {
        Alert.alert('Are you sure you want to reject this transaction?',
            transaction.title + ' by ' + transaction.contact.name,
            [
                { text: 'No' },
                { text: 'Yes', onPress: () => this.props.reject(transaction), },
            ]);
    }
}


const mapStateToProps = (state) => {
    let pendingTransactions = state.transactions.filter(t =>
        (t.user && t.user != state.auth.myId && t.approvalStatus == 'pending'))
            .map(t => ({
                id: t.id,
                title: t.title,
                contact: state.contacts.find(c => c.user == t.user),
                contactId: t.contact,
                amount: getAmount(t, state.auth.myId),
                user: t.user,
                transactionType: t.transactionType,
            }));

    pendingTransactions = pendingTransactions.filter(t => t.contact);
    return {
        pendingTransactions,
    };
};


const mapDispatchToProps = (dispatch) => {
    return {
        accept: (transaction) => dispatch(
            editTransaction(transaction.id, transaction.title, transaction.amount,
                transaction.contactId, transaction.transactionType, transaction.user,
                'approved')),

        reject: (transaction) => dispatch(
            editTransaction(transaction.id, transaction.title, transaction.amount,
                transaction.contactId, transaction.transactionType, transaction.user,
                'rejected')),
    };
};

export default connect(mapStateToProps, mapDispatchToProps)(NotificationListScreen);
