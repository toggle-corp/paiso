import React, { Component } from 'react';
import {
    View,
    ListView,
} from 'react-native';
import ContactTransaction from './ContactTransaction';
import styles from '../styles/dashboard';


export default class ContactTransactionList extends Component {
    constructor(props) {
        super(props);

        const ds = new ListView.DataSource({rowHasChanged: (r1, r2) => r1 !== r2});
        this.state = {
            ds: ds,
        };
    }


    render() {
        const dataSource = this.state.ds.cloneWithRows(this.props.transactions);
        return (
            <ListView
                dataSource={dataSource}
                enableEmptySections={true}
                renderSeparator={() => <View style={styles.separator}/>}
                renderRow={(data) => <ContactTransaction transaction={data} onSelect={() => this.props.onSelect(data.id)}/>}
            />
        );
    }
}
