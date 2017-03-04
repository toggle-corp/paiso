from django.db import models


class User(models.Model):
    user_id = models.CharField(max_length=300, unique=True)
    display_name = models.CharField(max_length=300)
    email = models.CharField(max_length=300, default=None, null=True, blank=True, unique=True)
    phone = models.CharField(max_length=48, default=None, null=True, blank=True, unique=True)
    photo_url = models.TextField(default=None, null=True, blank=True)

    def __str__(self):
        return self.display_name

    def get_id(self):
        return self.user_id


class Contact(models.Model):
    belongs_to = models.ForeignKey(User)
    display_name = models.CharField(max_length=300)
    email = models.CharField(max_length=300, default=None, null=True, blank=True)
    phone = models.CharField(max_length=48, default=None, null=True, blank=True)
    photo_url = models.TextField(null=True, blank=True)

    def __str__(self):
        return '{} ({})'.format(self.display_name, str(self.belongs_to))

    def get_id(self):
        return self.pk


class Transaction(models.Model):
    to = models.ForeignKey(User, default=None, null=True, blank=True, related_name='incoming_transactions')
    by = models.ForeignKey(User, default=None, null=True, blank=True, related_name='outgoing_transactions')
    added_by = models.ForeignKey(User, related_name='created_transactions')
    unregistered_contact = models.ForeignKey(Contact, default=None, null=True, blank=True)

    def __str__(self):
        return "By {} to {}".format(
            str(self.to) if self.to is not None else str(self.unregistered_contact),
            str(self.by) if self.by is not None else str(self.unregistered_contact)
        )

    def get_other(self, one):
        if one == self.to:
            if self.by is None:
                return self.unregistered_contact, True
            else:
                return self.by, False
        elif one == self.by:
            if self.to is None:
                return self.unregistered_contact, True
            else:
                return self.to, False
        return None

    def get_amount(self):
        try:
            return TransactionInformation.objects.get(transaction=self).amount
        except:
            return 0

    def get_history(self):
        return self.transactioninformation_set.all()


class TransactionInformation(models.Model):
    transaction = models.ForeignKey(Transaction)

    title = models.CharField(max_length=300)
    amount = models.FloatField()
    approved = models.BooleanField(default=False)
    timestamp = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return str(self.amount)

    class Meta:
        ordering = ['-timestamp']
