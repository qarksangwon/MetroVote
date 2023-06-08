package com.inhatc.metrovote.vo;

public class UserVO {
    public String userId;
    public String userPw;
    public String googleId;

    public UserVO() {
        // 파이어베이스에서 객체를 직렬화/역직렬화할 때 필요한 기본 생성자
    }

    public UserVO(String userId, String userPw, String googleId) {
        this.userId = userId;
        this.userPw = userPw;
        this.googleId = googleId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserPw() {
        return userPw;
    }


    public String getGoogleId() {
        return googleId;
    }
}