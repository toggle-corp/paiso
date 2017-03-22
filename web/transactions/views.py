from django.shortcuts import render
from django.views.generic import View, TemplateView
from django.db.models import Q
from django.core.exceptions import ObjectDoesNotExist
from django.utils import dateformat
from django.utils.decorators import method_decorator
from django.views.decorators.csrf import csrf_exempt

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
    @method_decorator(csrf_exempt)
    def dispatch(self, request, *args, **kwargs):
        return super(UserView, self).dispatch(request, *args, **kwargs)

    def get(self, request):
        users = User.objects.all()

        userId = request.GET.get('userId')
        if userId:
            users = users.filter(pk=userId)

        return JsonResult(data={'users': [user.serialize() for user in users]})

    def post(self, request):
        data_in = get_json_request(request)
        if data_in is None:
            return INVALID_JSON_REQUEST
        return JsonResult({'user': User.deserialize(data_in).serialize()})


# /api/v1/contact/
# Contacts for a user
class ContactView(View):
    @method_decorator(csrf_exempt)
    def dispatch(self, request, *args, **kwargs):
        return super(ContactView, self).dispatch(request, *args, **kwargs)

    def get(self, request):
        user, error = get_user(request.GET)
        if error:
            return error

        contacts = Contact.objects.filter(user=user)

        contact_id = request.GET.get('contactId')
        if contact_id:
            contacts = contacts.filter(pk=contact_id)

        return JsonResult(data={'contacts': [contact.serialize() for contact in contacts]})

    def post(self, request):
        data_in = get_json_request(request)
        if data_in is None:
            return INVALID_JSON_REQUEST

        user, error = get_user(data_in)
        if error:
            return error

        if data_in.get('items'):
            result = []
            for item in data_in.get('items'):
                result.append({'contact': Contact.deserialize(item).serialize()})
            return JsonResult({'items': result})
        else:
            return JsonResult({'contact': Contact.deserialize(data_in).serialize()})


# /api/v1/transaction/
# Transactions for a user
class TransactionView(View):
    @method_decorator(csrf_exempt)
    def dispatch(self, request, *args, **kwargs):
        return super(TransactionView, self).dispatch(request, *args, **kwargs)

    def get(self, request):
        user, error = get_user(request.GET)
        if error:
            return error

        transactions = Transaction.objects.filter(Q(user=user)|Q(contact__linked_user=user))

        transaction_id = request.GET.get('transactionId')
        if transaction_id:
            transactions = transactions.filter(pk=transaction_id)

        data = {'transactions': []}
        users = []
        for transaction in transactions:
            transactionData = transaction.serialize()

            if request.GET.get('data') == '1':
                transactionData['data'] = [dt.serialize() for dt in transaction.get_history()]

            data['transactions'].append(transactionData)
            if transaction.user != user:
                users.append(transaction.user)

        if request.GET.get('users') == '1':
            users = list(set(users))
            data['users'] = [user.serialize() for user in users]

        return JsonResult(data=data)

    def post(self, request):
        data_in = get_json_request(request)
        if data_in is None:
            return INVALID_JSON_REQUEST

        user, error = get_user(data_in)
        if error:
            return error

        if data_in.get('deleted') and data_in.get('transactionId'):
            Transaction.objects.filter(pk=data_in['transactionId']).delete()
            return JsonResult({'deleted': True})
        else:
            transaction = Transaction.deserialize(data_in)
            if transaction and data_in.get('data'):
                for dataItem in data_in['data']:
                    dataItem['transactionId'] = transaction.pk
                    TransactionData.deserialize(dataItem)

            return JsonResult({'transaction': transaction.serialize()})



# /api/v1/transaction-data/
# Transaction history for a transaction
class TransactionDataView(View):
    @method_decorator(csrf_exempt)
    def dispatch(self, request, *args, **kwargs):
        return super(TransactionDataView, self).dispatch(request, *args, **kwargs)

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
            return JsonError('Transaction with transaction id "{}" does not exist'.format(transaction_id))

        return JsonResult(data={'data': [dt.serialize() for dt in transaction.get_history()]})

    def post(self, request):
        data_in = get_json_request(request)
        if data_in is None:
            return INVALID_JSON_REQUEST

        user, error = get_user(data_in)
        if error:
            return error

        transaction_id = data_in.get('transactionId')
        if not transaction_id:
            return JsonError('transaction parameter not sent')

        try:
            transaction = Transaction.objects.get(pk=transaction_id)
        except ObjectDoesNotExist:
            return JsonError('Transaction with transaction id "{}" does not exist'.format(transaction_id))

        return JsonResult({'data': TransactionData.deserialize(data_in).serialize()})
