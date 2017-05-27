import { StackNavigator, } from 'react-navigation';
import UserScreen from './UserScreen.js';
import Dashboard from './Dashboard.js';


const App = StackNavigator({
    Dashboard: { screen: Dashboard },
    UserScreen: { screen: UserScreen },
});

export default App;
