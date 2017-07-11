from django.contrib.auth.models import User
from django.db.models import Q

from rest_framework import permissions
from rest_framework import viewsets

from users.serializers import UserSerializer, UserPutSerializer,\
        ContactSerializer
from users.models import Contact


class UserPermission(permissions.BasePermission):
    def has_object_permission(self, request, view, obj):
        return request.method == 'GET'\
            or request.user == obj  # or request.user.is_staff


class UserViewSet(viewsets.ModelViewSet):
    queryset = User.objects.all()
    permission_classes = (UserPermission, )

    def get_serializer_class(self):
        if self.request.method == 'PUT':
            return UserPutSerializer
        return UserSerializer

    def get_object(self):
        pk = self.kwargs['pk']
        if pk == 'me':
            return self.request.user
        else:
            return super().get_object()

    def get_queryset(self):
        queryset = User.objects.all()

        query = self.request.GET.get('q')
        if query:
            for term in query.split():
                queryset = queryset.filter(
                    Q(first_name__icontains=term) |
                    Q(last_name__icontains=term) |
                    Q(username__icontains=term)
                )
        return queryset.distinct()


class ContactPermission(permissions.BasePermission):
    def has_object_permission(self, request, view, obj):
        return request.user == obj.belongs_to


class ContactViewSet(viewsets.ModelViewSet):
    queryset = Contact.objects.all()
    serializer_class = ContactSerializer
    permission_classes = (ContactPermission, permissions.IsAuthenticated, )

    def get_queryset(self):
        return Contact.objects.filter(belongs_to=self.request.user).distinct()


# EOF
