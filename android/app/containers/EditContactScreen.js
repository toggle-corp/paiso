import React, { Component } from 'react';
import {
    View,
    Text,
    TextInput,
} from 'react-native';
import { Toolbar } from 'react-native-material-ui';

import LinkContactModal from '../components/LinkContactModal';

import styles from '../styles/common';


export default class EditContactScreen extends Component {
    constructor(props) {
        super(props);

        this.state = {
            linkModalVisible: false,
        };
    }

    render() {
        const { goBack } = this.props.navigation;
        const { params } = this.props.navigation.state;

        return (
            <View>
                <Toolbar
                    centerElement={ params.mode == 'add' ? 'Add contact' : 'Edit contact' }
                    leftElement='arrow-back'
                    rightElement='link'
                    onLeftElementPress={() => goBack(null)}
                    onRightElementPress={() => this.setState({ linkModalVisible: true })}
                />

                <View style={styles.formGroup}>
                    <Text>Name</Text>
                    <TextInput autoCapitalize='words' autoCorrect={true} />
                </View>
                <View style={styles.formGroup}>
                    <Text>Username</Text>
                    <TextInput />
                </View>

                <LinkContactModal
                    visible={this.state.linkModalVisible}
                    onRequestClose={() => this.setState({ linkModalVisible: false })}
                />
            </View>
        );
    }
}
