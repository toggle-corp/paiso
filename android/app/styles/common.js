import { StyleSheet } from 'react-native';


const styles = StyleSheet.create({
    actionButtonContainer: {
        position: 'absolute',
        right: 24,
        bottom: 24,
        elevation: 5,
        borderRadius: 50,
    },

    actionButton: {
        borderRadius: 50,
        backgroundColor: '#ff5722',
        width: 56,
        height: 56,
        alignItems: 'center',
        justifyContent: 'center',
    },

    actionButtonText: {
        fontSize: 24,
        color: 'white',
    },
});

export default styles;
