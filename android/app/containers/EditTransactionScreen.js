import React, { Component } from 'react';
import {
    View,
    Text,
    TextInput,
} from 'react-native';
import { Toolbar } from 'react-native-material-ui';

import styles from '../styles/common';


export default class EditTransactionScreen extends Component {
    render() {
        const { goBack } = this.props.navigation;
        return (
            <View>
                <Toolbar
                    centerElement={this.props.mode == 'add' ? 'Add transaction' : 'Edit transacction'}
                    leftElement='arrow-back'
                    rightElement='check'
                    onLeftElementPress={() => goBack(null)}
                />
                <View style={styles.formGroup}>
                    <Text>Title</Text>
                    <TextInput autoCapitalize='words' autoCorrect={true} />
                </View>
                <View style={styles.formGroup}>
                    <Text>Amount</Text>
                    <TextInput keyboardType='numeric' />
                </View>
            </View>
        );
    }
}
