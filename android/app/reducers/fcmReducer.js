export default function fcmReducer(state={ id: null, token: null, }, action) {
    switch(action.type) {
        case 'SAVE_FCM_TOKEN':
            return {
                id: action.id,
                token: action.token,
            };
        default:
            return state;
    }
}
