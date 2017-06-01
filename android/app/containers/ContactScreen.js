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
                <ContactTransactionList transactions={this.props.transactions} onSelect={(id) => navigate('EditTransaction', { mode: 'edit', contactId: params.contactId, transactionId: id })} />
                <ActionButton onPress={() => navigate('EditTransaction', { mode: 'add', contactId: params.contactId })} />
            </View>
        );
    }
}


const mapStateToProps = (state, ownProps) => {
    const { params } = ownProps.navigation.state;
    const ownTransactions =
        state.transactions.filter(t => t.contact == params.contactId);

    return {
        contact: state.contacts.find(c => c.id == params.contactId),
        transactions: ownTransactions.map(transaction => ({
            id: transaction.id,
            title: transaction.title,
            date: transaction.createdAt,
            amount: getAmount(transaction),
            addedBy: transaction.self,
            transactionType: transaction.transactionType,
        })),
    };
};

export default connect(mapStateToProps, null)(ContactScreen);
