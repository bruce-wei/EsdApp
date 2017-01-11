package com.esd.phicomm.bruce.esdapp;

import android.util.Log;

import java.util.HashMap;

/**
 * Created by Bruce on 2017/1/4.
 */

class UserBll{

    private static final String TAG = "UserBll";

    static boolean insertModel(UserInfo user) {
        HashMap<String, String> params = new HashMap<>();
        params.put("sUser_Id", user.getUserId());
        params.put("sHand_Test", user.getHandResult());
        params.put("sFoot_Test", user.getFootResult());
        WEB.setMethod("inser_ESD_Info");
        if ((WEB.WebServices(params)) == null) {
            Log.d(TAG, "无法连接ShopFloor");
            return false;
        }

        return true;
    }

    static UserInfo getModel(String id) throws Exception {
        HashMap<String, String> params = new HashMap<>();
        params.put("Tb_UserId", id);
        WEB.setMethod("Sel_UserName");
        Object response;
        if ((response = WEB.WebServices(params)) == null) {
            Log.d(TAG, "无法连接ShopFloor");
            throw new Exception("无法连接ShopFloor");
        }

        String strResponse = response.toString();
        String[] result = strResponse.split("Result=")[1].split(";");
        if (result[0].equals("anyType{}")) {
            Log.d(TAG, "员工信息不存在");
            throw new Exception("员工信息不存在");
        }

        UserInfo user = new UserInfo();
        user.setUserId(id);
        user.setUserName(result[0]);
        Log.d("TAG", user.toString());
        return user;
    }

}
