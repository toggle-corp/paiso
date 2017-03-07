from django.http import JsonResponse
from django.core.exceptions import ObjectDoesNotExist

import json


class JsonError(JsonResponse):
    def __init__(self, message, status=500):
        super().__init__({
            'status': False, 'message': message
        }, status=status)


class JsonResult(JsonResponse):
    def __init__(self, data, status=200):
        super().__init__({
            'status': True, 'data': data
        }, status=status)


def get_json_request(request):
    try:
        return json.loads(request.body.decode('utf-8'))
    except:
        return None


# Standard responses
INVALID_JSON_REQUEST = JsonError("Not a valid json data")


from transactions.models import *

def get_user(data):
    userId = data.get('userId')
    if not userId:
        return None, JsonError('userId parameter not sent')

    try:
        user = User.objects.get(pk=userId)
    except ObjectDoesNotExist:
        return None, JsonError('User with user id "{}" does not exist'.format(userId))

    return user, None
