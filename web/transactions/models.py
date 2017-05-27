from django.db import models
from django.contrib.auth.models import User

from users.models import Contact


class Transaction(models.Model):
    TRANSACTION_TYPES = (
        ('to', "to"),
        ('by', "by"),
    )

    TRANSACTION_STATUS = (
        ('pending', "pending"),
        ('approved', "approved"),
        ('rejected', "rejected"),
    )

    user = models.ForeignKey(User)
    contact = models.ForeignKey(Contact)
    transaction_type = models.CharField(max_length=2,
                                        choices=TRANSACTION_TYPES,
                                        default='to')
    title = models.CharField(max_length=300)
    amount = models.FloatField()

    created_at = models.DateTimeField(auto_now_add=True)
    edited_at = models.DateTimeField(auto_now=True)

    status = models.CharField(max_length=20,
                              choices=TRANSACTION_STATUS,
                              default='pending')

    def __str__(self):
        return "{} ({})".format(self.title, str(self.user))

# EOF
