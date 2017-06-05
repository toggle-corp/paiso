import React, { Component } from 'react';
import { AppRegistry, AsyncStorage, View } from 'react-native';

import { createStore, compose, applyMiddleware } from 'redux';
import { persistStore, autoRehydrate } from 'redux-persist';
import { Provider } from 'react-redux';
import thunk from 'redux-thunk';

import App from './app/containers/App';
import reducer from './app/reducers';
import syncManager from './app/utils/syncManager';

const createStoreWithMiddleware = applyMiddleware(thunk)(createStore);
const store = compose(autoRehydrate())(createStoreWithMiddleware)(reducer);


class Paiso extends Component {
    constructor(props) {
        super(props);
        this.state = {
            loaded: false,
        };
    }

    componentWillMount() {
        syncManager.init(store);
        persistStore(store, { storage: AsyncStorage }, () => {
            this.setState({ loaded: true });
        });
    }

    componentWillUnmount() {
        syncManager.finish();
    }

    render() {
        if (!this.state.loaded) {
            return <View></View>;
        }
        return (
            <Provider store={store}>
                <App />
            </Provider>
        );
    }
}


AppRegistry.registerComponent('paiso', () => Paiso);
