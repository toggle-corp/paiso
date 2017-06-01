import React from 'react';
import { StackNavigator } from 'react-navigation';
import { COLOR, ThemeProvider } from 'react-native-material-ui';

import HomeScreen from './HomeScreen';
import ContactScreen from './ContactScreen';
import EditContactScreen from './EditContactScreen';
import EditTransactionScreen from './EditTransactionScreen';


const App = StackNavigator({
    Home: { screen: HomeScreen },
    Contact: { screen: ContactScreen },
    EditContact: { screen: EditContactScreen },
    EditTransaction: { screen: EditTransactionScreen },
}, {
    initialRouteName: 'Home',
    navigationOptions: {
        header: null,
    },
});

const uiTheme = {
    palette: {
        primaryColor: COLOR.blue600,
    },
    toolbar: {
        container: {
            height: 56,
        },
    },
};



export default function ThemedApp() {
    return (
        <ThemeProvider uiTheme={uiTheme}>
            <App />
        </ThemeProvider>
    );
}
