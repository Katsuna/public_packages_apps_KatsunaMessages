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

import android.provider.BaseColumns;
import android.provider.Telephony;

public class ThreadColumns implements BaseColumns {

    public static final String DATE = Telephony.ThreadsColumns.DATE;
    public static final String TYPE = Telephony.ThreadsColumns.TYPE;
    public static final String READ = Telephony.ThreadsColumns.READ;
    public static final String RECIPIENT_IDS = Telephony.ThreadsColumns.RECIPIENT_IDS;
    public static final String SNIPPET = Telephony.ThreadsColumns.SNIPPET;
    public static final String MESSAGE_COUNT = Telephony.ThreadsColumns.MESSAGE_COUNT;

}
