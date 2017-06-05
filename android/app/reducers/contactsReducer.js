const getUniqueId = (contacts) => {
    let id = -1;
    while (contacts.find(c => c.id == id)) {
        --id;
    }
    return id;
};

export default function contactsReducer(state=[], action) {
    switch (action.type) {
        case 'ADD_CONTACT':
            return [
                ...state,
                {
                    id: action.id ? action.id : getUniqueId(state),
                    name: action.name,
                    user: action.user,
                    createdAt: action.createdAt ? action.createdAt : new Date(),
                    editedAt: action.editedAt ? action.editedAt : new Date(),
                    status: action.status,
                },
            ];

        case 'EDIT_CONTACT':
            return state.map(contact => {
                if (contact.id != action.id) {
                    return contact;
                }

                return Object.assign({}, contact, {
                    id: action.newId ? action.newId : contact.id,
                    name: action.name,
                    user: action.user,
                    createdAt: action.createdAt ? action.createdAt : contact.createdAt,
                    editedAt: action.editedAt ? action.editedAt : new Date(),
                    status: action.status,
                });
            });

        case 'CLEAR_CONTACTS':
            return [];

        default:
            return state;
    }
}
