from django.contrib.auth.models import User

from rest_framework import permissions
from rest_framework import viewsets

from users.serializers import UserSerializer, UserPutSerializer,\
        ContactSerializer
from users.models import Contact


class UserPermission(permissions.BasePermission):
    def has_object_permission(self, request, view, obj):
        return request.method == 'GET'\
                or request.user == obj # or request.user.is_staff


class UserViewSet(viewsets.ModelViewSet):
    queryset = User.objects.all()
    permission_classes = (UserPermission, )

    def get_serializer_class(self):
        if self.request.method == 'PUT':
            return UserPutSerializer
        return UserSerializer


class ContactPermission(permissions.BasePermission):
    def has_object_permission(self, request, view, obj):
        return request.user == obj.belongs_to


class ContactViewSet(viewsets.ModelViewSet):
    queryset = Contact.objects.all()
    serializer_class = ContactSerializer
    permission_classes = (ContactPermission, permissions.IsAuthenticated, )


#EOF
