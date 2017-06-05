import React, { Component } from 'react';
import {
    Modal,
    View,
    Text,
    ListView,
    TouchableNativeFeedback,
} from 'react-native';
import { Toolbar } from 'react-native-material-ui';
import Icon from 'react-native-vector-icons/MaterialIcons';

import { request } from '../utils';
import styles from '../styles/search';


function SearchUserItem(props) {
    return (
        <TouchableNativeFeedback onPress={props.onSelect}>
            <View style={styles.searchItem}>
                <Icon name='account-circle' style={styles.icon}/>
                <View style={styles.itemLabel}>
                    <Text style={styles.name}>{props.user.first_name} {props.user.last_name}</Text>
                    <Text style={styles.info}>{props.user.username}</Text>
                </View>
            </View>
        </TouchableNativeFeedback>
    );
}


export default class LinkContactModal extends Component {
    constructor(props) {
        super(props);

        this.ds = new ListView.DataSource({rowHasChanged: (r1, r2) => r1 != r2});
        this.state = {
            dataSource: this.ds.cloneWithRows([]),
        };
    }

    search(query) {
        query = query.trim();
        if (!query) {
            this.setState({
                dataSource: this.ds.cloneWithRows([]),
            });
            return;
        }

        request('user/?q=' + encodeURIComponent(query), null, 'GET')
            .then(json => {
                this.setState({
                    dataSource: this.ds.cloneWithRows(json),
                });
            });
    }

    select(data) {
        this.props.selectUser(data);
        this.props.onRequestClose();
    }

    render() {
        return (
            <Modal visible={this.props.visible} onRequestClose={this.props.onRequestClose}>
                <View>
                    <Toolbar
                        isSearchActive={true}
                        centerElement='Link paiso user'
                        leftElement='arrow-back'
                        onLeftElementPress={this.props.onRequestClose}
                        searchable={{
                            autoFocus: true,
                            placeholder: 'Search user',
                            onChangeText: query => this.search(query),
                        }}
                    />
                    <ListView
                        enableEmptySections={true}
                        dataSource={this.state.dataSource}
                        renderRow={data => <SearchUserItem onSelect={() => this.select(data.pk)} user={data}/>}
                    />
                </View>
            </Modal>
        );
    }
}
