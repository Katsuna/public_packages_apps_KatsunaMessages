/**
* Copyright (C) 2020 Manos Saratsis
*
* This file is part of Katsuna.
*
* Katsuna is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Katsuna is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Katsuna.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.katsuna.messages.utils;

public final class Constants {

    public static final int REQUEST_CODE_READ_SMS_AND_CONTACTS = 1;
    public static final int REQUEST_CODE_ASK_CALL_PERMISSION = 2;
    public static final int REQUEST_CODE_READ_SMS = 3;
    public static final int REQUEST_CODE_READ_CONV_ID = 4;
    public static final String DEFAULT_SMS_KEY = "DEFAULT_SMS_KEY";
    public static final String DEFAULT_SMS_NOT_SET = "NOT_SET";
    public static final String DEFAULT_SMS_ON = "ON";
    public static final String DEFAULT_SMS_OFF = "OFF";
    public static final int DEF_SMS_REQ_CODE = 20;

    public static final String EXTRA_CONVERASTION_ID = "EXTRA_CONVERASTION_ID";

    public static final long NOT_FOUND_CONVERSATION_ID = 0;

    private Constants() {
        // restrict instantiation
    }
}
