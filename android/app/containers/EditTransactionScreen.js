import React, { Component } from 'react';
import {
    View,
    Text,
    TextInput,
} from 'react-native';
import { Toolbar } from 'react-native-material-ui';
import { connect } from 'react-redux';
import { addTransaction, editTransaction } from '../actions/transactions';

import styles from '../styles/common';


class EditTransactionScreen extends Component {
    constructor(props) {
        super(props);

        this.state = {
            title: props.transaction ? props.transaction.title : '',
            amount: props.transaction ? props.transaction.amount : 0,
        };
    }

    done() {
        if (this.state.title.trim().length == 0) {
            return;
        }

        const { params } = this.props.navigation.state;
        const { goBack } = this.props.navigation;

        if (params.mode == 'add') {
            if (this.props.add) {
                this.props.add(this.state.title, this.state.amount, 'to');
                goBack();
            }
        }
        else if (params.mode == 'edit') {
            if (this.props.edit) {
                this.props.edit(this.state.title, this.state.amount, 'to');
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
                <View style={styles.formGroup}>
                    <Text>Title</Text>
                    <TextInput
                        autoCapitalize='words'
                        autoCorrect={true}
                        autoFocus={true}
                        defaultValue={this.state.title}
                        onChangeText={title => this.setState({title})}
                    />
                </View>
                <View style={styles.formGroup}>
                    <Text>Amount</Text>
                    <TextInput
                        keyboardType='numeric'
                        defaultValue={isNaN(this.state.amount) ? '0' : this.state.amount+''}
                        onChangeText={amount => this.setState({amount: parseInt(amount)})}
                    />
                </View>
            </View>
        );
    }
}


const mapStateToProps = (state, ownProps) => {
    const { params } = ownProps.navigation.state;
    return {
        transaction: (params.mode == 'edit') ?
            state.transactions.find(t => t.id == params.transactionId) : undefined,
    };
};


const mapDispatchToProps = (dispatch, ownProps) => {
    const { params } = ownProps.navigation.state;

    return {
        add: (title, amount, type) => dispatch(
            addTransaction(title, amount, params.contactId, type)),
        edit: (title, amount, type) => dispatch(
            editTransaction(params.transactionId, title, amount,
                params.contactId, type)),
    };
};


export default connect(mapStateToProps, mapDispatchToProps)(EditTransactionScreen);

