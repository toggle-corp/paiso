import React, { Component } from 'react';
import {
    View,
    Text,
    TextInput,
} from 'react-native';
import { Button } from 'react-native-material-ui';

import styles from '../styles/login';


export default class RegisterPage extends Component {
    constructor(props) {
        super(props);

        this.state = {
            first_name: '',
            last_name: '',
            username: '',
            password: '',
            repassword: '',
        };
    }

    register() {
        if (this.props.callback) {
            if (this.state.first_name.length == 0 ||
                this.state.last_name.length == 0 ||
                this.state.username.length == 0 ||
                this.state.password.length == 0 ||
                this.state.repassword.length == 0)
            {
                return;
            }

            if (this.state.password != this.state.repassword) {
                return;
            }

            this.props.callback(this.state.first_name,
                this.state.last_name, this.state.username,
                this.state.password);
        }
    }

    render() {
        return (
            <View style={this.props.style}>
                <View style={styles.field}>
                    <Text style={styles.label}>First name</Text>
                    <TextInput
                        style={styles.input}
                        underlineColorAndroid='#fff'
                        onChangeText={first_name => this.setState({first_name})}
                    />
                </View>
                <View style={styles.field}>
                    <Text style={styles.label}>Last name</Text>
                    <TextInput
                        style={styles.input}
                        underlineColorAndroid='#fff'
                        onChangeText={last_name => this.setState({last_name})}
                    />
                </View>
                <View style={styles.field}>
                    <Text style={styles.label}>Username</Text>
                    <TextInput
                        style={styles.input}
                        underlineColorAndroid='#fff'
                        onChangeText={username => this.setState({username})}
                    />
                </View>
                <View style={styles.field}>
                    <Text style={styles.label}>Password</Text>
                    <TextInput
                        style={styles.input}
                        underlineColorAndroid='#fff'
                        secureTextEntry={true}
                        onChangeText={password => this.setState({password})}
                    />
                </View>
                <View style={styles.field}>
                    <Text style={styles.label}>Retype password</Text>
                    <TextInput
                        style={styles.input}
                        underlineColorAndroid='#fff'
                        secureTextEntry={true}
                        onChangeText={repassword => this.setState({repassword})}
                    />
                </View>
                <View style={styles.field}>
                    <Button
                        style={{container: styles.button, text: styles.label}}
                        text="Register"
                        onPress={() => this.register()}
                    />
                </View>
            </View>
        );
    }
}
