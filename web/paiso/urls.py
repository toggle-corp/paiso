"""paiso URL Configuration

https://docs.djangoproject.com/en/1.10/topics/http/urls/
"""

from django.conf.urls import url, include
from django.contrib import admin
from django.views.generic import TemplateView

from rest_framework import routers
import rest_framework.authtoken.views

import users.views
import transactions.views
import expenses.views
import fcm.views


router = routers.DefaultRouter()
router.register(r'user', users.views.UserViewSet)
router.register(r'contact', users.views.ContactViewSet)
router.register(r'transaction', transactions.views.TransactionViewSet)
router.register(r'expense', expenses.views.ExpenseViewSet)
router.register(r'fcm-token', fcm.views.FcmTokenViewSet)


urlpatterns = [
    url(r'^', include(router.urls)),
    url(r'^privacy$', TemplateView.as_view(template_name="paiso/privacy.html")),
    url(r'^api-auth/', include('rest_framework.urls', namespace='rest_framework')),
    url(r'^api-token-auth/', rest_framework.authtoken.views.obtain_auth_token),

    url(r'^admin/', admin.site.urls),
]
