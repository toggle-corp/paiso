from rest_framework import serializers
from transactions.models import Transaction


class TransactionSerializer(serializers.ModelSerializer):
    user = serializers.PrimaryKeyRelatedField(
            read_only=True,
            default=serializers.CurrentUserDefault())

    class Meta:
        model = Transaction
        fields = ('pk', 'user', 'transaction_type', 'contact',
                  'title', 'amount', 'status', 'created_at',
                  'edited_at', )


# EOF
