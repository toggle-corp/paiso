export const clearUsers = () => ({
    type: 'CLEAR_USERS',
});

export const saveUser = (id, username, first_name, last_name) => ({
    type: 'SAVE_USER',
    id: id,
    username: username,
    first_name: first_name,
    last_name: last_name,
});
