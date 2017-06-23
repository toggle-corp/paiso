import React from 'react';
import { StackNavigator } from 'react-navigation';
import { COLOR, ThemeProvider } from 'react-native-material-ui';

import SplashScreen from './SplashScreen';
import LoginScreen from './LoginScreen';
import HomeScreen from './HomeScreen';
import ContactScreen from './ContactScreen';
import EditContactScreen from './EditContactScreen';
import EditTransactionScreen from './EditTransactionScreen';


const App = StackNavigator({
    Splash: { screen: SplashScreen },
    Login: { screen: LoginScreen },
    Home: { screen: HomeScreen },
    Contact: { screen: ContactScreen },
    EditContact: { screen: EditContactScreen },
    EditTransaction: { screen: EditTransactionScreen },
}, {
    initialRouteName: 'Splash',
    navigationOptions: {
        header: null,
    },
});

const uiTheme = {
    palette: {
        primaryColor: '#424242',
        primaryDarkColor: '#616161',
        accentColor: '#FFC107',
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
