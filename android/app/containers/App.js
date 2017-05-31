import { StackNavigator } from 'react-navigation';
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

export default App;

