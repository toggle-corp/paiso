from rest_framework import serializers
from transactions.models import Transaction


class TransactionSerializer(serializers.ModelSerializer):
    class Meta:
        model = Transaction
        fields = ('id', 'uuid', 'version', 'user', 'transaction_type',
                  'contact', 'title', 'amount', 'status', 'created_at',
                  'acknowledged_at', 'edited_at', 'deleted',)

    def validate_version(self, version):
        if self.instance and version < self.instance.version:
            raise serializers.ValidationError(
                'A newer version of this object already exists')
        return version

# EOF
