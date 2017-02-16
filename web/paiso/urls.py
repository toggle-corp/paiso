"""paiso URL Configuration
"""
from django.conf.urls import url
from django.contrib import admin

from transactions.views import *

urlpatterns = [
    url(r'^$', HomeView.as_view(), name='home'),

    url(r'^api/v1/user/$', UserView.as_view(), name='user_api'),
    url(r'^api/v1/party/$', PartyView.as_view(), name='party_api'),

    url(r'^admin/', admin.site.urls),
]
