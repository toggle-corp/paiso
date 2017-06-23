import { StyleSheet } from 'react-native';


const styles = StyleSheet.create({
    item: {
        padding: 10,
    },

    separator: {
        height: StyleSheet.hairlineWidth,
        backgroundColor: 'rgba(0, 0, 0, 0.1)',
    },

    itemTitle: {
        fontWeight: 'bold',
        fontSize: 16,
        paddingBottom: 4,
    },

    itemDescription: {
        fontSize: 16,
    },
});

export default styles;
