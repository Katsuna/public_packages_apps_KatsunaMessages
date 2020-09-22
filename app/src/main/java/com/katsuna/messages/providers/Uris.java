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
package com.katsuna.messages.providers;

import android.net.Uri;
import android.provider.Telephony;

class Uris {

    public static final Uri THREADS_URI = Telephony.Threads.CONTENT_URI;
    public static final Uri THREADS_URI_SIMPLE = THREADS_URI.buildUpon().appendQueryParameter("simple", "true").build();

    public static final Uri CANONICAL_ADDRESS = Uri.parse("content://mms-sms/canonical-address");

    public static final Uri URI_SMS = Uri.parse("content://sms");

    public static final Uri URI_SENT = Uri.parse("content://sms/sent");

    public static final Uri URI_INBOX = Uri.parse("content://sms/inbox");

}
