# -*- coding: utf-8 -*-
# Generated by Django 1.10.5 on 2017-06-12 06:22
from __future__ import unicode_literals

from django.db import migrations, models
import django.utils.timezone


class Migration(migrations.Migration):

    dependencies = [
        ('transactions', '0002_transaction_deleted'),
    ]

    operations = [
        migrations.AlterField(
            model_name='transaction',
            name='created_at',
            field=models.DateTimeField(default=django.utils.timezone.now),
        ),
        migrations.AlterField(
            model_name='transaction',
            name='edited_at',
            field=models.DateTimeField(default=django.utils.timezone.now),
        ),
    ]
