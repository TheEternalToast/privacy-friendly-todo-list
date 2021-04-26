package org.secuso.privacyfriendlytodolist.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.secuso.privacyfriendlytodolist.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;
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
                    return context.getResources().getString(R.string.day);
                case WEEKDAYS:
                    return context.getResources().getString(R.string.weekdays);
                case WEEKLY:
                    return context.getResources().getString(R.string.week);
                case MONTHLY:
                    return context.getResources().getString(R.string.month);
                case SOME_MONTHS:
                    return context.getResources().getString(R.string.some_months);
                case YEARLY:
                    return context.getResources().getString(R.string.year);
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
    protected Set<Integer> selection;

    protected long startDate;
    protected long endDate;

    // Constructors
    public Recurrence() {
        this.type = Type.NONE;
        this.increment = 1;
        this.selection = new HashSet<>();
        this.startDate = Helper.getCurrentTimestamp();
        this.endDate = Long.MAX_VALUE;
    }

    public Recurrence(Type type, int increment) {
        this.type = type;
        this.increment = increment;
        this.selection = new HashSet<>();
        this.startDate = Helper.getCurrentTimestamp();
        this.endDate = Long.MAX_VALUE;
    }

    public Recurrence(Type type, Set<Integer> selection) {
        this.type = type;
        this.increment = 1;
        this.selection = selection;
        this.startDate = Helper.getCurrentTimestamp();
        this.endDate = Long.MAX_VALUE;
    }

    public Recurrence(Type type, int increment, long startDate, long endDate) {
        this.type = type;
        this.increment = increment;
        this.selection = new HashSet<>();
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Recurrence(Type type, Set<Integer> selection, long startDate, long endDate) {
        this.type = type;
        this.increment = 1;
        this.selection = selection;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Recurrence(Type type, int increment, Set<Integer> selection, long startDate, long endDate) {
        this.type = type;
        this.increment = increment;
        this.selection = selection;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Recurrence(Recurrence recurrence) {
        this.type = recurrence.type;
        this.increment = recurrence.increment;
        this.selection = new HashSet<>();
        this.selection.addAll(recurrence.selection);
        this.startDate = recurrence.startDate;
        this.endDate = recurrence.endDate;
    }

    public Recurrence(Parcel parcel) {
        type = Type.fromInt(parcel.readInt());
        increment = parcel.readInt();
        selection = decodeSelection(parcel.readInt());
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
        this.increment = increment;
    }

    public Set<Integer> getSelection() {
        return selection;
    }

    public void setSelection(Set<Integer> selection) {
        this.selection = selection;
    }

    public boolean addToSelection(Integer option){
        return selection.add(option);
    }

    public boolean removeFromSelection(Integer option) {
        return selection.remove(option);
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    // Functional methods
    public long next(long lastDeadline) {
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

    public static Set<Integer> decodeSelection(int encodedSelection) {
        Set<Integer> selection = new HashSet<>();
        for (int i = 0; i < 31; i++) {
            if (encodedSelection % 2 == 1)
                selection.add(i);
            encodedSelection = encodedSelection >> 1;
        }
        return selection;
    }
}
