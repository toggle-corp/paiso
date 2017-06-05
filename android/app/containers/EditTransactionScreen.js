import React, { Component } from 'react';
import {
    View,
    Text,
    TextInput,
    TouchableWithoutFeedback,
} from 'react-native';
import { Toolbar } from 'react-native-material-ui';
import { connect } from 'react-redux';
import Icon from 'react-native-vector-icons/MaterialIcons';

import commonStyles from '../styles/common';
import styles from '../styles/transaction';

import { addTransaction, editTransaction } from '../actions/transactions';


class EditTransactionScreen extends Component {
    constructor(props) {
        super(props);

        this.state = {
            title: props.transaction ? props.transaction.title : '',
            amount: props.transaction ? props.transaction.amount : 0,
            transactionType: props.transaction ? props.transaction.transactionType : 'to',
        };
    }

    toggleType() {
        this.setState({
            transactionType: this.state.transactionType == 'to' ? 'by' : 'to',
        });
    }

    done() {
        if (this.state.title.trim().length == 0) {
            return;
        }

        const { params } = this.props.navigation.state;
        const { goBack } = this.props.navigation;

        if (params.mode == 'add') {
            if (this.props.add) {
                this.props.add(this.state.title, this.state.amount, this.state.transactionType, this.props.myId);
                goBack();
            }
        }
        else if (params.mode == 'edit') {
            if (this.props.edit) {
                this.props.edit(this.state.title, this.state.amount, this.state.transactionType, this.props.myId);
                goBack();
            }
        }
    }

    render() {
        const { params } = this.props.navigation.state;
        const { goBack } = this.props.navigation;
        return (
            <View>
                <Toolbar
                    centerElement={params.mode == 'add' ? 'Add transaction' : 'Edit transaction'}
                    leftElement='arrow-back'
                    rightElement='check'
                    onLeftElementPress={() => goBack(null)}
                    onRightElementPress={() => this.done()}
                />
                <View style={commonStyles.formGroup}>
                    <Text>Title</Text>
                    <TextInput
                        autoCapitalize='words'
                        autoCorrect={true}
                        autoFocus={true}
                        defaultValue={this.state.title}
                        onChangeText={title => this.setState({title})}
                    />
                </View>
                <View style={commonStyles.formGroup}>
                    <Text>Amount</Text>
                    <TextInput
                        keyboardType='numeric'
                        defaultValue={isNaN(this.state.amount) ? '0' : this.state.amount+''}
                        onChangeText={amount => this.setState({amount: parseInt(amount)})}
                    />
                </View>
                <TouchableWithoutFeedback onPress={() => this.toggleType()}>
                    <View style={[styles.transactionType, commonStyles.formGroup]}>
                        <View style={styles.user}>
                            <Icon style={styles.avatar} name="account-circle" />
                            <Text style={styles.userLabel}>You</Text>
                        </View>
                        <View>
                            <Icon style={styles.arrow} name={this.state.transactionType == 'to' ? 'arrow-forward' : 'arrow-back'} />
                        </View>
                        <View style={styles.user}>
                            <Icon style={styles.avatar} name="account-circle" />
                            <Text style={styles.userLabel}>{this.props.contact.name}</Text>
                        </View>
                    </View>
                </TouchableWithoutFeedback>
            </View>
        );
    }
}


const mapStateToProps = (state, ownProps) => {
    const { params } = ownProps.navigation.state;
    return {
        contact: state.contacts.find(c => c.id == params.contactId),
        transaction: (params.mode == 'edit') ? state.transactions.find(t => t.id == params.transactionId) : undefined,
        myId: state.auth.myId,
    };
};


const mapDispatchToProps = (dispatch, ownProps) => {
    const { params } = ownProps.navigation.state;

    return {
        add: (title, amount, type, user) => dispatch(
            addTransaction(title, amount, params.contactId, type, user)),
        edit: (title, amount, type, user) => dispatch(
            editTransaction(params.transactionId, title, amount,
                params.contactId, type, user)),
    };
};


export default connect(mapStateToProps, mapDispatchToProps)(EditTransactionScreen);

