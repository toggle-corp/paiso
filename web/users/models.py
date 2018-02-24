from django.db import models
from django.contrib.auth.models import User

import uuid


class Contact(models.Model):
    uuid = models.UUIDField(default=uuid.uuid4, unique=True)
    version = models.IntegerField(default=1)

    belongs_to = models.ForeignKey(User)
    name = models.CharField(max_length=255)
    user = models.ForeignKey(User, default=None, blank=True, null=True,
                             related_name='linked_contacts')

    created_at = models.DateTimeField(auto_now_add=True)
    edited_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return "{} - contact of {}".format(self.name, str(self.belongs_to))


# EOF
