package org.secuso.privacyfriendlytodolist.model;

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
public class Recurrence {

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
    }

    // Instance variables
    protected Type type;
    protected Set<Integer> selections;

    // Constructors
    public Recurrence(Type type, Set<Integer> selections) {
        this.type = type;
        this.selections = selections;
    }

    // Overrides
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Recurrence)) return false;
        Recurrence that = (Recurrence) o;
        return type == that.type &&
                selections.equals(that.selections);
    }

    @Override
    public int hashCode() {
        return (31 + type.hashCode()) * 31 + selections.hashCode();
    }

    // Functional methods
    public long next(long lastDeadline) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(TimeUnit.SECONDS.toMillis(lastDeadline));

        long now = Helper.getCurrentTimestamp();
        while (TimeUnit.MILLISECONDS.toSeconds(calendar.getTimeInMillis()) < now) {
            calendar.add(Calendar.DATE, 1);
        }

        boolean inSelection;
        switch (type) {
            case DAILY:
                calendar.add(Calendar.DATE, 1);
                break;
            case WEEKLY:
                calendar.add(Calendar.DATE, 7);
                break;
            case MONTHLY:
                calendar.add(Calendar.MONTH, 1);
                break;
            case YEARLY:
                calendar.add(Calendar.YEAR, 1);
                break;
            case WEEKDAYS:
                if (selections.isEmpty()) return 0;
                do {
                    calendar.add(Calendar.DATE, 1);
                    inSelection = isSelected(calendar.get(Calendar.DAY_OF_WEEK));
                } while (!inSelection);
                break;
            case SOME_MONTHS:
                if (selections.isEmpty()) return 0;
                do {
                    calendar.add(Calendar.MONTH, 1);
                    inSelection = isSelected(calendar.get(Calendar.MONTH));
                } while (!inSelection);
                break;
            default:
                return 0;
        }
        return TimeUnit.MILLISECONDS.toSeconds(calendar.getTimeInMillis());
    }

    public boolean isSelected(int indicator) {
        return selections.contains(indicator);
    }

    public int encodeSelection() {
        int encodedSelections = 0;
        for (int i = 30; i >= 0; i--) {
            if (isSelected(i)) encodedSelections++;
            encodedSelections = encodedSelections << 1;
        }
        return encodedSelections;
    }

    public static Set<Integer> decodeSelection(int encodedSelection){
        Set<Integer> selection = new HashSet<>();
        for (int i = 0; i < 31; i++) {
            if (encodedSelection % 2 == 1)
                selection.add(i);
            encodedSelection = encodedSelection >> 1;
        }
        return selection;
    }
}
