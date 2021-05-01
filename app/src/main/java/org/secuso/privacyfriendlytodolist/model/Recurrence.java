package org.secuso.privacyfriendlytodolist.model;

import android.content.Context;
import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.secuso.privacyfriendlytodolist.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
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

        public Class<? extends RecurrenceSelectionOption> getOptionType() {
            switch (this) {
                case WEEKDAYS:
                    return RecurrenceSelectionOption.Weekday.class;
                case SOME_MONTHS:
                    return RecurrenceSelectionOption.Month.class;
                default:
                    return RecurrenceSelectionOption.NoSelection.class;
            }
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
    public Recurrence(Type type, int increment, Set<Integer> selection, long startDate, long endDate) {
        super();
        this.type = type;
        setIncrement(increment);
        setSelection(selection);
        setStartDate(startDate);
        setEndDate(endDate);
    }

    public Recurrence() {
        this(Type.NONE, 1, new TreeSet<Integer>(), Helper.getCurrentTimestamp(), Long.MAX_VALUE);
    }

    public Recurrence(Recurrence recurrence) {
        this(recurrence.type, recurrence.increment, recurrence.selection, recurrence.startDate, recurrence.endDate);
    }

    public Recurrence(Parcel parcel) {
        this(
                Type.fromInt(parcel.readInt()),
                parcel.readInt(),
                decodeSelection(parcel.readInt()),
                parcel.readLong(),
                parcel.readLong()
        );
    }

    // Overrides

    /**
     * @see Parcelable
     */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type.getValue());
        dest.writeInt(increment);
        dest.writeList(new ArrayList<>(selection));
        dest.writeLong(startDate);
        dest.writeLong(endDate);
    }

    /**
     * @see Object
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Recurrence)) return false;
        Recurrence that = (Recurrence) o;
        return type == that.type &&
                increment == that.increment &&
                selection.equals(that.selection) &&
                startDate == that.startDate &&
                endDate == that.endDate;
    }

    public String toString(Context context) {
        Resources res = context.getResources();
        String unit;
        switch (type) {
            case DAILY:
                if (increment == 1) return context.getResources().getString(R.string.daily);
                unit = res.getString(R.string.days);
                break;
            case WEEKLY:
                if (increment == 1) return context.getResources().getString(R.string.weekly);
                unit = res.getString(R.string.weeks);
                break;
            case MONTHLY:
                if (increment == 1) return context.getResources().getString(R.string.monthly);
                unit = res.getString(R.string.months);
                break;
            case YEARLY:
                if (increment == 1) return context.getResources().getString(R.string.annual);
                unit = res.getString(R.string.years);
                break;
            case WEEKDAYS:
            case SOME_MONTHS:
                if (selection.isEmpty())
                    return context.getResources().getString(R.string.no_recurrence);
                StringBuilder selectedOptionsBuilder = new StringBuilder();
                for (int i : selection) {
                    try {
                        RecurrenceSelectionOption thisOption = recurrenceSelectionOptionFromInt(i);
                        selectedOptionsBuilder.append(thisOption.toString(context)).append(", ");
                    } catch (IllegalArgumentException iae) {
                        if (iae.getMessage().equals("No such weekday defined.")) {
                            Log.e("Recurrence", "illegal option", iae);
                        } else throw iae;
                    }
                }
                String selectedOptions = selectedOptionsBuilder.toString();
                unit = selectedOptions.substring(0, selectedOptions.length() - 2);
                break;
            default:
                return res.getString(R.string.no_recurrence);
        }
        // TODO remove unnecessary 1 for selective recurrence
        /* TODO
            fix Japanese version of selective Recurrence display:
            short versions for every Xth Monday are:
                X月
            this is highly confusing, since this also means "the Xth month".
            Also X1月 would be displayed for every Xth January, which is far from correct.
                In fact it simply means November if X=1
         */
        return res.getString(R.string.every_X_units, increment, unit);
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

    public void setSelection(Collection<Integer> selection) {
        this.selection = new TreeSet<>(selection);
    }

    private Integer verifySelectable(Integer option) {
        switch (type) {
            case WEEKDAYS:
                if (option < 1 || 8 <= option)
                    throw new IllegalArgumentException("Invalid option: " + option + ", must be at least 1 (Sunday) and at most 7 (Saturday).");
                break;
            case SOME_MONTHS:
                if (option < 0 || 12 <= option)
                    throw new IllegalArgumentException("Invalid option: " + option + ", must be at least 0 and at most 11.");
                break;
            default:
                throw new IllegalStateException("Selection can only be modified in selective recursion.");
        }
        return option;
    }

    public boolean addToSelection(Integer option) {
        return selection.add(verifySelectable(option));
    }

    public boolean removeFromSelection(Integer option) {
        return selection.remove(option);
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
        if (lastDeadline == -1) return -1;
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(TimeUnit.SECONDS.toMillis(lastDeadline));

        long deadline = TimeUnit.MILLISECONDS.toSeconds(calendar.getTimeInMillis());
        if (deadline < startDate)
            return startDate;

        incrementCalendar(calendar);

        deadline = TimeUnit.MILLISECONDS.toSeconds(calendar.getTimeInMillis());
        long now = Helper.getCurrentTimestamp();
        if (deadline < now && !allowPastRecurrence)
            deadline = now;
        if (deadline > endDate) return -1;
        return deadline;
    }

    private void incrementCalendar(Calendar calendar) {
        switch (type) {
            case DAILY:
                calendar.add(Calendar.DATE, increment);
                break;
            case WEEKLY:
                calendar.add(Calendar.DATE, 7 * increment);
                break;
            case MONTHLY:
                calendar.add(Calendar.MONTH, increment);
                break;
            case YEARLY:
                calendar.add(Calendar.YEAR, increment);
                break;
            case WEEKDAYS:
                incrementSelective(calendar, Calendar.DAY_OF_WEEK, Calendar.DATE, calendar.getFirstDayOfWeek(), Calendar.DATE, 7);
                break;
            case SOME_MONTHS:
                incrementSelective(calendar, Calendar.MONTH, Calendar.MONTH, 1, Calendar.YEAR, 1);
                break;
            default:
                calendar.setTimeInMillis(-1000);
        }
    }

    private void incrementSelective(
            Calendar cursor, int criteriaField, int addField, int firstInSpan, int spanField, int spanIncrement
    ) {
        if (selection.isEmpty()) {
            cursor.setTimeInMillis(-1000);
            return;
        }
        while (cursor.get(criteriaField) != firstInSpan) {
            cursor.add(addField, 1);
            if (!isSelected(cursor.get(criteriaField)))
                break;
        }
        if (cursor.get(criteriaField) == firstInSpan) {
            cursor.add(spanField, spanIncrement * increment);
            while (!isSelected(cursor.get(criteriaField)))
                cursor.add(addField, 1);
        }
    }

    public boolean isSelected(int indicator) {
        return selection.contains(indicator);
    }

    public boolean hasEnded() {
        return endDate < Helper.getCurrentTimestamp();
    }

    public boolean isSelective() {
        return type == Type.WEEKDAYS || type == Type.SOME_MONTHS;
    }

    public boolean isEffectivelyNONE() {
        return type == Type.NONE || hasEnded() || (isSelective() && selection.isEmpty());
    }

    public int encodeSelection() {
        int encodedSelections = 0;
        for (int i = 30; i > 0; i--) {
            if (isSelected(i)) encodedSelections++;
            encodedSelections = encodedSelections << 1;
        }
        if (isSelected(0)) encodedSelections++;
        return encodedSelections;
    }

    public static TreeSet<Integer> decodeSelection(int encodedSelection) {
        TreeSet<Integer> selection = new TreeSet<>();
        for (int i = 0; i < 31; i++) {
            if (encodedSelection % 2 == 1) selection.add(i);
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
