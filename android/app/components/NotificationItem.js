import React from 'react';
import {
    View,
    Text,
    TouchableNativeFeedback,
} from 'react-native';
import { Button } from 'react-native-material-ui';
import Icon from 'react-native-vector-icons/MaterialIcons';

import styles from '../styles/notification';


export function PendingTransaction(props) {
    return (
        <TouchableNativeFeedback>
            <View style={styles.notification}>
                <View style={styles.content}>
                    <Icon name="account-circle" style={styles.contactIcon} />
                    <View style={styles.labelContainer}>
                        <Text style={styles.contactName}>{props.transaction.contact.name}</Text>
                        <Text style={styles.transactionTitle}>{props.transaction.title}</Text>
                    </View>
                    <Text style={styles.amount}>{props.transaction.amount}</Text>
                </View>
                <View style={styles.footer}>
                    <Button text="Reject" onPress={props.onReject} />
                    <Button text="Accept" onPress={props.onAccept} />
                </View>
            </View>
        </TouchableNativeFeedback>
    );
}
