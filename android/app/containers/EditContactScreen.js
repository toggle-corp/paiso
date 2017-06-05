import React, { Component } from 'react';
import {
    View,
    Text,
    TextInput,
} from 'react-native';
import { Toolbar } from 'react-native-material-ui';
import { connect } from 'react-redux';
import { addContact, editContact } from '../actions/contacts';

import LinkContactModal from '../components/LinkContactModal';

import styles from '../styles/common';


class EditContactScreen extends Component {
    constructor(props) {
        super(props);

        this.state = {
            linkModalVisible: false,
            name: this.props.contact && this.props.contact.name || '',
            user: this.props.contact && this.props.contact.user || null,
        };
    }

    done() {
        if (this.state.name.trim().length == 0) {
            return;
        }

        const { params } = this.props.navigation.state;
        const { goBack } = this.props.navigation;
        if (params.mode == 'add') {
            if (this.props.add) {
                this.props.add(this.state.name, this.state.user);
                goBack();
            }
        }
        else if (params.mode == 'edit') {
            if (this.props.edit) {
                this.props.edit(this.state.name, this.state.user);
                goBack();
            }
        }
    }

    render() {
        const { goBack } = this.props.navigation;
        const { params } = this.props.navigation.state;

        return (
            <View>
                <Toolbar
                    centerElement={ params.mode == 'add' ? 'Add contact' : 'Edit contact' }
                    leftElement='arrow-back'
                    rightElement={['link', 'check']}
                    onLeftElementPress={() => goBack(null)}
                    onRightElementPress={(e) => {
                        if (e.index == 0) {
                            this.setState({ linkModalVisible: true });
                        }
                        else if (e.index == 1) {
                            this.done();
                        }
                    }}
                />

                <View style={styles.formGroup}>
                    <Text>Name</Text>
                    <TextInput
                        autoCapitalize='words'
                        autoCorrect={true}
                        autoFocus={true}
                        defaultValue={this.props.contact && this.props.contact.name}
                        onChangeText={(name) => this.setState({name})}
                    />
                </View>

                {this.props.user && (
                    <View style={styles.formGroup}>
                        <Text> Linked with paiso user: {this.props.user.username}</Text>
                    </View>
                )}

                <LinkContactModal
                    visible={this.state.linkModalVisible}
                    selectUser={(user) => this.setState({ user: user })}
                    onRequestClose={() => this.setState({ linkModalVisible: false })}
                />
            </View>
        );
    }
}


const mapStateToProps = (state, ownProps) => {
    const { params } = ownProps.navigation.state;

    let contact = (params.mode == 'edit') ? state.contacts.find(c => c.id == params.contactId) : undefined;

    return {
        contact: contact,
        user: contact && contact.user && state.users.find(u => u.id == contact.user),
    };
};


const mapDispatchToProps = (dispatch, ownProps) => {
    const { params } = ownProps.navigation.state;

    return {
        add: (name, user) => dispatch(addContact(name, user)),
        edit: (name, user) => dispatch(editContact(params.contactId, name, user)),
    };
};


export default connect(mapStateToProps, mapDispatchToProps)(EditContactScreen);

