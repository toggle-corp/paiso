import { StyleSheet } from 'react-native';

const styles = StyleSheet.create({
    notification: {
    },

    separator: {
        backgroundColor: '#121212',
        height: StyleSheet.hairlineWidth,
    },

    content: {
        backgroundColor: '#fafafa',
        paddingLeft: 16,
        paddingRight: 16,
        paddingTop: 8,
        paddingBottom: 8,
        flexDirection: 'row',
        alignItems: 'center',
    },

    contactIcon: {
        fontSize: 42,
        paddingRight: 8,
    },

    labelContainer: {
        marginRight: 'auto',
    },

    contactName: {
        fontSize: 14,
        fontWeight: 'bold'
    },

    transactionTitle: {
        fontSize: 16,
    },

    amount: {
        fontSize: 18,
    },

    footer: {
        backgroundColor: '#eee',
        justifyContent: 'flex-end',
        flexDirection: 'row',
        paddingLeft: 16,
        paddingRight: 16,
    },
});

export default styles;
