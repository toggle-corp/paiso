from django.contrib.auth.models import User

from rest_framework import permissions
from rest_framework import viewsets

from transactions.serializers import TransactionSerializer
from transactions.models import Transaction


class TransactionPermission(permissions.BasePermission):
    def has_object_permission(self, request, view, obj):
        return request.user == obj.user or\
                request.contact.user == obj.user


class TransactionViewSet(viewsets.ModelViewSet):
    queryset = Transaction.objects.all()
    serializer_class = TransactionSerializer
    permission_classes = (TransactionPermission, permissions.IsAuthenticated, )


# EOF
