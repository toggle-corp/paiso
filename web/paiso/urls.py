"""paiso URL Configuration

https://docs.djangoproject.com/en/1.10/topics/http/urls/
"""

from django.conf.urls import url, include
from django.contrib import admin

from rest_framework import routers
import rest_framework.authtoken.views

import users.views
import transactions.views


router = routers.DefaultRouter()
router.register(r'user', users.views.UserViewSet)
router.register(r'contact', users.views.ContactViewSet)
router.register(r'transaction', transactions.views.TransactionViewSet)


urlpatterns = [
    url(r'^', include(router.urls)),
    url(r'^api-auth/', include('rest_framework.urls', namespace='rest_framework')),
    url(r'^api-token-auth/', rest_framework.authtoken.views.obtain_auth_token),

    url(r'^admin/', admin.site.urls),
]
