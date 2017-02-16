from django.shortcuts import render
from django.views.generic import View, TemplateView

import json
from paiso.utils import *


class HomeView(View):
    def get(self, request):
        return render(request, 'paiso/home.html')


# /api/v1/user/
# User
class UserView(View):
    def get(self, request):
        return JsonError('API not implemented yet')

    def post(self, request):
        data_in = get_json_request()

        # Create or update user with given userId
        user, created = User.objects.update_or_create(
            user_id=data_in['userId'],
            defaults={
                'user_id': data_in['userId'],
                'display_name': data_in['displayName'],
                'email': data_in['email'],
                'user_id': data_in['userId'],
                'photo_url': data_in['photoUrl']
            }
        )

        return JsonResult({
            'result': 'success', 'created': created, 'userId': user.pk

        
        })


# /api/v1/party/
# Transaction party
class PartyView(View):
    def get(self, request):
        if "userId" not in request.GET:
            return JsonError('userId parameter not sent');

        
        return JsonResult(data_out)
