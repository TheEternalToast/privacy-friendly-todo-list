package org.secuso.privacyfriendlytodolist.model;

import android.content.Context;

import org.secuso.privacyfriendlytodolist.R;

public interface RecurrenceSelectionOption {

    public int getValue();
    public String toString(Context context);

    public enum Weekday implements RecurrenceSelectionOption {
        MONDAY(0), TUESDAY(1), WEDNESDAY(2), THURSDAY(3),
        FRIDAY(4), SATURDAY(5), SUNDAY(6);
        private final int value;

        Weekday(final int value) {
            this.value = value;
        }

        public static Weekday fromInt(int i) {
            for (Weekday w : Weekday.values())
                if (w.getValue() == i) return w;
            throw new IllegalArgumentException("No such weekday defined.");
        }

        public int getValue() {
            return value;
        }

        public String toString(Context context){
            switch (this) {
                case MONDAY:
                    return context.getResources().getString(R.string.monday_abbr);
                case TUESDAY:
                    return context.getResources().getString(R.string.tuesday_abbr);
                case WEDNESDAY:
                    return context.getResources().getString(R.string.wednesday_abbr);
                case THURSDAY:
                    return context.getResources().getString(R.string.thursday_abbr);
                case FRIDAY:
                    return context.getResources().getString(R.string.friday_abbr);
                case SATURDAY:
                    return context.getResources().getString(R.string.saturday_abbr);
                case SUNDAY:
                    return context.getResources().getString(R.string.sunday_abbr);
                default:
                    throw new IllegalStateException("No such weekday defined.");
            }
        }
    }

    public enum Month implements RecurrenceSelectionOption {
        JANUARY(0), FEBRUARY(1), MARCH(2), APRIL(3), MAY(4),
        JUNE(5), JULY(6), AUGUST(7), SEPTEMBER(8), OCTOBER(9),
        NOVEMBER(10), DECEMBER(11);
        private final int value;

        Month(final int value) {
            this.value = value;
        }

        public static Month fromInt(int i) {
            for (Month m : Month.values())
                if (m.getValue() == i) return m;
            throw new IllegalArgumentException("No such month defined.");
        }

        public int getValue() {
            return value;
        }

        public String toString(Context context){
            switch (this) {
                case JANUARY:
                    return context.getResources().getString(R.string.january_abbr);
                case FEBRUARY:
                    return context.getResources().getString(R.string.february_abbr);
                case MARCH:
                    return context.getResources().getString(R.string.march_abbr);
                case APRIL:
                    return context.getResources().getString(R.string.april_abbr);
                case MAY:
                    return context.getResources().getString(R.string.may_abbr);
                case JUNE:
                    return context.getResources().getString(R.string.june_abbr);
                case JULY:
                    return context.getResources().getString(R.string.july_abbr);
                case AUGUST:
                    return context.getResources().getString(R.string.august_abbr);
                case SEPTEMBER:
                    return context.getResources().getString(R.string.september_abbr);
                case OCTOBER:
                    return context.getResources().getString(R.string.october_abbr);
                case NOVEMBER:
                    return context.getResources().getString(R.string.november_abbr);
                case DECEMBER:
                    return context.getResources().getString(R.string.december_abbr);
                default:
                    throw new IllegalStateException("No such month defined.");
            }
        }
    }

}
