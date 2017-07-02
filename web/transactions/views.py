from django.db.models import Q, F

from rest_framework import viewsets
from rest_framework import permissions

from transactions.serializers import TransactionSerializer
from transactions.models import Transaction
from transactions.notify import generate_notification_for


class TransactionPermission(permissions.BasePermission):
    def has_object_permission(self, request, view, obj):
        return request.user == obj.user or\
                request.user == obj.contact.user


class TransactionViewSet(viewsets.ModelViewSet):
    queryset = Transaction.objects.all()
    serializer_class = TransactionSerializer
    permission_classes = (TransactionPermission, permissions.IsAuthenticated, )

    def get_queryset(self):
        return Transaction.objects.filter(
            Q(user=self.request.user) |
            Q(
                contact__user=self.request.user,
                contact__user__contact__user=F('user')
            )
        )

    def perform_create(self, serializer):
        transaction = serializer.save()
        self.post_save(transaction)

    def perform_update(self, serializer):
        transaction = serializer.save()
        self.post_save(transaction)

    def post_save(self, transaction):
        generate_notification_for(transaction, self.request.user)

# EOF
