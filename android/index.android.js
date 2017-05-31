
import React, { Component } from 'react';
import { AppRegistry } from 'react-native';

import { createStore, applyMiddleware, combineReducers } from 'redux';
import { Provider } from 'react-redux';
import thunk from 'redux-thunk';

import App from './app/containers/App';
import * as reducers from './app/reducers';

import { COLOR, ThemeProvider } from 'react-native-material-ui';


const createStoreWithMiddleware = applyMiddleware(thunk)(createStore);
const reducer = combineReducers(reducers);
const store = createStoreWithMiddleware(reducer);

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


class Paiso extends Component {
    render() {
        return (
            <Provider store={store}>
                <ThemeProvider uiTheme={uiTheme}>
                    <App />
                </ThemeProvider>
            </Provider>
        );
    }
}


AppRegistry.registerComponent('paiso', () => Paiso);
