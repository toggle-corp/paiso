from django.db import models
from django.contrib.auth.models import User
from django.utils import timezone

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

    deleted = models.BooleanField(default=False)

    created_at = models.DateTimeField(default=timezone.now)
    edited_at = models.DateTimeField(default=timezone.now)

    acknowledged_at = models.DateTimeField(null=True, blank=True,
                                           default=None)

    status = models.CharField(max_length=20,
                              choices=TRANSACTION_STATUS,
                              default='pending')

    def __str__(self):
        return "{} ({})".format(self.title, str(self.user))

    def save(self, *args, **kwargs):
        super(Transaction, self).save(*args, **kwargs)

        from transactions.notify import generate_notification_for
        generate_notification_for(self)

# EOF
