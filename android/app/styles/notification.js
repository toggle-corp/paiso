import { StyleSheet } from 'react-native';

const styles = StyleSheet.create({
    notification: {
        marginLeft: 8,
        marginRight: 8,
        marginTop: 4,
        marginBottom: 4,
        backgroundColor: '#fff',
        elevation: 2,
    },

    separator: {
        // backgroundColor: 'rgba(0, 0, 0, 0.1)',
        height: StyleSheet.hairlineWidth,
    },

    content: {
        flexDirection: 'row',
        alignItems: 'center',
        paddingLeft: 16,
        paddingRight: 16,
        paddingTop: 10,
        paddingBottom: 8,
    },

    contactIcon: {
        fontSize: 48,
    },

    labelContainer: {
        marginRight: 'auto',
        paddingLeft: 10,
        paddingRight: 10,
    },

    transactionType: {
        paddingRight: 4,
        fontSize: 12,
    },

    contactName: {
        fontWeight: 'bold',
        fontSize: 12,
    },

    transactionTitle: {
        fontSize: 18,
    },

    amount: {
        fontSize: 21,
        fontFamily: 'monospace',
    },

    actionButtons: {
        flexDirection: 'row',
    },

    info: {
        fontSize: 12,
        color: 'rgba(0, 0, 0, 0.35)',
    },

    footer: {
        flexDirection: 'row',
        justifyContent: 'flex-end',
        alignItems: 'center',
    },
});

export default styles;
