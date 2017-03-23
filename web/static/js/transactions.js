let transactions = {
    init: function($scope, data) {
        $scope.transactions = data.transactions;
        $scope.contacts = data.contacts;
        $scope.users = data.users;

        this.refreshTransactionsData($scope);
        this.refreshContactsData($scope);
    },

    refreshTransactionsData: function($scope) {
        for (let t=0; t<$scope.transactions.length; t++) {
            let transaction = $scope.transactions[t];

            // Get latest title and amount of transaction
            if (transaction.data.length > 0) {
                transaction.title = transaction.data[0].title;
                transaction.amount = (transaction.transactionType == 'to') ? transaction.data[0].amount : -transaction.data[0].amount;
                transaction.timestamp = transaction.data[0].timestamp;
            } else {
                // This shouldn't really be happening
                transaction.title = "Untitled";
                transaction.amount = 0;
                transaction.timestamp = 0;
            }
        }
    },

    refreshContactsData: function($scope) {
        // Get contact for each transaction
        for (let t=0; t<$scope.transactions.length; t++) {
            let transaction = $scope.transactions[t];
            let contact;

            if (transaction.userId == $scope.userId) {
                contact = $scope.contacts.find(c => c.contactId == transaction.contactId);
            } else {
                contact = $scope.contacts.find(c => c.linkedUserId == transaction.userId);
            }

            // Update photoUrl since contact photo is stored in android device only
            let user = $scope.users.find(u => u.userId == contact.linkedUserId);
            if (user) {
                contact.photoUrl = user.photoUrl ? user.photoUrl : '/static/img/default-avatar.png';
            } else {
                contact.photoUrl = '/static/img/default-avatar.png';
            }

            // Update amount and transactions for this contact
            if (!contact.transactions) {
                contact.transactions = [];
                contact.amount = 0;
                contact.timestamp = 0;
            }
            contact.transactions.push(transaction);
            contact.amount += transaction.amount;
            contact.timestamp = Math.max(transaction.timestamp, contact.timestamp);

            contact.transactions.sort((s1, s2) => s2.timestamp - s1.timestamp);
        }

        // Sort contacts by transaction timestamp
        $scope.contacts.sort((c1, c2) => c2.timestamp - c1.timestamp)
    },
};
