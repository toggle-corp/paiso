package com.togglecorp.paiso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class User {
    public String displayName;
    public String email;
    public String photoUrl;

    public User() {}

    public User(String displayName, String email, String photoUrl) {
        this.displayName = displayName;
        this.email = email;
        this.photoUrl = photoUrl;
    }
}
