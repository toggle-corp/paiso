import React, { Component } from 'react';
import {
    View,
    Text,
} from 'react-native';

import AmountHeader from '../components/AmountHeader';
import ActionButton from '../components/common/ActionButton';

import styles from '../styles/userscreen';
import commonStyles from '../styles/common';


export default class UserScreen extends Component {
    static navigationOptions = ({ navigation }) => ({
        title: `${navigation.state.params.user.name}`,
    });

    render() {
        const user = this.props.navigation.state.params.user;
        return (
            <View style={styles.userscreen}>
                <AmountHeader amount={user.amount} />
                <ActionButton
                    onPress={() => { console.log('hi'); }}
                    component={
                        <Text style={commonStyles.actionButtonText}>+</Text>
                    } />
            </View>
        );
    }
}
