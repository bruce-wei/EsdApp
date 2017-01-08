package com.esd.phicomm.bruce.esdapp;

/**
 * Created by Bruce on 2017/1/4.
 */

public class UserInfo {

    private String userId;
    private String userName;
    private String handResult;
    private String footResult;

    public void setUserId(String id) {
        userId = id;
    }

    public void setUserName(String name) {
        userName = name;
    }

    public void setHandResult(String result) {
        handResult = result;
    }

    public void setFootResult(String result) {
        footResult = result;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getHandResult() {
        return handResult;
    }

    public String getFootResult() {
        return footResult;
    }
}
