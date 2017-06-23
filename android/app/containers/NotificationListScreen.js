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

import { InfoTransaction, PendingTransaction } from '../components/NotificationItem';
import styles from '../styles/notification';

import { approveTransaction, acceptTransaction, rejectTransaction } from '../actions/transactions';
import { getAmount, getAcknowledgeStatus } from '../utils';


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
        const dataSource = this.ds.cloneWithRows(this.props.transactions);
        return (
            <View style={{ flex: 1, backgroundColor: '#616161', }}>
                <Toolbar centerElement='Notifications' leftElement='notifications' />
                <ListView
                    style={{ paddingTop: 4 }}
                    dataSource={dataSource}
                    enableEmptySections={true}
                    renderRow={data => (data.approvalStatus == 'pending') ? (
                        <PendingTransaction
                            transaction={data}
                            onAccept={() => this.accept(data) }
                            onReject={() => this.reject(data) }
                        />
                    ) :
                    (
                        <InfoTransaction
                            transaction={data}
                            onDismiss={() => this.acknowledge(data) }
                        />
                    )}
                />
                <FcmController />
            </View>
        );
    }

    accept(transaction) {
        this.props.accept(transaction);
    }

    acknowledge(transaction) {
        this.props.acknowledge(transaction);
    }

    reject(transaction) {
        Alert.alert('Are you sure you want to reject this transaction?',
            transaction.title + ' by ' + transaction.contact.name,
            [
                { text: 'No' },
                { text: 'Yes', onPress: () => {
                    this.props.reject(transaction);
                }, },
            ]);
    }
}


const mapStateToProps = (state) => {
    let transactions = state.transactions.filter(t =>
        (t.user && t.user != state.auth.myId &&
            ((t.approvalStatus == 'pending' && !t.deleted) ||
                getAcknowledgeStatus(t))))
            .map(t => ({
                id: t.id,
                title: t.title,
                contact: state.contacts.find(c => c.user == t.user),
                contactId: t.contact,
                amount: getAmount(t, state.auth.myId),
                user: t.user,
                transactionType: t.transactionType,
                approvalStatus: t.approvalStatus,
                acknowledgeStatus: getAcknowledgeStatus(t),
                editedAt: t.editedAt,
            }));

    transactions = transactions.filter(t => t.contact);
    transactions.sort((t1, t2) => (new Date(t2.editedAt) - new Date(t1.editedAt)));
    return {
        transactions,
    };
};


const mapDispatchToProps = (dispatch) => {
    return {
        accept: (transaction) => dispatch(acceptTransaction(transaction.id)),
        reject: (transaction) => dispatch(rejectTransaction(transaction.id)),
        acknowledge: (transaction) => dispatch(approveTransaction(transaction.id)),
    };
};

export default connect(mapStateToProps, mapDispatchToProps)(NotificationListScreen);
