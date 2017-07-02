from rest_framework import serializers
from fcm.models import FcmToken


class FcmTokenSerializer(serializers.ModelSerializer):
    class Meta:
        model = FcmToken
        fields = ('id', 'token', 'user',)
