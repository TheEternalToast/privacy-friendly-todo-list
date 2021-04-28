/*
 This file is part of Privacy Friendly To-Do List.

 Privacy Friendly To-Do List is free software:
 you can redistribute it and/or modify it under the terms of the
 GNU General Public License as published by the Free Software Foundation,
 either version 3 of the License, or any later version.

 Privacy Friendly To-Do List is distributed in the hope
 that it will be useful, but WITHOUT ANY WARRANTY; without even
 the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Privacy Friendly To-Do List. If not, see <http://www.gnu.org/licenses/>.
 */

package org.secuso.privacyfriendlytodolist.model;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.secuso.privacyfriendlytodolist.R;
import org.secuso.privacyfriendlytodolist.model.TodoTask.DeadlineColors;
import org.secuso.privacyfriendlytodolist.model.TodoTask.Priority;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class Helper {

    public static final CharSequence DATE_FORMAT = "dd.MM.yyyy";

    public static String getDate(Context context, long time) {
        Calendar today = Calendar.getInstance();
        Calendar toDisplay = Calendar.getInstance();
        toDisplay.setTimeInMillis(TimeUnit.SECONDS.toMillis(time));
        switch (toDisplay.get(Calendar.DATE) - today.get(Calendar.DATE)) {
            case -1:
                return context.getResources().getString(R.string.yesterday);
            case 0:
                return context.getResources().getString(R.string.today);
            case 1:
                return context.getResources().getString(R.string.tomorrow);
            default:
                if (time == Long.MAX_VALUE)
                    return context.getResources().getString(R.string.never);
                return DateFormat.format(DATE_FORMAT, toDisplay).toString();
        }
    }

    public static String getDateTime(Context context, long time) {
        Calendar toDisplay = Calendar.getInstance();
        toDisplay.setTimeInMillis(TimeUnit.SECONDS.toMillis(time));
        return getDate(context, time) + " " + DateFormat.format("HH:mm", toDisplay).toString();
    }

    public static long getCurrentTimestamp() {
        return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
    }

    public static long startOfDay(long dateTime) {
        Calendar calendar = Calendar.getInstance();
        if (dateTime == -1)
            calendar.setTimeInMillis(getCurrentTimestamp());
        else
            calendar.setTimeInMillis(TimeUnit.SECONDS.toMillis(dateTime));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return TimeUnit.MILLISECONDS.toSeconds(calendar.getTimeInMillis());
    }

    public static long endOfDay(long dateTime) {
        if (dateTime == Long.MAX_VALUE) return dateTime;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(TimeUnit.SECONDS.toMillis(dateTime));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return TimeUnit.MILLISECONDS.toSeconds(calendar.getTimeInMillis());
    }

    public static int getDeadlineColor(Context context, DeadlineColors color) {
        switch (color) {
            case RED:
                return ContextCompat.getColor(context, R.color.deadline_red);
            case BLUE:
                return ContextCompat.getColor(context, R.color.deadline_blue);
            case ORANGE:
                return ContextCompat.getColor(context, R.color.deadline_orange);
        }

        throw new IllegalArgumentException("Deadline color not defined.");
    }

    public static String priority2String(Context context, Priority priority) {

        switch (priority) {
            case HIGH:
                return context.getResources().getString(R.string.high_priority);
            case MEDIUM:
                return context.getResources().getString(R.string.medium_priority);
            case LOW:
                return context.getResources().getString(R.string.low_priority);
            default:
                return context.getResources().getString(R.string.unknown_priority);

        }
    }

    public static TextView getMenuHeader(Context context, String title) {
        TextView blueBackground = new TextView(context);
        blueBackground.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        blueBackground.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
        blueBackground.setText(title);
        blueBackground.setTextColor(ContextCompat.getColor(context, R.color.black));
        blueBackground.setPadding(65, 65, 65, 65);
        blueBackground.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        blueBackground.setTypeface(null, Typeface.BOLD);

        return blueBackground;
    }

}

