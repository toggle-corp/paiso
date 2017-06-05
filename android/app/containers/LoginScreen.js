import React, { Component } from 'react';
import {
    View,
    Text,
    KeyboardAvoidingView,
} from 'react-native';
import PropTypes from 'prop-types';
import { Button } from 'react-native-material-ui';
import { NavigationActions } from 'react-navigation';
import { connect } from 'react-redux';

import LoginPage from '../components/LoginPage';
import RegisterPage from '../components/RegisterPage';
import styles from '../styles/login';

import { login, logout, register } from '../actions/auth';


class LoginScreen extends Component {
    constructor(props) {
        super(props);

        this.state = {
            page: 'login',
        };
    }

    componentWillReceiveProps(nextProps) {
        if (nextProps.token) {
            const resetAction = NavigationActions.reset({
                index: 0,
                actions: [
                    NavigationActions.navigate({ routeName: 'Home'})
                ]
            });
            this.props.navigation.dispatch(resetAction);
        }
    }

    render() {
        const { primaryColor } = this.context.uiTheme.palette;
        return (
            <View style={[styles.container, {backgroundColor: primaryColor}, ]}>
                <KeyboardAvoidingView behavior="position">
                    <View style={styles.header}>
                        <Text style={styles.title}>PAISO</Text>
                        <Text style={styles.subtitle}>{this.state.page == 'login' && 'Login' || 'Register'}</Text>
                    </View>
                    {
                        this.state.page == 'login' &&
                            (<LoginPage style={styles.main} callback={(u, p) => this.login(u, p)} />) ||
                            (<RegisterPage style={styles.main} callback={(f, l, u, p) => this.register(f, l, u, p)} />)
                    }
                </KeyboardAvoidingView>
                <View style={styles.footer}>
                    <Button style={{container: styles.buttonFooter, text: styles.label}}
                        text={this.state.page == 'login' && 'No username? Register' || 'Already registered? Login'}
                        onPress={() => this.setState({ page: this.state.page == 'login' ? 'register' : 'login'})}
                    />
                </View>
            </View>
        );
    }

    login(username, password) {
        if (username.length == 0 || password.length == 0) {
            return;
        }
        if (this.props.login) {
            this.props.login(username, password);
        }
    }

    register(first_name, last_name, username, password) {
        if (this.props.register) {
            this.props.register(first_name, last_name, username, password);
        }
    }
}

LoginScreen.contextTypes = {
    uiTheme: PropTypes.object.isRequired,
};


const mapStateToProps = (state) => ({
    token: state.auth.token,
});

const mapDispatchToProps = (dispatch) => ({
    login: (username, password) => dispatch(login(username, password)),
    logout: () => dispatch(logout()),
    register: (f, l, u, p) => dispatch(register(f, l, u, p)),
});

export default connect(mapStateToProps, mapDispatchToProps)(LoginScreen);

