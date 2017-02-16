from django.contrib import admin
from transactions.models import *


class TransactionInformationInline(admin.TabularInline):
    model = TransactionInformation


class TransactionAdmin(admin.ModelAdmin):
    inlines = [TransactionInformationInline,]


admin.site.register(User)
admin.site.register(Contact)
admin.site.register(Transaction, TransactionAdmin)
