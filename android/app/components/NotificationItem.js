import React from 'react';
import {
    View,
    Text,
    TouchableNativeFeedback,
} from 'react-native';
import { Button } from 'react-native-material-ui';
import Icon from 'react-native-vector-icons/MaterialIcons';
import PropTypes from 'prop-types';
import moment from 'moment';

import styles from '../styles/notification';

function capitalize(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}


export function PendingTransaction(props, context) {
    const { primaryColor } = context.uiTheme.palette;
    return (
        <TouchableNativeFeedback>
            <View style={styles.notification}>
                <View style={styles.content}>
                    <Icon name="account-circle" style={styles.contactIcon} />
                    <View style={styles.labelContainer}>
                        <Text style={styles.transactionTitle}>
                            {props.transaction.title}
                        </Text>
                        <View style={{ flexDirection: 'row', }}>
                            <Text style={styles.transactionType}>
                                Added by
                            </Text>
                            <Text style={styles.contactName}>
                                {props.transaction.contact.name}
                            </Text>
                        </View>
                        <Text style={styles.info}>
                            {moment(props.transaction.editedAt).calendar()}
                        </Text>
                    </View>
                    <Text style={styles.amount}>{props.transaction.amount}</Text>
                </View>
                <View style={styles.footer}>
                    <View style={styles.actionButtons}>
                        <Button style={{ text: { color: 'tomato', } }} text="Reject" onPress={props.onReject} />
                        <Button style={{ text: { color: primaryColor, } }} text="Accept" onPress={props.onAccept} />
                    </View>
                </View>
            </View>
        </TouchableNativeFeedback>
    );
}


export function InfoTransaction(props, context) {
    const { primaryColor } = context.uiTheme.palette;
    return (
        <TouchableNativeFeedback>
            <View style={styles.notification}>
                <View style={styles.content}>
                    <Icon name="account-circle" style={styles.contactIcon} />
                    <View style={styles.labelContainer}>
                        <Text style={styles.transactionTitle}>
                            {props.transaction.title}
                        </Text>
                        <View style={{ flexDirection: 'row', }}>
                            <Text style={styles.transactionType}>
                                {capitalize(props.transaction.acknowledgeStatus)} by
                            </Text>
                            <Text style={styles.contactName}>
                                {props.transaction.contact.name}
                            </Text>
                        </View>
                        <Text style={styles.info}>
                            {moment(props.transaction.editedAt).calendar()}
                        </Text>
                    </View>
                    <Text style={styles.amount}>{props.transaction.amount}</Text>
                </View>
                <View style={styles.footer}>
                    <View style={styles.actionButtons}>
                        <Button style={{ text: { color: primaryColor, } }} text="Dismiss" onPress={props.onDismiss} />
                    </View>
                </View>
            </View>
        </TouchableNativeFeedback>
    );
}


InfoTransaction.contextTypes = {
    uiTheme: PropTypes.object.isRequired,
};
PendingTransaction.contextTypes = {
    uiTheme: PropTypes.object.isRequired,
};
