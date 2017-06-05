import { StyleSheet } from 'react-native';


const styles = StyleSheet.create({
    transaction: {
        flexDirection: 'row',
        alignItems: 'center',
        padding: 8,
    },
    transactionIcon: {
        fontSize: 42,
        paddingLeft: 8,
        paddingRight: 8,
    },
    transactionLabel: {
        marginRight: 'auto',
    },
    transactionName: {
        fontSize: 16,
    },
    transactionInfo: {
        fontSize: 12,
    },
    transactionAmount: {
        fontSize: 18
    }
});

export default styles;
