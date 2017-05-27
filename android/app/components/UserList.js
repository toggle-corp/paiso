import React, { Component } from 'react';
import {
    View,
    Text,
    ListView,
} from 'react-native';


function UserItem(props) {
    return (
        <View>
            <Text>{props.name}</Text>
            <Text>{props.amount}</Text>
        </View>
    );
}


export default class UserList extends Component {
    constructor(props) {
        super(props);

        const ds = new ListView.DataSource({
            rowHasChanged: (r1, r2) =>
                r1.name !== r2.name || r1.amount !== r2.amount,
        });

        this.state = {
            dataSource: ds.cloneWithRows(props.users),
        };
    }

    componentWillReceiveProps(newProps) {
        this.setState({
            dataSource: this.state.dataSource.cloneWithRows(
                newProps.users),
        });
    }

    render() {
        return (
            <ListView
                dataSource={this.state.dataSource}
                renderRow={(user) => <UserItem name={user.name} amount={user.amount} />}
            />
        );
    }
}
