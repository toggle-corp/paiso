import { Component } from 'react';
import FCM, { FCMEvent } from 'react-native-fcm';
import { connect } from 'react-redux';

import { saveToken } from '../actions/fcm';


class FcmController extends Component {
    componentDidMount() {
        // FCM.requestPermissions(); // for iOS
        FCM.getFCMToken().then(token => {
            console.log(token);
            this.props.saveToken(this.props.tokenId, token);
        });

        this.notificationListener = FCM.on(FCMEvent.Notification, async (notif) => {
            console.log(notif);
        });

        this.refreshTokenListener = FCM.on(FCMEvent.RefreshToken, token => {
            console.log(token);
            this.props.saveToken(this.props.tokenId, token);
        });
    }

    componentWillUnmount() {
        this.notificationListener.remove();
        this.refreshTokenListener.remove();
    }

    render() {
        return null;
    }
}


const mapStateToProps = (state) => {
    return {
        tokenId: state.fcm.id,
    };
};


const mapDispatchToProps = (dispatch) => {
    return {
        saveToken: (id, token) => dispatch(saveToken(id, token)),
    };
};

export default connect(mapStateToProps, mapDispatchToProps)(FcmController);
