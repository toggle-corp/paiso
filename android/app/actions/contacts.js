export const addContact = (name, user=null) => ({
    type: 'ADD_CONTACT',
    name: name,
    user: user,
});

export const editContact = (id, name, user) => ({
    type: 'EDIT_CONTACT',
    id: id,
    name: name,
    user: user,
});
