package com.laundry.app.dto;

import android.content.Context;
import android.text.TextUtils;

import com.laundry.app.utils.SharePreferenceManager;

public class UserInfo {
    private String username;
    private String role;
    private String token;
    private static final UserInfo INSTANCE = new UserInfo();

    private UserInfo() {
    }

    public void setUsername(Context context, String username) {
        SharePreferenceManager.setUsername(context, username);
    }

    public void setToken(Context context, String token) {
        SharePreferenceManager.setToken(context, token);
    }

    public static UserInfo getInstance() {
        return INSTANCE;
    }

    public String getUsername(Context context) {
        return SharePreferenceManager.getUsername(context);
    }

    public String getRole() {
        return role;
    }

    public String getToken(Context context) {
        return SharePreferenceManager.getToken(context);
    }

    /**
     * Check login
     * @param context Context
     * @return true if exist username and token else false
     */
    public boolean isLogin(Context context) {
        username = SharePreferenceManager.getUsername(context);
        token = SharePreferenceManager.getToken(context);
        return !TextUtils.isEmpty(username) && !TextUtils.isEmpty(token);
    }

    /**
     * Init Userinfo
     * @param context
     */
    public void init(Context context) {
        username = null;
        token = null;
        role = null;
        SharePreferenceManager.setToken(context, null);
        SharePreferenceManager.setUsername(context, null);
        SharePreferenceManager.setUserAvatar(context, null);
    }
}
