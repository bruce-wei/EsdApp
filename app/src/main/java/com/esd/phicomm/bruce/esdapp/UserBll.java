package com.esd.phicomm.bruce.esdapp;

import android.util.Log;

import java.util.HashMap;

/**
 * Created by Bruce on 2017/1/4.
 */

public class UserBll {

    public static void insertModel(UserInfo user) {
        HashMap<String, String> methods = new HashMap<>();
        methods.put("Tb_UserId", "1112");
        WEB.setMethod("Sel_UserName");
        WEB.setRturnType("String");
        Object result = WEB.WebServices(methods);
        Log.d("UserBll", "");
    }

    public static UserInfo getModel(String id) {
        return new UserInfo();
    }

}
