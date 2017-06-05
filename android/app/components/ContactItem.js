import React from 'react';
import {
    View,
    TouchableNativeFeedback,
    Text,
} from 'react-native';
import Icon from 'react-native-vector-icons/MaterialIcons';

import styles from '../styles/contact';


export default function Contact(props) {
    return (
        <TouchableNativeFeedback onPress={props.onSelect}>
            <View style={styles.contact}>
                <Icon name='account-circle' style={styles.contactIcon}/>
                <View style={styles.contactLabel}>
                    <Text style={styles.contactName}>{props.contact.name}</Text>
                    { props.contact.username && (
                        <Text style={styles.contactInfo}>{props.contact.username}</Text>
                    )}
                </View>
            </View>
        </TouchableNativeFeedback>
    );
}
