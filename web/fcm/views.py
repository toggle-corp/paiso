from rest_framework import viewsets
from rest_framework import permissions

from fcm.serializers import FcmTokenSerializer
from fcm.models import FcmToken


class FcmTokenViewSet(viewsets.ModelViewSet):
    queryset = FcmToken.objects.all()
    serializer_class = FcmTokenSerializer
    permission_classes = (permissions.IsAuthenticated, )
