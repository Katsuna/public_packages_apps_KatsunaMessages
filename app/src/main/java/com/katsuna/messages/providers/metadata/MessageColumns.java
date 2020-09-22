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
package com.katsuna.messages.providers.metadata;

import android.provider.Telephony;

public class MessageColumns implements android.provider.BaseColumns {

    public static final String ADDRESS = Telephony.TextBasedSmsColumns.ADDRESS;
    public static final String BODY = Telephony.TextBasedSmsColumns.BODY;
    public static final String DATE = Telephony.TextBasedSmsColumns.DATE;
    public static final String READ = Telephony.TextBasedSmsColumns.READ;
    public static final String TYPE = Telephony.TextBasedSmsColumns.TYPE;
    public static final String THREAD_ID = Telephony.TextBasedSmsColumns.THREAD_ID;
    public static final String DATE_SENT = Telephony.TextBasedSmsColumns.DATE_SENT;

}
