from rest_framework import serializers
from expenses.models import Expense


class ExpenseSerializer(serializers.ModelSerializer):
    class Meta:
        model = Expense
        fields = ('id', 'user', 'title', 'amount', 'created_at',
                  'edited_at', 'deleted',)

# EOF
