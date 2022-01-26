package com.example.healthsmast;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class Session { //session object to store user's temporary login credentials
    SharedPreferences session;
    SharedPreferences.Editor editor;
    Context context;
    public static final String sessionEmail = "sessionEmail";
    public static final String sessionPW = "sessionPassword";

    public Session(Context contextInput){ //Constructor
        context = contextInput;
        session = context.getSharedPreferences("RememberMe",Context.MODE_PRIVATE);
        editor = session.edit();
    }

    public void createRememberMeSession(String email, String password){ //when user checked remember me, create a session
        //save data into session
        editor.putBoolean("isRememberMe", true);
        editor.putString(sessionEmail, email);
        editor.putString(sessionPW, password);
        editor.commit();
    }

    public HashMap<String,String> getUserDetailFromSession(){ //when user reenters login screen, get user's data from previously saved session
        //retrieve data from session
        HashMap<String, String> loginData = new HashMap<String, String>();
        loginData.put(sessionEmail, session.getString(sessionEmail,null));
        loginData.put(sessionPW, session.getString(sessionPW,null));
        return loginData;
    }

    public boolean checkRememberMe(){ //check if the session is empty or not
        if(session.getBoolean("isRememberMe", false)){
            return true;
        }
        else{
            return false;
        }
    }
}
