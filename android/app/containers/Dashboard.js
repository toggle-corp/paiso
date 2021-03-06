import React, { Component } from 'react';
import {
    View
} from 'react-native';
import Icon from 'react-native-vector-icons/MaterialIcons';
import { Toolbar } from 'react-native-material-ui';
import { connect } from 'react-redux';

import AmountHeader from '../components/AmountHeader';
import DashboardTransactionList from '../components/DashboardTransactionList';
import { getAmount } from '../utils';


class Dashboard extends Component {
    static navigationOptions = {
        tabBarIcon: ({ tintColor }) => (
            <Icon name="dashboard" size={24} color={tintColor} />
        ),
    };

    render() {
        const { navigate } = this.props.navigation;
        const total = this.props.transactions.reduce((a, t) => a + t.amount, 0);
        return (
            <View style={{ flex:1, backgroundColor: '#616161' }}>
                <Toolbar centerElement="Dashboard" leftElement='dashboard' />
                <AmountHeader amount={total} />
                <DashboardTransactionList
                    transactions={this.props.transactions}
                    onSelect={(id) => navigate('Contact', { contactId: id })}
                />
            </View>
        );
    }
}


const mapStateToProps = (state) => {
    let transactions = [];

    state.contacts.forEach(contact => {
        let ts = state.transactions.filter( t =>
            !t.deleted && (t.contact == contact.id ||
            (t.user && t.user == contact.user && t.approvalStatus == 'approved')));

        if (ts.length > 0) {
            transactions.push({
                id: contact.id,
                name: contact.name,
                username: undefined,
                amount: ts.reduce((a, t) => a + getAmount(t, state.auth.myId), 0),
            });
        }
    });

    return {
        transactions: transactions,
    };
};


export default connect(mapStateToProps)(Dashboard);

