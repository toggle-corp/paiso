import { request } from '../utils';

export const saveToken = (token) => ({
    type: 'SAVE_TOKEN',
    token: token,
});

export const logout = () => ({
    type: 'LOGOUT',
});

export const saveMyId = (id) => ({
    type: 'SAVE_MY_ID',
    id: id,
});


function _login(username, password, dispatch) {
    request('api-token-auth/', {
        username: username,
        password: password,
    }, 'POST')
        .then(json => {
            if (json.token) {
                dispatch(saveToken(json.token));
            }
        })
        .catch(error => console.log(error));
}

export const login = (username, password) => dispatch => {
    _login(username, password, dispatch);
};


export const register = (first_name, last_name, username, password) => dispatch => {
    request('user/', {
        first_name: first_name,
        last_name: last_name,
        username: username,
        password: password,
    }, 'POST')
        .then(json => {
            console.log(json);
            _login(username, password, dispatch);
        })
        .catch(error => console.log(error));
};
