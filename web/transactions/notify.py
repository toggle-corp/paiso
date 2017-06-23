from fcm.models import FcmToken
from config import SERVER_KEY

import requests


def generate_notification_for(transaction):
    if not transaction.contact.user:
        return

    tokens = FcmToken.objects.filter(user=transaction.contact.user)

    url = 'https://fcm.googleapis.com/fcm/send'

    headers = {
        'Content-Type': 'application/json',
        'Authorization': 'key=' + SERVER_KEY,
    }

    action = 'added'
    if transaction.acknowledged_at:
        action = 'edited'
    if transaction.deleted:
        action = 'deleted'

    body = {
        'registration_ids': [t.token for t in tokens],
        'prority': 'high',
        'notification': {
            'title': transaction.title,
            'body': '{}, {} by {}'.format(
                transaction.amount,
                action,
                transaction.user.get_full_name()),
            'sound': 'default',
        },
    }

    requests.post(url, json=body, headers=headers)
