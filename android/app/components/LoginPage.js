import React, { Component } from 'react';
import {
    View,
    Text,
    TextInput,
} from 'react-native';
import { Button } from 'react-native-material-ui';

import styles from '../styles/login';


export default class LoginPage extends Component {
    constructor(props) {
        super(props);

        this.state = {
            username: '',
            password: '',
        };
    }

    render() {
        return (
            <View style={this.props.style}>
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
                    <Button
                        style={{container: styles.button, text: styles.label}}
                        text="Login"
                        onPress={() => this.props.callback && this.props.callback(this.state.username, this.state.password)}
                    />
                </View>
            </View>
        );
    }
}
