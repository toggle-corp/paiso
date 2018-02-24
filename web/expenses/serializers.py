from rest_framework import serializers
from expenses.models import Expense


class ExpenseSerializer(serializers.ModelSerializer):
    class Meta:
        model = Expense
        fields = ('id', 'uuid', 'version', 'user', 'title', 'amount',
                  'created_at', 'edited_at', 'deleted',)

    def validate_version(self, version):
        if self.instance and version < self.instance.version:
            raise serializers.ValidationError(
                'A newer version of this object already exists')
        return version

# EOF
