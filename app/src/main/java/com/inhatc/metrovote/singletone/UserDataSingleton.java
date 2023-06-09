package com.inhatc.metrovote.singletone;

import com.google.firebase.auth.FirebaseUser;

public class UserDataSingleton {
    private static UserDataSingleton instance;
    private FirebaseUser user;

    private UserDataSingleton() {
        // private 생성자
    }

    public static synchronized UserDataSingleton getInstance() {
        if (instance == null) {
            instance = new UserDataSingleton();
        }
        return instance;
    }

    public FirebaseUser getUser() {
        return user;
    }

    public void setUser(FirebaseUser user) {
        this.user = user;
    }
}
