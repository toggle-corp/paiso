import React, { Component } from 'react';
import {
    ListView,
} from 'react-native';
import ContactTransaction from './ContactTransaction';


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
                renderRow={(data) => <ContactTransaction transaction={data} onSelect={() => this.props.onSelect(data.id)}/>}
            />
        );
    }
}
