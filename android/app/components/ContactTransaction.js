import React from 'react';
import {
    View,
    Text,
    TouchableNativeFeedback,
} from 'react-native';

import styles from '../styles/dashboard';


export default function ContactTransaction(props) {
    return (
        <TouchableNativeFeedback onPress={props.onSelect}>
            <View style={styles.transaction}>
                <View style={styles.transactionLabel}>
                    <Text style={styles.transactionName}>{props.transaction.title}</Text>
                    <Text style={styles.transactionInfo}>{new Date(props.transaction.date).toLocaleString()}</Text>
                </View>
                <Text style={styles.transactionAmount}>{props.transaction.amount}</Text>
            </View>
        </TouchableNativeFeedback>
    );
}
