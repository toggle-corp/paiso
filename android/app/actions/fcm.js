export const saveToken = (id, token) => ({
    type: 'SAVE_FCM_TOKEN',
    id: id,
    token: token,
});
