import React from 'react';
import {
    View,
    Text,
} from 'react-native';
import PropTypes from 'prop-types';

import styles from '../styles/common';


export default function AmountHeader(props, context) {
    const { primaryColor } = context.uiTheme.palette;
    return (
        <View style={[styles.amountHeader, {backgroundColor: primaryColor}, ]}>
            <Text style={styles.amountHeaderAmount}>{props.amount}</Text>
        </View>
    );
}

AmountHeader.contextTypes = {
    uiTheme: PropTypes.object.isRequired,
};
