import React, { Component } from 'react';
import {
    View
} from 'react-native';
import { NavigationActions } from 'react-navigation';
import { connect } from 'react-redux';


class SplashScreen extends Component {
    componentWillMount() {
        if (this.props.token) {
            const resetAction = NavigationActions.reset({
                index: 0,
                actions: [
                    NavigationActions.navigate({ routeName: 'Home'})
                ]
            });
            this.props.navigation.dispatch(resetAction);
        } else {
            const resetAction = NavigationActions.reset({
                index: 0,
                actions: [
                    NavigationActions.navigate({ routeName: 'Login'})
                ]
            });
            this.props.navigation.dispatch(resetAction);
        }
    }

    render() {
        return (
            <View></View>
        );
    }
}


const mapStateToProps = (state) => ({
    token: state.auth.token,
});

export default connect(mapStateToProps)(SplashScreen);

