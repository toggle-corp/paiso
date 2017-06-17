import { combineReducers } from 'redux';

import contactsReducer from './contactsReducer.js';
import transactionsReducer from './transactionsReducer.js';
import usersReducer from './usersReducer.js';
import authReducer from './authReducer.js';
import fcmReducer from './fcmReducer.js';


const reducer = combineReducers({
    contacts: contactsReducer,
    transactions: transactionsReducer,
    users: usersReducer,
    auth: authReducer,
    fcm: fcmReducer,
});


const rootReducer = (state, action) => {
    if (action.type == 'LOGOUT') {
        const fcm = state.fcm;
        state = { fcm };
    }
    return reducer(state, action);
};


export default rootReducer;
