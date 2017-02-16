"""paiso URL Configuration
"""
from django.conf.urls import url
from django.contrib import admin

from transactions.views import *

urlpatterns = [
    url(r'^$', HomeView.as_view(), name='home'),

    url(r'^admin/', admin.site.urls),
]
