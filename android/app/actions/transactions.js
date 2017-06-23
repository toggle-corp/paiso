export const addTransaction = (title, amount, contact, transactionType, user,
    approvalStatus='pending', deleted=false,
    id=null, createdAt=null, editedAt=null, acknowledgedAt=null, status='edited') => ({
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
        acknowledgedAt: acknowledgedAt,
        status: status,
    });

export const editTransaction = (id, title, amount, contact, transactionType, user,
    approvalStatus=null, deleted=false,
    newId=null, createdAt=null, editedAt=null, acknowledgedAt=null, status='edited') => ({
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
        acknowledgedAt: acknowledgedAt,
        status: status,
    });

export const acceptTransaction = (id) => ({
    type: 'ACCEPT_TRANSACTION',
    id,
});

export const rejectTransaction = (id) => ({
    type: 'REJECT_TRANSACTION',
    id,
});

export const approveTransaction = (id) => ({
    type: 'APPROVE_TRANSACTION',
    id,
});

export const clearTransactions = () => ({
    type: 'CLEAR_TRANSACTIONS',
});
