from django.core.management import BaseCommand
from django.contrib.staticfiles import finders
from django.db.models import Q

from transactions.models import *
import paiso.settings

from datetime import datetime
import os
import json


def get_contact(user, other):
    existing = Contact.objects.filter(user=user, linked_user=other)
    if existing.count() > 0:
        return existing[0]

    new_contact = Contact(
        user=user, linked_user=other,
        display_name=other.display_name,
        email=other.email,
        phone=other.phone,
        photo_url=other.photo_url
    )
    new_contact.save()
    return new_contact


class Command(BaseCommand):
    def handle(self, *args, **options):
        path = os.path.join(paiso.settings.BASE_DIR, 'static/old-data/old-data.json')
        data = None

        print('Reading data from file')
        with open(path) as data_file:
            data = json.load(data_file)

        if data:
            print('Data read successfully')

            print('Migrating users')
            users = data['users']
            new_users = {}
            for user_id, user in users.items():
                existing = User.objects.filter(email=user['email'])

                if existing.count() > 0:
                    new_user = existing[0]
                else:
                    new_user = User(
                        display_name=user['displayName'],
                        email=user['email'],
                        photo_url=user['photoUrl']
                    )
                    new_user.save()
                new_users[user_id] = new_user
            print('Done')

            unlinked_contacts = Contact.objects.filter(linked_user=None)
            for contact in unlinked_contacts:
                contact.refresh_linked_users()

            print('Migrating transactions for users')
            for user_id, user in new_users.items():
                if user_id not in data['user_transactions']:
                    continue

                for transaction_id, status in data['user_transactions'][user_id].items():
                    if status:
                        if transaction_id not in data['transactions']:
                            continue

                        transaction = data['transactions'][transaction_id]
                        if transaction['added_by'] == user_id:
                            if not transaction['customUser']:
                                new_transaction = Transaction(user=user)
                                if transaction['to'] == user_id:
                                    new_transaction.transaction_type = 'by'
                                    other = transaction['by']
                                else:
                                    new_transaction.transaction_type = 'to'
                                    other = transaction['to']

                                if other not in new_users:
                                    continue
                                new_transaction.contact = get_contact(user, new_users[other])

                                new_transaction.save()

                                new_data = TransactionData(transaction=new_transaction)
                                new_data.title = transaction['title']
                                new_data.timestamp = datetime.fromtimestamp(int(transaction['date'])/1000)
                                new_data.amount = transaction['amount']
                                new_data.approved = True

                                new_data.save()
            print('Done')
