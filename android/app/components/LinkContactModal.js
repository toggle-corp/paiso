import React from 'react';
import {
    Modal,
    View,
} from 'react-native';
import { Toolbar } from 'react-native-material-ui';


export default function LinkContactModal(props) {
    return (
        <Modal visible={props.visible} onRequestClose={props.onRequestClose}>
            <View>
                <Toolbar
                    isSearchActive={true}
                    centerElement='Link paiso user'
                    leftElement='arrow-back'
                    onLeftElementPress={props.onRequestClose}
                    searchable={{
                        autoFocus: true,
                        placeholder: 'Search user',
                        onChangeText: (value) => console.log(value),
                    }}
                />
                <View style={{ padding: 16 }}>
                </View>
            </View>
        </Modal>
    );
}
