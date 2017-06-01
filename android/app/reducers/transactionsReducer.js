const getUniqueId = (transactions) => {
    let id = -1;
    while (transactions.find(t => t.id == id)) {
        --id;
    }
    return id;
};


export default function transactionsReducer(state=[], action) {
    switch (action.type) {
        case 'ADD_TRANSACTION':
            return [
                ...state,
                {
                    id: getUniqueId(state),
                    title: action.title,
                    amount: action.amount,
                    contact: action.contact,
                    transactionType: action.transactionType,
                    addedBy: 'self',
                    createdAt: new Date(),
                    editedAt: new Date(),
                    status: 'new',
                }
            ];
        case 'EDIT_TRANSACTION':
            return state.map(transaction => {
                if (transaction.id != action.id) {
                    return transaction;
                }

                return Object.assign({}, transaction, {
                    title: action.title,
                    amount: action.amount,
                    contact: action.contact,
                    transactionType: action.transactionType,
                    editedAt: new Date(),
                    status: (transaction.status == 'new' ? 'new' : 'edited'),
                });
            });
        default:
            return state;
    }
}
