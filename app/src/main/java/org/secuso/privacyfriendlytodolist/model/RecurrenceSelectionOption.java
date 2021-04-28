package org.secuso.privacyfriendlytodolist.model;

import android.content.Context;

import org.secuso.privacyfriendlytodolist.R;

import java.util.Calendar;

public interface RecurrenceSelectionOption{
    public boolean isSelected();

    public void setSelected(boolean selected);

    public int getValue();

    public String toString(Context context);

    public enum Weekday implements RecurrenceSelectionOption {
        MONDAY(Calendar.MONDAY), TUESDAY(Calendar.TUESDAY), WEDNESDAY(Calendar.WEDNESDAY),
        THURSDAY(Calendar.THURSDAY), FRIDAY(Calendar.FRIDAY), SATURDAY(Calendar.SATURDAY),
        SUNDAY(Calendar.SUNDAY);
        private final int value;
        private boolean selected = false;

        Weekday(final int value) {
            this.value = value;
        }

        public static Weekday fromInt(int i) {
            for (Weekday w : Weekday.values())
                if (w.getValue() == i) return w;
            throw new IllegalArgumentException("No such weekday defined.");
        }


        @Override
        public boolean isSelected() {
            return selected;
        }

        @Override
        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        @Override
        public int getValue() {
            return value;
        }

        @Override
        public String toString(Context context) {
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
        JANUARY(Calendar.JANUARY), FEBRUARY(Calendar.FEBRUARY), MARCH(Calendar.MARCH),
        APRIL(Calendar.APRIL), MAY(Calendar.MAY), JUNE(Calendar.JUNE), JULY(Calendar.JULY),
        AUGUST(Calendar.AUGUST), SEPTEMBER(Calendar.SEPTEMBER), OCTOBER(Calendar.OCTOBER),
        NOVEMBER(Calendar.NOVEMBER), DECEMBER(Calendar.DECEMBER);
        private final int value;
        private boolean selected = false;

        Month(final int value) {
            this.value = value;
        }

        public static Month fromInt(int i) {
            for (Month m : Month.values())
                if (m.getValue() == i) return m;
            throw new IllegalArgumentException("No such month defined.");
        }

        @Override
        public boolean isSelected() {
            return selected;
        }

        @Override
        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public int getValue() {
            return value;
        }

        public String toString(Context context) {
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

    public enum NoSelection implements RecurrenceSelectionOption {
        ;

        @Override
        public boolean isSelected() {
            return false;
        }

        @Override
        public void setSelected(boolean selected) {
        }

        @Override
        public int getValue() {
            return 0;
        }

        @Override
        public String toString(Context context) {
            return "";
        }
    }
}
