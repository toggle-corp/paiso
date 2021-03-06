import React, { Component } from 'react';
import {
    View,
    ListView,
} from 'react-native';
import { ActionButton, Toolbar } from 'react-native-material-ui';
import Icon from 'react-native-vector-icons/MaterialIcons';
import { connect } from 'react-redux';

import styles from '../styles/contact';
import ContactItem from '../components/ContactItem';


class ContactListScreen extends Component {
    static navigationOptions = {
        tabBarIcon: ({ tintColor }) => (
            <Icon name='contacts' size={24} color={tintColor} />
        ),
    };

    constructor(props) {
        super(props);
        this.ds = new ListView.DataSource({rowHasChanged: (r1, r2) => r1 !== r2});
    }

    render() {
        const { navigate } = this.props.navigation;

        // const contacts = [
        //     { name: 'Bibek Dahal', username: 'bibekdahal', },
        //     { name: 'Aditya Khatri', username: 'adityakhatri47', },
        //     { name: 'Ankit Mehta', username: 'frozenhelium', },
        //     { name: 'Navin Ayer', username: 'thenav56', },
        // ];
        const contacts = this.props.contacts;
        const dataSource = this.ds.cloneWithRows(contacts);

        return (
            <View style={{ flex: 1, backgroundColor: '#616161', }}>
                <Toolbar centerElement='Contacts' leftElement='contacts' />
                <ListView
                    dataSource={dataSource}
                    enableEmptySections={true}
                    renderSeparator={() => <View style={styles.separator} />}
                    renderRow={(data) => (
                        <ContactItem
                            contact={data}
                            onSelect={() => navigate('Contact', { contactId: data.id })}
                        />
                    )}
                />
                <ActionButton onPress={() => navigate('EditContact', { mode: 'add' })} />
            </View>
        );
    }
}


const mapStateToProps = (state) => {
    return {
        contacts: state.contacts.map(contact => ({
            id: contact.id,
            name: contact.name,
            username: undefined // TODO
        })),
    };
};


export default connect(mapStateToProps)(ContactListScreen);

