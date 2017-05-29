import React from 'react';
import {
    View,
    TouchableNativeFeedback,
} from 'react-native';

import styles from '../../styles/common';


const touchableBackground =
    TouchableNativeFeedback.SelectableBackgroundBorderless();

export default function ActionButton(props) {
    return (
        <View style={styles.actionButtonContainer}>
            <TouchableNativeFeedback
                background={touchableBackground}
                onPress={props.onPress}>
                <View style={styles.actionButton}>
                    {props.component}
                </View>
            </TouchableNativeFeedback>
        </View>
    );
}
