import React, { Component } from 'react';
import {
    View,
    Text,
    ListView,
    TouchableNativeFeedback,
} from 'react-native';


function UserItem(props) {
    return (
        <TouchableNativeFeedback
            background={TouchableNativeFeedback.SelectableBackground()}
            onPress={props.onPress}>
            <View>
                <Text>{props.name}</Text>
                <Text>{props.amount}</Text>
            </View>
        </TouchableNativeFeedback>
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
                renderRow={(user) => <UserItem onPress={() => this.props.onSelect(user)} name={user.name} amount={user.amount} />}
            />
        );
    }
}
