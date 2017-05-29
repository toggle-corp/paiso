import React, { Component } from 'react';
import {
    View,
} from 'react-native';

import AmountHeader from '../components/AmountHeader';
import UserList from '../components/UserList';

import styles from '../styles/dashboard';


export default class Dashboard extends Component {
    static navigationOptions = {
        title: 'Paiso',
    };

    render() {
        const { navigate } = this.props.navigation;

        const users = [
            { name: 'Aditya Noob', amount: 200, },
            { name: 'Frozen Helium', amount: 500, },
        ];

        const total = users.reduce((a, b) => a + b.amount, 0);

        return (
            <View style={styles.dashboard}>
                <AmountHeader amount={total} />
                <UserList
                    users={users}
                    onSelect={(user) => navigate('UserScreen', { user: user })} />
            </View>
        );
    }
}
