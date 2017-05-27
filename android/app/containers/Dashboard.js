import React, { Component } from 'react';
import {
    View,
} from 'react-native';

// import styles from '../styles/dashboard';
import AmountHeader from '../components/AmountHeader';
import UserList from '../components/UserList';


export default class Dashboard extends Component {
    static navigationOptions = {
        title: 'Paiso',
    };

    render() {
        let users = [
            { name: 'Aditya Noob', amount: '200', },
            { name: 'Frozen Helium', amount: '500', },
        ];

        return (
            <View>
                <AmountHeader amount={200} />
                <UserList users={users} />
            </View>
        );
    }
}
