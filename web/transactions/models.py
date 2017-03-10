from django.db import models
from django.utils import dateformat

from datetime import datetime


class User(models.Model):
    display_name = models.CharField(max_length=300)
    email = models.CharField(max_length=300, default=None, null=True, blank=True, unique=True)
    phone = models.CharField(max_length=48, default=None, null=True, blank=True, unique=True)
    photo_url = models.TextField(default=None, null=True, blank=True)

    def __str__(self):
        return self.display_name

    def serialize(self):
        return {
            'userId': self.pk,
            'displayName': self.display_name,
            'email': self.email,
            'phone': self.phone,
            'photoUrl': self.photo_url,
        }

    @staticmethod
    def deserialize(data):
        if data.get('userId') and User.objects.filter(pk=data['userId']).count() > 0:
            user = User.objects.get(pk=data['userId'])
        elif data.get('email') and data.get('email') != '' and User.objects.filter(email=data['email']).count() > 0:
            user = User.objects.get(email=data['email'])
        elif data.get('phone') and data.get('phone') != '' and User.objects.filter(phone=data['phone']).count() > 0:
            user = User.objects.get(phone=data['phone'])
        else:
            user = User()

        user.display_name = data.get('displayName')
        user.email = data.get('email')
        user.phone = data.get('phone')
        user.photo_url = data.get('photoUrl')
        user.save()

        return user


class Contact(models.Model):
    user = models.ForeignKey(User)
    linked_user = models.ForeignKey(User, default=None, null=True, blank=True, related_name='linked_contacts')

    display_name = models.CharField(max_length=300)
    email = models.CharField(max_length=300, default=None, null=True, blank=True)
    phone = models.CharField(max_length=48, default=None, null=True, blank=True)
    photo_url = models.TextField(null=True, blank=True)

    def __str__(self):
        return '{} (contact of {})'.format(self.display_name, str(self.user))

    def serialize(self):
        return {
            'contactId': self.pk,
            'userId': self.user.pk,
            'linkedUserId': self.linked_user.pk if self.linked_user else None,
            'displayName': self.display_name,
            'email': self.email,
            'phone': self.phone,
            'photoUrl': self.photo_url,
        }

    @staticmethod
    def deserialize(data):
        user = User.objects.get(pk=data.get('userId'))

        if data.get('contactId') and Contact.objects.filter(pk=data['contactId']).count() > 0:
            contact = Contact.objects.get(pk=data['contactId'])
        elif data.get('email') and data.get('email') != '' and Contact.objects.filter(email=data['email']).count() > 0:
            contact = Contact.objects.filter(email=data['email'])[0]
        elif data.get('phone') and data.get('phone') != '' and Contact.objects.filter(phone=data['phone']).count() > 0:
            contact = Contact.objects.filter(phone=data['phone'])[0]
        else:
            contact = Contact()

        contact.user = User.objects.get(pk=data.get('userId'))
        contact.linked_user = User.objects.get(pk=data['linkedUserId']) if data.get('linkedUserId') else None

        contact.display_name = data.get('displayName')
        contact.email = data.get('email')
        contact.phone = data.get('phone')
        contact.photo_url = data.get('photoUrl')
        contact.save()

        return contact

    def refresh_linked_users(self):
        if self.email and self.email != '' and User.objects.filter(email=self.email).count() > 0:
            self.linked_user = User.objects.get(email=self.email)
            self.save()
        elif self.phone and self.phone != '' and User.objects.filter(phone=self.phone).count() > 0:
            self.phone = User.objects.get(phone=self.phone)
            self.save()


class Transaction(models.Model):

    TRANSACTION_TYPES = (
        ('to', 'to'),
        ('by', 'by'),
    )

    user = models.ForeignKey(User)
    contact = models.ForeignKey(Contact)
    transaction_type = models.CharField(max_length=2, choices=TRANSACTION_TYPES, default='to')

    def __str__(self):
        return "By {} to {}".format(
            str(self.user) if self.transaction_type == 'to' else str(self.contact),
            str(self.contact) if self.transaction_type == 'to' else str(self.user)
        )

    def get_amount(self):
        try:
            return TransactionData.objects.get(transaction=self).amount
        except:
            return 0

    def get_history(self):
        return self.transactiondata_set.all()

    def serialize(self):
        return {
            'transactionId': self.pk,
            'userId': self.user.pk,
            'contactId': self.contact.pk,
            'transactionType': self.transaction_type,
        }

    @staticmethod
    def deserialize(data):
        if data.get('transactionId') and Transaction.objects.filter(pk=data['transactionId']).count() > 0:
            transaction = Transaction.objects.get(pk=data['transactionId'])
        else:
            transaction = Transaction()

        transaction.user = User.objects.get(pk=data.get('userId'))
        transaction.contact = Contact.objects.get(pk=data.get('contactId'))
        transaction.transaction_type = data.get('transactionType')
        transaction.save()

        return transaction


class TransactionData(models.Model):
    transaction = models.ForeignKey(Transaction)

    title = models.CharField(max_length=300)
    amount = models.FloatField()
    approved = models.BooleanField(default=False)
    timestamp = models.DateTimeField()

    def __str__(self):
        return '{} ({})'.format(str(self.amount), str(self.transaction))

    class Meta:
        ordering = ['-timestamp']

    def serialize(self):
        return {
            'dataId': self.pk,
            'transactionId': self.transaction.pk,
            'title': self.title,
            'amount': self.amount,
            'approved': self.approved,
            'timestamp': int(dateformat.format(self.timestamp, 'U'))*1000,
        }

    @staticmethod
    def deserialize(data):
        if data.get('dataId') and TransactionData.objects.filter(pk=data['dataId']).count() > 0:
            transData = TransactionData.objects.get(pk=data['dataId'])
        else:
            transData = TransactionData()

        transData.transaction = Transaction.objects.get(pk=data['transactionId'])
        transData.title = data.get('title')
        transData.amount = data.get('amount')
        transData.approved = data.get('approved')
        transData.timestamp = datetime.fromtimestamp(int(data.get('timestamp'))/1000)
        transData.save()

        return transData
