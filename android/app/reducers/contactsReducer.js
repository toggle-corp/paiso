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
                    id: getUniqueId(state),  // Note while synchronizing to synchronize references as well, like in transactions
                    name: action.name,
                    user: action.user,
                    createdAt: new Date(),
                    editedAt: new Date(),
                    status: 'new',
                },
            ];

        case 'EDIT_CONTACT':
            return state.map(contact => {
                if (contact.id != action.id) {
                    return contact;
                }

                return Object.assign({}, contact, {
                    name: action.name,
                    user: action.user,
                    editedAt: new Date(),
                    status: (contact.status == 'new' ? 'new' : 'edited')
                });
            });

        default:
            return state;
    }
}
