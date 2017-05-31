import React, { Component } from 'react';
import {
    View,
} from 'react-native';
import { ActionButton, Toolbar } from 'react-native-material-ui';

import AmountHeader from '../components/AmountHeader';
import ContactTransactionList from '../components/ContactTransactionList';


export default class ContactScreen extends Component {
    static navigationOptions = {
        header: null,
    };

    render() {
        const { navigate, goBack } = this.props.navigation;
        const transactions = [
            { name: 'Choreko', date: new Date(), amount: -1000, },
            { name: 'Borrowed', date: new Date(), amount: 500, },
        ];

        return (
            <View style={{flex: 1}}>
                <Toolbar
                    centerElement='Ankit Mehta'
                    leftElement='arrow-back'
                    rightElement='edit'
                    onLeftElementPress={() => goBack(null)}
                    onRightElementPress={() => navigate('EditContact', { mode: 'edit' })}
                />
                <AmountHeader amount={-500} />
                <ContactTransactionList transactions={transactions} onSelect={() => navigate('EditTransaction', { mode: 'edit' })} />
                <ActionButton onPress={() => navigate('EditTransaction', { mode: 'add' })} />
            </View>
        );
    }
}
