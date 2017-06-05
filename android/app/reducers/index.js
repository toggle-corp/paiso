import { combineReducers } from 'redux';

import contactsReducer from './contactsReducer.js';
import transactionsReducer from './transactionsReducer.js';
import usersReducer from './usersReducer.js';
import authReducer from './authReducer.js';


const reducer = combineReducers({
    contacts: contactsReducer,
    transactions: transactionsReducer,
    users: usersReducer,
    auth: authReducer,
});


const rootReducer = (state, action) => {
    if (action.type == 'LOGOUT') {
        state = undefined;
    }
    return reducer(state, action);
};


export default rootReducer;
