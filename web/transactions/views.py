from django.shortcuts import render
from django.views.generic import View, TemplateView
from django.db.models import Q
from django.core.exceptions import ObjectDoesNotExist
from django.utils import dateformat

import json
import datetime

from paiso.utils import *
from transactions.models import *


class HomeView(View):
    def get(self, request):
        return render(request, 'paiso/home.html')


# /api/v1/user/
# User
class UserView(View):
    def get(self, request):
        users = User.objects.all()

        user_id = request.GET.get('userId')
        if user_id:
            users = users.filter(user_id=user_id)

        data = []
        for user in users:
            data.append({
                'userId': user.user_id,
                'displayName': user.display_name,
                'email': user.email,
                'photoUrl': user.photo_url,
            })

        return JsonResult(data=data)

    def post(self, request):
        data_in = get_json_request(request)
        if data_in is None:
            return INVALID_JSON_REQUEST

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
            'created': created, 'userId': user.user_id if created else None
        })


# /api/v1/contact/
# Contacts for a user
class ContactView(View):
    def get(self, request):
        user, error = get_user(request.GET)
        if error:
            return error

        contacts = Contact.objects.filter(belongs_to=user)

        contact_id = request.GET.get('contactId')
        if contact_id:
            contacts = contacts.filter(pk=contact_id)

        data = []
        for contact in contacts:
            data.append({
                'contactId': contact.pk,
                'displayName': contact.display_name,
                'email': contact.email,
                'photoUrl': contact.photo_url
            })

        return JsonResult(data=data)

    def post(self, request):
        data_in = get_json_request(request)
        if data_in is None:
            return INVALID_JSON_REQUEST

        user, error = get_user(data_in)
        if error:
            return error

        contact, created = Contact.objects.update_or_create(
            pk=data_in['contactId'],
            defaults={
                'pk': data_in['contactId'],
                'belongs_to': user,
                'display_name': data_in['displayName'],
                'email': data_in['email'],
                'photo_url': data_in['photoUrl']
            }
        )

        return JsonResult({
            'created': created, 'contactId': contact.pk if created else None
        })


# /api/v1/transaction/
# Transactions for a user
class TransactionView(View):
    def get(self, request):
        user, error = get_user(request.GET)
        if error:
            return error

        transactions = Transaction.objects.filter(Q(to=user)|Q(by=user))

        transaction_id = request.GET.get('transactionId')
        if transaction_id:
            transactions = transactions.filter(pk=transaction_id)

        data = []
        for transaction in transactions:
            transactionData = {
                'transactionId': transaction.pk,
                'to': transaction.to.user_id if transaction.to else None,
                'by': transaction.by.user_id if transaction.by else None,
                'addedBy': transaction.added_by.user_id,
                'unregisteredContact': transaction.unregistered_contact.pk if transaction.unregistered_contact else None,
            }

            if request.GET.get('data') == '1':
                transactionData['data'] = [
                    {
                        'dataId': info.pk,
                        'title': info.title,
                        'amount': info.amount,
                        'approved': info.approved,
                        'timestamp':  dateformat.format(info.timestamp, 'U'),
                    } for info in transaction.get_history()
                ]

            data.append(transactionData)

        return JsonResult(data=data)

    def post(self, request):
        data_in = get_json_request(request)
        if data_in is None:
            return INVALID_JSON_REQUEST

        user, error = get_user(data_in)
        if error:
            return error

        transaction, created = Transaction.objects.update_or_create(
            pk=data_in['transactionId'],
            defaults={
                'pk': data_in['transactionId'],
                'to': User.objects.get(user_id=data_in['to']) if data_in.get('to') else None,
                'by': User.objects.get(user_id=data_in['by']) if data_in.get('by') else None,
                'unregistered_contact': Contact.objects.get(pk=data_in['unregisteredContact']) if data_in.get('unregisteredContact') else None,
                'added_by': user,
            }
        )

        if created and 'data' in data_in:
            for dataItem in data_in['data']:
                TransactionInformation.objects.update_or_create(
                    pk=dataItem['dataId'],
                    defaults={
                        'pk': dataItem['dataId'],
                        'transaction': transaction,
                        'title': dataItem['title'],
                        'amount': dataItem['amount'],
                        'approved': dataItem['approved'],
                        'timestamp': datetime.fromtimestamp(dataItem['timestamp'])
                    }
                )

        return JsonResult({
            'created': created, 'transactionId': transaction.pk if created else None
        })



# /api/v1/transaction-data/
# Transaction history for a transaction
class TransactionDataView(View):
    def get(self, request):
        user, error = get_user(request.GET)
        if error:
            return error

        transaction_id = request.GET.get('transactionId')
        if not transaction_id:
            return JsonError('transactionId parameter not sent')

        try:
            transaction = Transaction.objects.get(pk=transaction_id)
        except ObjectDoesNotExist:
            return JsonError('Transaction with transactionId "{}" does not exist'.format(transaction_id))

        data = []
        for info in transaction.get_history():
            data.append({
                'dataId': info.pk,
                'title': info.title,
                'amount': info.amount,
                'approved': info.approved,
                'timestamp':  dateformat.format(info.timestamp, 'U'),
            })

        return JsonResult(data=data)

    def post(self, request):
        data_in = get_json_request(request)
        if data_in is None:
            return INVALID_JSON_REQUEST

        user, error = get_user(data_in)
        if error:
            return error

        transaction_id = request.GET.get('transactionId')
        if not transaction_id:
            return JsonError('transactionId parameter not sent')

        try:
            transaction = Transaction.objects.get(pk=transaction_id)
        except ObjectDoesNotExist:
            return JsonError('Transaction with transactionId "{}" does not exist'.format(transaction_id))

        info, created = TransactionInformation.objects.update_or_create(
            pk=data_in['transactionId'],
            defaults={
                'pk': data_in['dataId'],
                'transaction': transaction,
                'title': data_in['title'],
                'amount': data_in['amount'],
                'approved': data_in['approved'],
                'timestamp': datetime.fromtimestamp(data_in['timestamp'])
            }
        )

        return JsonResult({
            'created': created, 'transactionDataId': info.pk if created else None
        })



# TODO: Remove this in favor of all of the above
# /api/v1/party/
# Transaction party
class PartyView(View):
    def get(self, request):
        # First get the active user
        user, error = get_user(request.GET)
        if error:
            return error

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
