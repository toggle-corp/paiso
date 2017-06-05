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

export const SERVER_URL = 'http://192.168.100.30:8000/';
// export const SERVER_URL = 'http://192.168.100.11:8000/';

export function request(path, body=null, method='GET', token=null) {
    let headers = {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
    };

    if (token) {
        headers['Authorization'] = 'Token ' + token;
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
