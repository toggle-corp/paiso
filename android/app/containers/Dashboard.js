import React, { Component } from 'react';
import {
    View
} from 'react-native';
import { Toolbar } from 'react-native-material-ui';
import Icon from 'react-native-vector-icons/MaterialIcons';

import AmountHeader from '../components/AmountHeader';
import DashboardTransactionList from '../components/DashboardTransactionList';


export default class Dashboard extends Component {
    static navigationOptions = {
        tabBarIcon: ({ tintColor }) => (
            <Icon name="dashboard" size={24} color={tintColor} />
        ),
    };

    render() {
        const transactions = [
            { name: 'Ankit Mehta', username: 'frozenhelium', amount: -300, },
            { name: 'Aditya Khatri', username: 'adityakhatri47', amount: 3000, },
        ];
        return (
            <View>
                <Toolbar centerElement="Dashboard" leftElement='dashboard' />
                <AmountHeader amount={3000-300} />
                <DashboardTransactionList transactions={transactions} />
            </View>
        );
    }
}
