import React from 'react';
import {
    View,
    Text,
} from 'react-native';


export default function AmountHeader(props) {
    return (
        <View>
            <Text>Total</Text>
            <Text>{props.amount}</Text>
        </View>
    );
}
