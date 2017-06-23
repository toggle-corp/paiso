import { StyleSheet } from 'react-native';


const styles = StyleSheet.create({
    amountHeader: {
        height: 96,
        padding: 16,
        flexDirection: 'row',
        justifyContent: 'flex-end',
        alignItems: 'flex-end',
        elevation: 4,
    },
    amountHeaderLabel: {
        color: 'white',
        fontSize: 18,
    },
    amountHeaderAmount: {
        color: 'white',
        fontSize: 36,
        fontFamily: 'monospace',
    },

    formGroup: {
        padding: 16,
    },
});

export default styles;
