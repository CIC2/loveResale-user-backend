package com.resale.resaleuser.model;

public enum ActionType {
    CREATE_USER(1),
    GET_CURRENT_USER(2),
    GET_PROFILE(3),
    GET_PROFILE_BY_ID(4),
    CHANGE_STATUS(5),
    GET_ALL_USERS(6),
    LOGOUT(7),
    SEND_FORGET_PASSWORD_OTP(8),
    VERIFY_FORGET_PASSWORD_OTP(9),
    RESET_FORGET_PASSWORD(10),
    CONFIRM_FORGET_PASSWORD(11),
    UPDATE_USER(12),
    UPDATE_TOKEN(13),
    GET_CUSTOMER_DETAILS(14),
    GET_LANGUAGES(15),
    GET_CUSTOMER_PROFILE (16),
    GET_PERMISSIONS(17),
    INTERNAL_GET_USER_PROFILE(18),
    INTERNAL_ASSIGN_SALESMAN(19),
    INTERNAL_GET_SALES_AND_TEAM_LEADS(20),
    INTERNAL_GET_ZOOM_ID_FOR_USER(21),
    INTERNAL_IS_USER_ASSIGNED_TO_TEAMLEAD(22),

    UNKNOWN(99); // fallback



    private final int code;

    ActionType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}


