package com.katsuna.messages.utils;

public final class Constants {

    public static final int REQUEST_CODE_READ_SMS_AND_CONTACTS = 1;
    public static final int REQUEST_CODE_ASK_CALL_PERMISSION = 2;
    public static final int REQUEST_CODE_READ_SMS = 3;
    public static final String DEFAULT_SMS_KEY = "DEFAULT_SMS_KEY";
    public static final String DEFAULT_SMS_NOT_SET = "NOT_SET";
    public static final String DEFAULT_SMS_ON = "ON";
    public static final String DEFAULT_SMS_OFF = "OFF";
    public static final int DEF_SMS_REQ_CODE = 20;

    public static final String EXTRA_DISPLAY_NAME = "EXTRA_DISPLAY_NAME";
    public static final String EXTRA_NUMBER = "EXTRA_NUMBER";
    public static final String EXTRA_CONVERASTION_ID = "EXTRA_CONVERASTION_ID";

    public static final long NOT_FOUND_CONVERSATION_ID = 0;

    private Constants() {
        // restrict instantiation
    }
}
