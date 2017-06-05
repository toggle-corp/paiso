export default function authReducer(state={}, action) {
    switch(action.type) {
        case 'SAVE_TOKEN':
            return Object.assign({}, state, {
                token: action.token,
            });
        case 'LOGOUT':
            return {
                myId: undefined,
                token: undefined,
            };
        case 'SAVE_MY_ID':
            return Object.assign({}, state, {
                myId: action.id,
            });
        default:
            return state;
    }
}
