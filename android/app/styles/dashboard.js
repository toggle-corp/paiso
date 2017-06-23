import { StyleSheet } from 'react-native';


const styles = StyleSheet.create({
    transaction: {
        flexDirection: 'row',
        alignItems: 'center',
        paddingLeft: 16,
        paddingRight: 16,
        paddingTop: 10,
        paddingBottom: 10,
    },
    transactionIcon: {
        fontSize: 42,
    },
    transactionLabel: {
        paddingLeft: 10,
        paddingRight: 10,
        marginRight: 'auto',
    },
    transactionName: {
        fontSize: 18,
    },
    transactionInfo: {
        fontSize: 12,
        color: 'rgba(0, 0, 0, 0.35)',
    },
    transactionAmount: {
        fontSize: 18,
        fontFamily: 'monospace',
    },
    separator: {
        height: StyleSheet.hairlineWidth,
        backgroundColor: 'rgba(255, 255, 255, 0.1)',
    },
});

export default styles;
