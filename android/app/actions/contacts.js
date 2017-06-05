export const addContact = (name, user=null, id=null, createdAt=null, editedAt=null, status='edited') => ({
    id: id,
    type: 'ADD_CONTACT',
    name: name,
    user: user,
    createdAt: createdAt,
    editedAt: editedAt,
    status: status,
});

export const editContact = (id, name, user=null, newId=null, createdAt=null, editedAt=null, status='edited') => ({
    type: 'EDIT_CONTACT',
    id: id,
    name: name,
    user: user,
    newId: newId,
    createdAt: createdAt,
    editedAt: editedAt,
    status: status,
});

export const clearContacts = () => ({
    type: 'CLEAR_CONTACTS',
});
