from django.contrib import admin
from transactions.models import *


class TransactionDataInline(admin.TabularInline):
    model = TransactionData


class TransactionAdmin(admin.ModelAdmin):
    inlines = [TransactionDataInline,]


admin.site.register(User)
admin.site.register(Contact)
admin.site.register(Transaction, TransactionAdmin)
