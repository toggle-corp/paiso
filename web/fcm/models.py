from django.db import models
from django.contrib.auth.models import User


class FcmToken(models.Model):
    token = models.TextField()
    user = models.ForeignKey(User)

    def __str__(self):
        return '{} ({})'.format(str(self.user), self.token)


# EOF
