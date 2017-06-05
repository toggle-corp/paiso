export default function usersReducer(state=[], action) {
    let obj;

    switch (action.type) {
        case 'CLEAR_USERS':
            return [];

        case 'SAVE_USER':
            obj = {
                id: action.id,
                username: action.username,
                first_name: action.first_name,
                last_name: action.last_name,
            };
            if (state.find(u => u.id == action.id)) {
                return state.map(user => {
                    if (user.id != action.id) {
                        return user;
                    }

                    return Object.assign({}, user, obj);
                });
            }
            else {
                return [...state, obj];
            }

        default:
            return state;
    }
}
