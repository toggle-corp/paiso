import contactsReducer from './contactsReducer.js';
import transactionsReducer from './transactionsReducer.js';
import usersReducer from './usersReducer.js';


const reducers = {
    contacts: contactsReducer,
    transactions: transactionsReducer,
    users: usersReducer,
};

export default reducers;
