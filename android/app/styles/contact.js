import { StyleSheet } from 'react-native';


const styles = StyleSheet.create({
    contact: {
        flexDirection: 'row',
        alignItems: 'center',
        paddingLeft: 16,
        paddingRight: 16,
        paddingTop: 10,
        paddingBottom: 10,
    },
    contactIcon: {
        fontSize: 42,
    },
    contactName: {
        paddingLeft: 10,
        paddingRight: 10,
        fontSize: 18,
    },
    contactInfo: {
        fontSize: 12,
    },
    separator: {
        height: StyleSheet.hairlineWidth,
        backgroundColor: 'rgba(255, 255, 255, 0.1)',
    },
});

export default styles;
