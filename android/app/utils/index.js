export function getAmount(transaction) {
    if (transaction.addedBy == 'self') {
        if (transaction.transactionType == 'to') {
            return transaction.amount;
        } else {
            return -transaction.amount;
        }
    }
    else {
        if (transaction.transactionType == 'by') {
            return transaction.amount;
        } else {
            return -transaction.amount;
        }
    }
}
