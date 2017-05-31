import { StyleSheet } from 'react-native';


const styles = StyleSheet.create({
    amountHeader: {
        height: 64,
        padding: 8,
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'flex-end',
        elevation: 4,
    },
    amountHeaderLabel: {
        color: 'white',
        fontSize: 18,
    },
    amountHeaderAmount: {
        color: 'white',
        fontSize: 24,
    },

    formGroup: {
        padding: 16,
    },
});

export default styles;
