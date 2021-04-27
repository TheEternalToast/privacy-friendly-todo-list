package org.secuso.privacyfriendlytodolist.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.secuso.privacyfriendlytodolist.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ben Westerath on 25.04.2021.
 * <p>
 * This class is responsible to enable recurring To-Do tasks.
 */
public class Recurrence implements Parcelable {

    // Enums
    public enum Type {
        NONE(0), DAILY(1), WEEKDAYS(2), WEEKLY(3), MONTHLY(4), SOME_MONTHS(5), YEARLY(6);
        // Types must be sorted in the same way as they will be displayed

        private final int value;

        Type(final int newValue) {
            value = newValue;
        }

        public int getValue() {
            return value;
        }

        public static Type fromInt(int i) {
            for (Type t : Type.values()) {
                if (t.getValue() == i) {
                    return t;
                }
            }
            throw new IllegalArgumentException("No such type defined.");
        }

        public String toString(Context context) {
            switch (this) {
                case NONE:
                    return context.getResources().getString(R.string.no_recurrence);
                case DAILY:
                    return context.getResources().getString(R.string.days);
                case WEEKDAYS:
                    return context.getResources().getString(R.string.weekdays);
                case WEEKLY:
                    return context.getResources().getString(R.string.weeks);
                case MONTHLY:
                    return context.getResources().getString(R.string.months);
                case SOME_MONTHS:
                    return context.getResources().getString(R.string.some_months);
                case YEARLY:
                    return context.getResources().getString(R.string.years);
                default:
                    throw new IllegalStateException("No such type defined.");
            }
        }

        public static ArrayList<String> stringValues(Context context) {
            ArrayList<String> strVals = new ArrayList<>(values().length);
            for (int i = 0; i < values().length; i++) {
                strVals.set(i, values()[i].toString(context));
            }
            return strVals;
        }
    }

    // Class variables
    public static boolean allowPastRecurrence = false;

    public static final Parcelable.Creator<Recurrence> CREATOR =
            new Creator<Recurrence>() {
                @Override
                public Recurrence createFromParcel(Parcel source) {
                    return new Recurrence(source);
                }

                @Override
                public Recurrence[] newArray(int size) {
                    return new Recurrence[size];
                }
            };

    // Instance variables
    protected Type type;
    protected int increment;
    protected SortedSet<Integer> selection;

    protected long startDate;
    protected long endDate;

    // Constructors
    public Recurrence() {
        this.type = Type.NONE;
        this.increment = 1;
        this.selection = new TreeSet<>();
        this.startDate = Helper.getCurrentTimestamp();
        this.endDate = Long.MAX_VALUE;
    }

    public Recurrence(Type type, int increment) {
        this.type = type;
        setIncrement(increment);
        this.selection = new TreeSet<>();
        this.startDate = Helper.getCurrentTimestamp();
        this.endDate = Long.MAX_VALUE;
    }

    public Recurrence(Type type, Set<Integer> selection) {
        this.type = type;
        this.increment = 1;
        setSelection(selection);
        this.startDate = Helper.getCurrentTimestamp();
        this.endDate = Long.MAX_VALUE;
    }

    public Recurrence(Type type, int increment, long startDate, long endDate) {
        this.type = type;
        setIncrement(increment);
        this.selection = new TreeSet<>();
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Recurrence(Type type, Set<Integer> selection, long startDate, long endDate) {
        this.type = type;
        this.increment = 1;
        setSelection(selection);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Recurrence(Type type, int increment, Set<Integer> selection, long startDate, long endDate) {
        this.type = type;
        setIncrement(increment);
        setSelection(selection);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Recurrence(Recurrence recurrence) {
        this.type = recurrence.type;
        setIncrement(recurrence.increment);
        setSelection(recurrence.selection);
        this.startDate = recurrence.startDate;
        this.endDate = recurrence.endDate;
    }

    public Recurrence(Parcel parcel) {
        type = Type.fromInt(parcel.readInt());
        setIncrement(parcel.readInt());
        setSelection(decodeSelection(parcel.readInt()));
        startDate = parcel.readLong();
        endDate = parcel.readLong();
    }

    // Overrides
    //  Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type.getValue());
        dest.writeInt(increment);
        dest.writeInt(encodeSelection());
        dest.writeLong(startDate);
        dest.writeLong(endDate);
    }

    //  Object
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Recurrence)) return false;
        Recurrence that = (Recurrence) o;
        return type == that.type &&
                selection.equals(that.selection);
    }

    @Override
    public int hashCode() {
        return (31 + type.hashCode()) * 31 + selection.hashCode();
    }

    public String toString(Context context) {
        switch (type) {
            case NONE:
                return context.getResources().getString(R.string.no_recurrence);
            case DAILY:
                if (increment == 1) return context.getResources().getString(R.string.daily);
                return context.getResources().getString(R.string.every_X_days, increment);
            case WEEKLY:
                if (increment == 1) return context.getResources().getString(R.string.weekly);
                return context.getResources().getString(R.string.every_X_weeks, increment);
            case MONTHLY:
                if (increment == 1) return context.getResources().getString(R.string.monthly);
                return context.getResources().getString(R.string.every_X_months, increment);
            case YEARLY:
                if (increment == 1) return context.getResources().getString(R.string.annual);
                return context.getResources().getString(R.string.every_X_years, increment);
            case WEEKDAYS:
            case SOME_MONTHS:
                if (selection.isEmpty())
                    return context.getResources().getString(R.string.no_recurrence);
                StringBuilder selectedOptionsBuilder = new StringBuilder();
                for (int i : selection) {
                    RecurrenceSelectionOption thisOption = recurrenceSelectionOptionFromInt(i);
                    selectedOptionsBuilder.append(thisOption.toString(context)).append(", ");
                }
                String selectedOptions = selectedOptionsBuilder.toString();
                return selectedOptions.substring(0, selectedOptions.length() - 2);
        }
        return super.toString();
    }

    // Getters & Setters
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getIncrement() {
        return increment;
    }

    public void setIncrement(int increment) {
        this.increment = Math.max(increment, 1);
    }

    public SortedSet<Integer> getSelection() {
        return selection;
    }

    public void setSelection(Set<Integer> selection) {
        this.selection = new TreeSet<>();
        for (Integer option : selection) {
            addToSelection(option);
        }
    }

    private Integer verifySelectable(Integer option) {
        int numOptions;
        switch (type) {
            case WEEKDAYS:
                numOptions = 7;
                break;
            case SOME_MONTHS:
                numOptions = 12;
                break;
            default:
                throw new IllegalStateException("Selection can only be modified in selective recursion.");
        }
        if (option < 0 || numOptions <= option)
            throw new IllegalArgumentException("Invalid option: " + option + ", must be at least 0 and less than " + numOptions + ".");
        return option;
    }

    public boolean addToSelection(Integer option) {
        return selection.add(verifySelectable(option));
    }

    public boolean removeFromSelection(Integer option) {
        try {
            return selection.remove(verifySelectable(option));
        } catch (IllegalArgumentException ignored){
            return false;
        } catch (IllegalStateException ignored){
            return false;
        }
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = Helper.startOfDay(startDate);
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = Helper.endOfDay(endDate);
    }

    // Functional methods
    public long next(long lastDeadline) {
        if (lastDeadline == -1 ) return -1;
        Calendar deadline = GregorianCalendar.getInstance();
        deadline.setTimeInMillis(TimeUnit.SECONDS.toMillis(lastDeadline));

        long now = Helper.getCurrentTimestamp();
        long longDeadline = TimeUnit.MILLISECONDS.toSeconds(deadline.getTimeInMillis());
        while (longDeadline < startDate && (allowPastRecurrence || longDeadline < now)) {
            deadline.add(Calendar.DATE, increment);
            longDeadline = TimeUnit.MILLISECONDS.toSeconds(deadline.getTimeInMillis());
        }

        boolean inSelection;
        switch (type) {
            case DAILY:
                deadline.add(Calendar.DATE, increment);
                break;
            case WEEKLY:
                deadline.add(Calendar.DATE, 7 * increment);
                break;
            case MONTHLY:
                deadline.add(Calendar.MONTH, increment);
                break;
            case YEARLY:
                deadline.add(Calendar.YEAR, increment);
                break;
            case WEEKDAYS:
                if (selection.isEmpty()) return -1;
                do {
                    deadline.add(Calendar.DATE, 1);
                    inSelection = isSelected(deadline.get(Calendar.DAY_OF_WEEK));
                } while (!inSelection);
                break;
            case SOME_MONTHS:
                if (selection.isEmpty()) return -1;
                do {
                    deadline.add(Calendar.MONTH, 1);
                    inSelection = isSelected(deadline.get(Calendar.MONTH));
                } while (!inSelection);
                break;
            default:
                return -1;
        }
        longDeadline = TimeUnit.MILLISECONDS.toSeconds(deadline.getTimeInMillis());
        if (longDeadline > endDate) return -1;
        return longDeadline;
    }

    public boolean isSelected(int indicator) {
        return selection.contains(indicator);
    }

    public int encodeSelection() {
        int encodedSelections = 0;
        for (int i = 30; i >= 0; i--) {
            if (isSelected(i)) encodedSelections++;
            encodedSelections = encodedSelections << 1;
        }
        return encodedSelections;
    }

    public static TreeSet<Integer> decodeSelection(int encodedSelection) {
        TreeSet<Integer> selection = new TreeSet<>();
        for (int i = 0; i < 31; i++) {
            if (encodedSelection % 2 == 1)
                selection.add(i);
            encodedSelection = encodedSelection >> 1;
        }
        return selection;
    }

    private RecurrenceSelectionOption recurrenceSelectionOptionFromInt(int i) {
        switch (type) {
            case WEEKDAYS:
                return RecurrenceSelectionOption.Weekday.fromInt(i);
            case SOME_MONTHS:
                return RecurrenceSelectionOption.Month.fromInt(i);
            default:
                throw new IllegalStateException("Selection option can only be inferred from selective recursion types.");
        }
    }
}
