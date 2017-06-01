import React from 'react';
import {
    View,
    Text,
    TouchableNativeFeedback,
} from 'react-native';
import Icon from 'react-native-vector-icons/FontAwesome';

import styles from '../styles/dashboard';


export default function DashboardTransaction(props) {
    return (
        <TouchableNativeFeedback>
            <View style={styles.transaction}>
                <Icon name='user-o' style={styles.transactionIcon}/>
                <View style={styles.transactionLabel}>
                    <Text style={styles.transactionName}>{props.transaction.name}</Text>
                    {props.transaction.username && (
                        <Text style={styles.transactionInfo}>{props.transaction.username}</Text>
                    )}
                </View>
                <Text style={styles.transactionAmount}>{props.transaction.amount}</Text>
            </View>
        </TouchableNativeFeedback>
    );
}
