export const addTransaction = (title, amount, contact, transactionType) => ({
    type: 'ADD_TRANSACTION',
    title: title,
    amount: amount,
    contact: contact,
    transactionType: transactionType,
});

export const editTransaction = (id, title, amount, contact, transactionType) => ({
    type: 'EDIT_TRANSACTION',
    id: id,
    title: title,
    amount: amount,
    contact: contact,
    transactionType: transactionType,
});
