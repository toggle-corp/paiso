from django.shortcuts import render
from django.views.generic import View, TemplateView
from django.db.models import Q
from django.core.exceptions import ObjectDoesNotExist
from django.utils import dateformat

import json
from paiso.utils import *

from transactions.models import *


class HomeView(View):
    def get(self, request):
        return render(request, 'paiso/home.html')


# /api/v1/user/
# User
class UserView(View):
    def get(self, request):
        return JsonError('API not implemented yet')

    def post(self, request):
        data_in = get_json_request(request)
        if data_in is None:
            return INVALID_JSON_REQUEST

        # Create or update user with given userId
        user, created = User.objects.update_or_create(
            user_id=data_in['userId'],
            defaults={
                'user_id': data_in['userId'],
                'display_name': data_in['displayName'],
                'email': data_in['email'],
                'photo_url': data_in['photoUrl']
            }
        )

        return JsonResult({
            'created': created, 'userPk': user.pk
        })


# /api/v1/party/
# Transaction party
class PartyView(View):
    def get(self, request):
        # First get the active user
        if 'userId' not in request.GET:
            return JsonError('userId parameter not sent')
        user_id = request.GET['userId']

        try:
            user = User.objects.get(user_id=user_id)
        except ObjectDoesNotExist:
            return JsonError('user with userId "{}" does not exist'.format(user_id))

        # Get all parties involved in transactions with the user
        parties = {}
        for transaction in Transaction.objects.filter(Q(to=user)|Q(by=user)):
            other, unregistered = transaction.get_other(user)

            # Add a party
            unique_key = str(other.pk) + ':' + str(unregistered)
            if unique_key not in parties:
                parties[unique_key] = {
                    'user_id': other.get_id(),
                    'displayName': other.display_name,
                    'email': other.email,
                    'photoUrl': other.photo_url,
                    'amount': 0,
                    'unregistered': unregistered,
                    'transactions': []
                }

            # Add transaction for the party
            parties[unique_key]['amount'] += transaction.get_amount()
            transaction_object = {
                'transactionId': transaction.pk,
                'isOwner': transaction.added_by == user,
                'history': []
            }
            parties[unique_key]['transactions'].append(transaction_object)

            # History of the transaction
            for info in transaction.get_history():
                transaction_object['history'].append({
                    'title': info.title,
                    'timestamp': dateformat.format(info.timestamp, 'U'),
                    'amount': info.amount,
                    'approved': info.approved
                })

        return JsonResult(list(parties.values()))
