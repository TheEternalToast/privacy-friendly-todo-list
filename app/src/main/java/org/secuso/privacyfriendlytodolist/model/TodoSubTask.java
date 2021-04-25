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

/**
 * Created by Sebastian Lutz on 12.03.2018.
 * Altered by Ben Westerath on 25.04.2021.
 *
 * Class to set up To-Do subtasks and its parameters.
 */

public class TodoSubTask extends BaseTodo implements Parcelable {

    // Class variables
    public static final Parcelable.Creator<TodoSubTask> CREATOR =
            new Creator<TodoSubTask>() {
                @Override
                public TodoSubTask createFromParcel(Parcel source) {
                    return new TodoSubTask(source);
                }

                @Override
                public TodoSubTask[] newArray(int size) {
                    return new TodoSubTask[size];
                }
            };

    // Instance variables
    private String name;
    private long taskIdForeignKey;

    private int progress = -1;
    private boolean done;
    private boolean inTrash;

    // Constructors
    public TodoSubTask() {
        super();
        done = false;
        inTrash = false;
    }

    public TodoSubTask(TodoSubTask todoSubTask, CopyMode mode){
        super(todoSubTask, mode);
        this.taskIdForeignKey = todoSubTask.taskIdForeignKey;
        if (mode == CopyMode.CLONE){
            this.done = todoSubTask.done;
            this.inTrash = todoSubTask.inTrash;
            this.progress = todoSubTask.progress;
        } else {
            done = false;
            inTrash = false;
        }
    }

    public TodoSubTask(Parcel parcel) {
        id = parcel.readInt();
        name = parcel.readString();
        done = parcel.readByte() != 0;
        inTrash = parcel.readByte() != 0;
        taskIdForeignKey = parcel.readLong();
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
        dest.writeByte((byte) (done ? 1 : 0));
        dest.writeByte((byte) (inTrash ? 1 : 0));
        dest.writeLong(taskIdForeignKey);
    }

    // Getters & Setters
    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTaskId() {
        return taskIdForeignKey;
    }

    public void setTaskId(long taskIdForeignKey) {
        this.taskIdForeignKey = taskIdForeignKey;
    }


    @Override
    public int getProgress() {
        return progress;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean d) {
        done = d;
    }

    public boolean isInTrash() {return inTrash; }

    public void setInTrash(boolean a) { inTrash = a; }


    // Functional methods
    public boolean checkQueryMatch(String query)
    {
        // no query? always match!
        if(query == null || query.isEmpty())
            return true;

        String queryLowerCase = query.toLowerCase();
        if(this.name.toLowerCase().contains(queryLowerCase))
            return true;
        return false;
    }
}
