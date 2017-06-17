export const addTransaction = (title, amount, contact, transactionType, user,
    approvalStatus='pending', deleted=false,
    id=null, createdAt=null, editedAt=null, status='edited') => ({
        id: id,
        type: 'ADD_TRANSACTION',
        title: title,
        amount: amount,
        contact: contact,
        transactionType: transactionType,
        user: user,
        approvalStatus: approvalStatus,
        deleted: deleted,
        createdAt: createdAt,
        editedAt: editedAt,
        status: status,
    });

export const editTransaction = (id, title, amount, contact, transactionType, user,
    approvalStatus=null, deleted=false,
    newId=null, createdAt=null, editedAt=null, status='edited') => ({
        type: 'EDIT_TRANSACTION',
        id: id,
        title: title,
        amount: amount,
        contact: contact,
        transactionType: transactionType,
        user: user,
        approvalStatus: approvalStatus,
        deleted: deleted,
        newId: newId,
        createdAt: createdAt,
        editedAt: editedAt,
        status: status,
    });

export const clearTransactions = () => ({
    type: 'CLEAR_TRANSACTIONS',
});
