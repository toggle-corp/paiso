from django.db.models import Q

from fcm.models import FcmToken
from users.models import Contact
from config import SERVER_KEY

import requests


def generate_notification_for(transaction, me):
    if not transaction.contact.user or transaction.status == 'rejected':
        return

    tokens = FcmToken.objects.filter(
        ~Q(user=me),
        user=transaction.contact.user,
        user__contact__user=transaction.user
    )

    url = 'http://fcm.googleapis.com/fcm/send'

    headers = {
        'Content-Type': 'application/json',
        'Authorization': 'key=' + SERVER_KEY,
    }

    contact = Contact.objects.get(
        user=transaction.user,
        belongs_to=transaction.contact.user
    )

    action = 'added'
    if transaction.deleted:
        action = 'deleted'
    elif transaction.acknowledged_at:
        action = 'edited'

    body = {
        'registration_ids': [t.token for t in tokens],
        'data': {
            'id': str(transaction.pk),
            'title': transaction.title,
            'user': contact.name,
            'amount': str(transaction.amount),
            'action': action,
        },
    }

    requests.post(url, json=body, headers=headers)
