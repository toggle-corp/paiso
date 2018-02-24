from django.db import models
from django.contrib.auth.models import User
from django.utils import timezone

import uuid


class Expense(models.Model):
    uuid = models.UUIDField(default=uuid.uuid4, unique=True)
    version = models.IntegerField(default=1)

    user = models.ForeignKey(User)
    title = models.CharField(max_length=300)
    amount = models.FloatField()
    deleted = models.BooleanField(default=False)

    created_at = models.DateTimeField(default=timezone.now)
    edited_at = models.DateTimeField(default=timezone.now)

    def __str__(self):
        return "{} ({})".format(self.title, str(self.user))
