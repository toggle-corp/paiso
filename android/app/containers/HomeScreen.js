import { TabNavigator } from 'react-navigation';

import Dashboard from './Dashboard';
import ContactListScreen from './ContactListScreen';
import NotificationListScreen from './NotificationListScreen';
import SettingsScreen from './SettingsScreen';


const HomeScreen = TabNavigator({
    Dashboard: { screen: Dashboard },
    Contacts: { screen: ContactListScreen },
    Notifications: { screen: NotificationListScreen },
    Settings: { screen: SettingsScreen },
}, {
    swipeEnabled: false,
    animationEnabled: false,
    tabBarPosition: 'bottom',
    tabBarOptions: {
        showLabel: false,
        indicatorStyle: { display: 'none' },
        showIcon: true,
    },
});

export default HomeScreen;
