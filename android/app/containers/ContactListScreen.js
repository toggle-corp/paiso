import React, { Component } from 'react';
import {
    View,
    ListView,
} from 'react-native';
import { ActionButton, Toolbar } from 'react-native-material-ui';
import Icon from 'react-native-vector-icons/MaterialIcons';

import ContactItem from '../components/ContactItem';


export default class ContactListScreen extends Component {
    static navigationOptions = {
        tabBarIcon: ({ tintColor }) => (
            <Icon name='contacts' size={24} color={tintColor} />
        ),
    };

    render() {
        const { navigate } = this.props.navigation;

        const contacts = [
            { name: 'Bibek Dahal', username: 'bibekdahal', },
            { name: 'Aditya Khatri', username: 'adityakhatri47', },
            { name: 'Ankit Mehta', username: 'frozenhelium', },
            { name: 'Navin Ayer', username: 'thenav56', },
        ];
        const ds = new ListView.DataSource({rowHasChanged: (r1, r2) => r1 !== r2});
        const dataSource = ds.cloneWithRows(contacts);

        return (
            <View style={{flex: 1}}>
                <Toolbar centerElement='Contacts' leftElement='contacts' />
                <ListView
                    dataSource={dataSource}
                    renderRow={(data) => (
                        <ContactItem
                            contact={data}
                            onSelect={() => navigate('Contact')}
                        />
                    )}
                />
                <ActionButton onPress={() => navigate('EditContact', { mode: 'add' })} />
            </View>
        );
    }
}
