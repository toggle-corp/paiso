from django.db.models import Q, F

from rest_framework import viewsets
from rest_framework import permissions

from expenses.serializers import ExpenseSerializer
from expenses.models import Expense


class ExpensePermission(permissions.BasePermission):
    def has_object_permission(self, request, view, obj):
        return request.user == obj.user


class ExpenseViewSet(viewsets.ModelViewSet):
    queryset = Expense.objects.all()
    serializer_class = ExpenseSerializer
    permission_classes = (ExpensePermission, permissions.IsAuthenticated, )

    def get_queryset(self):
        return Expense.objects.filter(user=self.request.user)


# EOF
