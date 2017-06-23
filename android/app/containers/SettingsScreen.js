import React, { Component } from 'react';
import {
    View,
    Text,
    TouchableNativeFeedback,
} from 'react-native';
import Icon from 'react-native-vector-icons/MaterialIcons';
import { Toolbar } from 'react-native-material-ui';
import { NavigationActions } from 'react-navigation';
import { connect } from 'react-redux';

import { logout } from '../actions/auth';
import styles from '../styles/settings';


class SettingsScreen extends Component {
    static navigationOptions = {
        tabBarIcon: ({ tintColor }) => (
            <Icon name="settings" size={24} color={tintColor} />
        ),
    };

    componentWillReceiveProps(nextProps) {
        if (!nextProps.token) {
            const resetAction = NavigationActions.reset({
                index: 0,
                actions: [
                    NavigationActions.navigate({ routeName: 'Login'})
                ]
            });
            this.props.navigation.dispatch(resetAction);
        }
    }

    render() {
        return (
            <View>
                <Toolbar centerElement='Settings' leftElement='settings' />
                <TouchableNativeFeedback>
                    <View style={styles.item}>
                        <Text style={styles.itemTitle}>About</Text>
                        <Text style={styles.itemDescription}>Paiso</Text>
                        <Text style={styles.itemDescription}>Copyright (c) 2017 Togglecorp</Text>
                    </View>
                </TouchableNativeFeedback>
                <View style={styles.separator} />
                <TouchableNativeFeedback
                    onPress={() => (this.props.logout && this.props.logout())} >
                    <View style={styles.item}>
                        <Text style={styles.itemTitle}>Logout</Text>
                    </View>
                </TouchableNativeFeedback>
            </View>
        );
    }
}


const mapStateToProps = (state) => ({
    token: state.auth.token,
});


const mapDispatchToProps = (dispatch) => ({
    logout: () => dispatch(logout()),
});

export default connect(mapStateToProps, mapDispatchToProps)(SettingsScreen);
