var paiso = angular.module('paiso', []);

paiso.controller('mainController',  ['$scope', '$http', function($scope, $http) {

    // CSRF handling for angular post/put/delete requests
    $http.defaults.xsrfCookieName = 'csrftoken';
    $http.defaults.xsrfHeaderName = 'X-CSRFToken';

    // Try signing in
    auth.init($scope, $http).then(function() {
        // We are now signed in
        // Do everything here

        // Get all parties and their transactions
        $http.get(partyApi, { params: { userId: $scope.auth.user.uid } }).then(
            function success(response) {
                $scope.users = response.data.data;
            },
            function error(response) {
                console.log('Failed get transactions for current user');
                console.log(response);
            }
        );

    }).catch(function(errorMessage) {
        console.log(errorMessage);
    });

    // Ankit's code:
    // $scope.firstName= "Ankit";
    // $scope.lastName= "Mehta";

    // $scope.users = [
    //     {
    //         id: 1,
    //         name: "Ankit Mehta",
    //         extra: "frozenhelium@togglecorp.com",
    //         amount: -50000,
    //         photoUrl: "/static/img/default-avatar.png",
    //         transactions: [
    //             {
    //                 id: 1,
    //                 isOwner: true,
    //                 history: [
    //                     {
    //                         title: 'The begining',
    //                         timestamp: 1023449,
    //                         amount: -50000,
    //                         approved: true
    //                     }
    //                 ]
    //             }
    //         ]
    //     },
    //     {
    //         id: 2,
    //         name: "Aditya Khatri",
    //         extra: "adify@noob.com",
    //         amount: 15000,
    //         photoUrl: "/static/img/default-avatar.png",
    //         transactions: [
    //             {
    //                 id: 3,
    //                 isOwner: true,
    //                 history: [
    //                     {
    //                         title: 'okay dood',
    //                         timestamp: 1023449,
    //                         amount: 15000,
    //                         approved: true
    //                     }
    //                 ]
    //             }
    //         ]
    //     },
    //     {
    //         id: 3,
    //         name: "Bibek Dahal",
    //         extra: "bibekdahal.bd16@gmail.com",
    //         amount: -24000,
    //         photoUrl: "/static/img/default-avatar.png",
    //         transactions: [
    //             {
    //                 id: 5,
    //                 isOwner: true,
    //                 history: [
    //                     {
    //                         title: 'bla bla',
    //                         timestamp: 1023449,
    //                         amount: 5000,
    //                         approved: true
    //                     }
    //                 ]
    //             }
    //         ]
    //     },
    //     {
    //         id: 4,
    //         name: "Prabesh Pathak",
    //         extra: "jackyjeddragon@rocketmail.com",
    //         amount: 6000,
    //         photoUrl: "/static/img/default-avatar.png",
    //         transactions: [
    //             {
    //                 id: 11,
    //                 isOwner: true,
    //                 history: [
    //                     {
    //                         title: 'Just test',
    //                         timestamp: 1023449,
    //                         amount: 5000,
    //                         approved: true
    //                     }
    //                 ]
    //             },
    //             {
    //                 id: 12,
    //                 isOwner: true,
    //                 history: [
    //                     {
    //                         title: 'yet another test',
    //                         timestamp: 1023449,
    //                         amount: 1000,
    //                         approved: true
    //                     }
    //                 ]
    //             }
    //         ]
    //     },
    // ];
}]);
