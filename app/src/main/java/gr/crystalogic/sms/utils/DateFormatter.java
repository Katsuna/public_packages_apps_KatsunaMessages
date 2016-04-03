package gr.crystalogic.sms.utils;

import android.content.Context;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Interval;

import gr.crystalogic.sms.R;

public class DateFormatter {

    public static String format(Context context, long date) {
        DateTime dateTime = new DateTime(date);

        Interval todayInterval = new Interval(DateTime.now().withTimeAtStartOfDay(), Days.ONE);
        Interval yesterdayInterval = new Interval(DateTime.now().withTimeAtStartOfDay().minusDays(1), Days.ONE);

        String output;
        if (todayInterval.contains(dateTime)) {
            output = dateTime.toString("HH:mm ") + context.getResources().getString(R.string.today);
        } else if (yesterdayInterval.contains(dateTime)) {
            output = dateTime.toString("HH:mm ") + context.getResources().getString(R.string.yesterday);
        } else {
            output = dateTime.toString("HH:mm EEEE d-M-yy");
        }

        return output;
    }
}