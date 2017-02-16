from django.db import models


class User(models.Model):
    user_id = models.CharField(max_length=300, unique=True)
    display_name = models.CharField(max_length=300)
    email = models.CharField(max_length=300, default=None, null=True, blank=True, unique=True)
    # phone = models.CharField(max_length=48, default=None, null=True, blank=True, unique=True)
    photo_url = models.TextField(default=None, null=True, blank=True)

    def __str__(self):
        return self.display_name


class Contact(models.Model):
    belongs_to = models.ForeignKey(User)

    contact_id = models.CharField(max_length=300, unique=True)
    display_name = models.CharField(max_length=300)
    email = models.CharField(max_length=300, default=None, null=True, blank=True)
    # phone = models.CharField(max_length=48, default=None, null=True, blank=True)
    photo_url = models.TextField(null=True, blank=True)

    def __str__(self):
        return self.display_name + ' (' + str(self.belongs_to) + ')'


class Transaction(models.Model):
    to = models.ForeignKey(User, default=None, null=True, blank=True, related_name='incoming_transactions')
    by = models.ForeignKey(User, default=None, null=True, blank=True, related_name='outgoing_transactions')
    added_by = models.ForeignKey(User, default=None, null=True, blank=True, related_name='created_transactions')
    unregistered_contact = models.ForeignKey(Contact, default=None, null=True, blank=True)

    def __str__(self):
        return "By " + str(self.by) + " to " + str(self.to)


class TransactionInformation(models.Model):
    transaction = models.ForeignKey(Transaction)

    title = models.CharField(max_length=300)
    amount = models.FloatField()
    approved = models.BooleanField(default=False)
    timestamp = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return str(self.amount)
