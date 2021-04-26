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

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.secuso.privacyfriendlytodolist.model.database.DBQueryHandler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Sebastian Lutz on 12.03.2018.
 * Altered by Ben Westerath on 25.04.2021.
 * <p>
 * Class to set up To-Do Tasks and its parameters.
 */

public class TodoTask extends BaseTodo implements Parcelable {

    // Enums
    public enum Priority {
        HIGH(0), MEDIUM(1), LOW(2); // Priority steps must be sorted in the same way like they will be displayed

        private final int value;

        Priority(final int newValue) {
            value = newValue;
        }

        public int getValue() {
            return value;
        }

        public static Priority fromInt(int i) {
            for (Priority p : Priority.values()) {
                if (p.getValue() == i) {
                    return p;
                }
            }
            throw new IllegalArgumentException("No such priority defined.");
        }
    }

    public enum DeadlineColors {
        BLUE, ORANGE, RED
    }

    // Class variables
    private static final String TAG = TodoTask.class.getSimpleName();
    public static final String PARCELABLE_KEY = "key_for_parcels";

    public static final Parcelable.Creator<TodoTask> CREATOR =
            new Creator<TodoTask>() {
                @Override
                public TodoTask createFromParcel(Parcel source) {
                    return new TodoTask(source);
                }

                @Override
                public TodoTask[] newArray(int size) {
                    return new TodoTask[size];
                }
            };

    // Instance variables
    private int listIdForeignKey;
    private String listName;
    private int listPosition; // indicates at what position inside the list this task it placed

    private Priority priority;

    private int progress;
    private boolean done;
    private boolean inTrash;

    protected long deadline; // absolute timestamp
    protected Recurrence recurrence;

    private long reminderTime = -1; // absolute timestamp
    private boolean reminderTimeChanged = false; // important for the reminder service
    private boolean reminderTimeWasInitialized = false;

    private ArrayList<TodoSubTask> subTasks = new ArrayList<TodoSubTask>();

    // Constructors
    public TodoTask() {
        super();
        done = false;
        inTrash = false;

        recurrence = new Recurrence();
    }

    public TodoTask(TodoTask todoTask, CopyMode mode) {
        super(todoTask, mode);

        this.listIdForeignKey = todoTask.listIdForeignKey;
        this.listName = todoTask.listName;
        this.listPosition = todoTask.listPosition;

        this.priority = todoTask.priority;

        if (mode == CopyMode.CLONE) {
            this.progress = todoTask.progress;
            this.done = todoTask.done;
            this.inTrash = todoTask.inTrash;
        } else {
            done = false;
            inTrash = false;
        }

        this.recurrence = new Recurrence(todoTask.recurrence);
        if (mode == CopyMode.NEXT) {
            this.deadline = todoTask.recurrence.next(todoTask.deadline);
            this.reminderTime = todoTask.recurrence.next(todoTask.reminderTime);
        } else {
            this.deadline = todoTask.deadline;
            this.reminderTime = todoTask.reminderTime;
        }

        ArrayList<TodoSubTask> subTasks = new ArrayList<>();
        for (TodoSubTask subTask : getSubTasks()) {
            subTasks.add(new TodoSubTask(subTask, mode));
        }
        this.subTasks = subTasks;

    }

    public TodoTask(Parcel parcel) {
        id = parcel.readInt();
        name = parcel.readString();
        description = parcel.readString();

        listIdForeignKey = parcel.readInt();
        listName = parcel.readString();
        listPosition = parcel.readInt();

        priority = Priority.fromInt(parcel.readInt());

        progress = parcel.readInt();
        done = parcel.readByte() != 0;
        inTrash = parcel.readByte() != 0;

        deadline = parcel.readLong();
        recurrence = parcel.readParcelable(Recurrence.class.getClassLoader());

        reminderTime = parcel.readLong();

        parcel.readList(subTasks, TodoSubTask.class.getClassLoader());
    }

    // Overrides
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(description);

        dest.writeInt(listIdForeignKey);
        dest.writeString(listName);
        dest.writeInt(listPosition);

        dest.writeInt(priority.getValue());

        dest.writeInt(progress);
        dest.writeByte((byte) (done ? 1 : 0));
        dest.writeByte((byte) (inTrash ? 1 : 0));

        dest.writeLong(deadline);
        dest.writeParcelable(recurrence, flags);

        dest.writeLong(reminderTime);

        dest.writeList(subTasks);
    }

    // Getters & Setters
    public int getListId() {
        return this.listIdForeignKey;
    }

    public void setListId(int listId) {
        this.listIdForeignKey = listId;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public int getListPosition() {
        return listPosition;
    }

    public void setListPosition(int listPosition) {
        this.listPosition = listPosition;
    }


    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }


    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean isInTrash() {
        return inTrash;
    }

    public void setInTrash(boolean inTrash) {
        this.inTrash = inTrash;
    }


    public boolean hasDeadline() {
        return deadline > 0;
    }

    public long getDeadline() {
        return deadline;
    }

    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }

    public Recurrence getRecurrence() {
        return recurrence;
    }

    public int getRecurrenceType() {
        return recurrence.type.getValue();
    }

    public int getEncodedRecurrenceSelection() {
        return recurrence.encodeSelection();
    }

    public void setRecurrence(Recurrence recurrence) {
        this.recurrence = recurrence;
    }


    public long getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(long reminderTime) {

        if (reminderTime > deadline && deadline > 0) {
            Log.i(TAG, "Reminder time must not be greater than the deadline.");
        } else {
            this.reminderTime = reminderTime;
        }

        // check if reminder time was already set and now changed -> important for reminder service

        if (reminderTimeWasInitialized)
            reminderTimeChanged = true;

        reminderTimeWasInitialized = true;
    }

    public boolean reminderTimeChanged() {
        return reminderTimeChanged;
    }

    public void resetReminderTimeChangedStatus() {
        reminderTimeChanged = false;
    }


    public ArrayList<TodoSubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(ArrayList<TodoSubTask> tasks) {
        this.subTasks = tasks;
    }

    // Functional methods

    // This method expects the deadline to be greater than the reminder time.
    public DeadlineColors getDeadlineColor(long defaultReminderTime) {

        // The default reminder time is a relative value in seconds (e.g. 86400s == 1 day)
        // The user specified reminder time is an absolute timestamp

        if (!done && deadline > 0) {

            long currentTimeStamp = Helper.getCurrentTimestamp();
            long remTimeToCalc = reminderTime > 0 ? deadline - reminderTime : defaultReminderTime;

            if ((currentTimeStamp >= (deadline - remTimeToCalc)) && (deadline > currentTimeStamp))
                return DeadlineColors.ORANGE;

            if ((currentTimeStamp > deadline) && (deadline > 0))
                return DeadlineColors.RED;
        }

        return DeadlineColors.BLUE;
    }

    public void setAllSubTasksDone(boolean doneSubTask) {
        for (TodoSubTask subTask : subTasks) {
            subTask.setDone(doneSubTask);
        }

    }

    // A task is done if the user manually sets it done or when all subtaks are done.
    // If a subtask is selected "done", the entire task might be "done" if by now all subtasks are done.
    public void doneStatusChanged() {
        boolean doneSubTasks = true;

        int i = 0;
        while (doneSubTasks && i < subTasks.size()) {
            doneSubTasks &= subTasks.get(i).isDone();
            i++;
        }


        if (doneSubTasks != done) {
            dbState = DBQueryHandler.ObjectStates.UPDATE_DB;

        }

        done = doneSubTasks;
    }

    public boolean checkQueryMatch(String query, boolean recursive) {
        // no query? always match!
        if (query == null || query.isEmpty())
            return true;

        String queryLowerCase = query.toLowerCase();
        if (this.name.toLowerCase().contains(queryLowerCase))
            return true;
        if (this.description.toLowerCase().contains(queryLowerCase))
            return true;
        if (recursive)
            for (int i = 0; i < this.subTasks.size(); i++)
                if (this.subTasks.get(i).checkQueryMatch(queryLowerCase))
                    return true;
        return false;
    }

    public boolean checkQueryMatch(String query) {
        return checkQueryMatch(query, true);
    }
}
