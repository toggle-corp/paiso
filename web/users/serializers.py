from django.contrib.auth.models import User
from users.models import Contact
from rest_framework import serializers


class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ('pk', 'username', 'password', 'first_name', 'last_name', )
        extra_kwargs = {'password': {'write_only': True}, }

    def create(self, validated_data):
        user = User(username=validated_data['username'],
                    first_name=validated_data['first_name'],
                    last_name=validated_data['last_name'])
        user.set_password(validated_data['password'])
        user.save()
        return user


class UserPutSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ('pk', 'username', 'first_name', 'last_name', )


class ContactSerializer(serializers.ModelSerializer):
    belongs_to = serializers.PrimaryKeyRelatedField(
            read_only=True,
            default=serializers.CurrentUserDefault())

    class Meta:
        model = Contact
        fields = ('pk', 'name', 'user', 'belongs_to', 'created_at', 
                  'edited_at', )


# EOF
