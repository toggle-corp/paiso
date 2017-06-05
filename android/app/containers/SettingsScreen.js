import React, { Component } from 'react';
import {
    View,
    Text,
} from 'react-native';
import Icon from 'react-native-vector-icons/MaterialIcons';
import { Button } from 'react-native-material-ui';
import { NavigationActions } from 'react-navigation';
import { connect } from 'react-redux';

import { logout } from '../actions/auth';


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
                <Text>Settings</Text>
                <Button text='Logout' onPress={() => (this.props.logout && this.props.logout())} />
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
