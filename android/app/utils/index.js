export function getAmount(transaction, myId) {
    if (transaction.user == myId) {
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


export function getAcknowledgeStatus(transaction) {
    if (transaction.approvalStatus == 'rejected' || 
        transaction.approvalStatus == 'pending') {
        return null;
    }

    if (transaction.deleted) {
        return 'deleted';
    }

    if (!transaction.acknowledgedAt) {
        return 'added';
    }

    if (transaction.acknowledgedAt < transaction.createdAt) {
        return 'added';
    }

    if (transaction.acknowledgedAt < transaction.editedAt) {
        return 'edited';
    }

    return null;
}


// export const SERVER_URL = 'http://192.168.100.30:8000/';
export const SERVER_URL = 'http://192.168.100.11:8000/';

export function request(path, body=null, method='GET', token=null) {
    let headers = {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
    };

    if (token) {
        headers.Authorization = 'Token ' + token;
    }

    if (body) {
        body = JSON.stringify(body);
    }

    return fetch(SERVER_URL + path, {
        method: method,
        headers: headers,
        body: body,
    }).then(response => response.json());
}
