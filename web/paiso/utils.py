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
    user_id = data.get('userId')
    if not user_id:
        return None, JsonError('userId parameter not sent')

    try:
        user = User.objects.get(user_id=user_id)
    except ObjectDoesNotExist:
        return None, JsonError('User with userId "{}" does not exist'.format(user_id))

    return user, None
