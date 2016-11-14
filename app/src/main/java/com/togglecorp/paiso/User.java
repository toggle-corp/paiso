package com.togglecorp.paiso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class User {
    public String displayName;
    public String email;
    public String photoUrl;
    public HashMap<String, String> tokens = new HashMap<>();

    public User() {}

    public User(String displayName, String email, String photoUrl) {
        this.displayName = displayName;
        this.email = email;
        this.photoUrl = photoUrl;
    }

    public List<String> getTokens() {
        if (tokens == null)
            return new ArrayList<>();
        return new ArrayList<>(tokens.values());
    }
}
