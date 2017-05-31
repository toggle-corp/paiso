import React from 'react';
import {
    View,
    Text,
    TouchableNativeFeedback,
} from 'react-native';
import Icon from 'react-native-vector-icons/FontAwesome';

import styles from '../styles/dashboard';


export default function ContactTransaction(props) {
    return (
        <TouchableNativeFeedback onPress={props.onSelect}>
            <View style={styles.transaction}>
                <Icon name='circle-o' style={styles.transactionIcon}/>
                <View style={styles.transactionLabel}>
                    <Text style={styles.transactionName}>{props.transaction.name}</Text>
                    <Text style={styles.transactionInfo}>{props.transaction.date.toLocaleString()}</Text>
                </View>
                <Text style={styles.transactionAmount}>{props.transaction.amount}</Text>
            </View>
        </TouchableNativeFeedback>
    );
}
