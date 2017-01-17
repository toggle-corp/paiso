package com.togglecorp.paiso;

import java.util.ArrayList;

public class Contact {
    public String userId = null;
    public String displayName = null;
    public String data;
    public String photoUrl = null;

    public Contact(String displayName, String data, String photoUrl) {
        this.displayName = displayName;
        this.data = data;
        this.photoUrl = photoUrl;
    }
}
