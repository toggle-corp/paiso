import React, { Component } from 'react';
import {
    View,
} from 'react-native';
import { ActionButton, Toolbar } from 'react-native-material-ui';
import { connect } from 'react-redux';

import AmountHeader from '../components/AmountHeader';
import ContactTransactionList from '../components/ContactTransactionList';
import { getAmount } from '../utils';


class ContactScreen extends Component {
    static navigationOptions = {
        header: null,
    };

    editTransaction(id) {
        if (this.props.transactions.find(t => t.id == id).user != this.props.myId) {
            return;
        }

        const { navigate } = this.props.navigation;
        const { params } = this.props.navigation.state;
        navigate('EditTransaction', { mode: 'edit', contactId: params.contactId, transactionId: id });
    }

    render() {
        const { navigate, goBack } = this.props.navigation;
        const { params } = this.props.navigation.state;
        const total = this.props.transactions.reduce((a, t) => a + t.amount, 0);

        return (
            <View style={{flex: 1}}>
                <Toolbar
                    centerElement={this.props.contact.name}
                    leftElement='arrow-back'
                    rightElement='edit'
                    onLeftElementPress={() => goBack(null)}
                    onRightElementPress={() => navigate('EditContact', { mode: 'edit', contactId: params.contactId })}
                />
                <AmountHeader amount={total} />
                <ContactTransactionList transactions={this.props.transactions} onSelect={id => this.editTransaction(id)} />
                <ActionButton onPress={() => navigate('EditTransaction', { mode: 'add', contactId: params.contactId })} />
            </View>
        );
    }
}


const mapStateToProps = (state, ownProps) => {
    const { params } = ownProps.navigation.state;

    const contact = state.contacts.find(c => c.id == params.contactId);
    const ownTransactions =
        state.transactions.filter(t => t.contact == params.contactId || (t.user && t.user == contact.user));

    return {
        contact: contact,
        transactions: ownTransactions.map(transaction => ({
            id: transaction.id,
            title: transaction.title,
            date: transaction.createdAt,
            amount: getAmount(transaction, state.auth.myId),
            user: transaction.user,
            transactionType: transaction.transactionType,
        })),
        myId: state.auth.myId,
    };
};

export default connect(mapStateToProps, null)(ContactScreen);
