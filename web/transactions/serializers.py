from rest_framework import serializers
from transactions.models import Transaction


class TransactionSerializer(serializers.ModelSerializer):
    class Meta:
        model = Transaction
        fields = ('pk', 'user', 'transaction_type', 'contact',
                  'title', 'amount', 'status', 'created_at',
                  'acknowledged_at', 'edited_at', 'deleted',)


# EOF
