package com.togglecorp.paiso;

public class Contact {
    public String userId = null;
    public String displayName = null;
    public String email;
    public String photoUrl = null;

    public Contact(String displayName, String email, String photoUrl) {
        this.displayName = displayName;
        this.email = email;
        this.photoUrl = photoUrl;
    }
}
