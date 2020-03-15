package com.creativeshare.Jack.singletone;

import android.content.Context;

import com.creativeshare.Jack.models.UserModel;
import com.creativeshare.Jack.preferences.Preferences;

public class UserSingleTone {

    private static UserSingleTone instance = null;
    private UserModel userModel;

    private UserSingleTone() {
    }

    public static UserSingleTone getInstance()
    {
        if (instance ==null)
        {
            instance = new UserSingleTone();
        }
        return instance;
    }


    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }
    public void clear(Context context)
    {
        this.userModel=null;
        Preferences preferences = Preferences.getInstance();
        preferences.ClearUserData(context);

    }

}
