import React, { Component } from 'react';
import {
    View,
    Text
} from 'react-native';
import Icon from 'react-native-vector-icons/MaterialIcons';


export default class NotificationListScreen extends Component {
    static navigationOptions = {
        tabBarIcon: ({ tintColor }) => (
            <Icon name="notifications" size={24} color={tintColor} />
        ),
    };

    render() {
        return (
            <View>
                <Text>Notifications</Text>
            </View>
        );
    }
}
