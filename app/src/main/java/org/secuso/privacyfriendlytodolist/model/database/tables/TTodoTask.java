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

package org.secuso.privacyfriendlytodolist.model.database.tables;

/**
 * Created by Sebastian Lutz on 12.03.2018.
 * Altered by Ben Westerath on 25.04.2021.
 * <p>
 * This class is responsible to define sql table of To-Do tasks.
 */
public class TTodoTask {

    // TAG
    private static final String TAG = TTodoList.class.getSimpleName();

    // columns + tablename
    public static final String TABLE_NAME = "todo_task";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";

    public static final String COLUMN_TODO_LIST_ID = "todo_list_id";
    public static final String COLUMN_LIST_POSITION = "position_in_todo_list";

    public static final String COLUMN_PRIORITY = "priority";

    public static final String COLUMN_PROGRESS = "progress";
    public static final String COLUMN_DONE = "done";
    public static final String COLUMN_TRASH = "in_trash";

    public static final String COLUMN_DEADLINE = "deadline";
    public static final String COLUMN_RECURRENCE_TYPE = "type_of_recurrence";
    public static final String COLUMN_RECURRENCE_INCREMENT = "recurrence_increment";
    public static final String COLUMN_RECURRENCE_SELECTION = "encoded_recurrence_selection";
    public static final String COLUMN_RECURRENCE_START_DATE = "recurrence_start_date";
    public static final String COLUMN_RECURRENCE_END_DATE = "recurrence_end_date";

    public static final String COLUMN_DEADLINE_WARNING_TIME = "deadline_warning_time"; // absolute value in seconds

    public static final String COLUMN_NUM_SUBTASKS = "num_subtasks";


    // sql table creation
    public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT NOT NULL, " +
            COLUMN_DESCRIPTION + " TEXT NOT NULL, " +

            COLUMN_TODO_LIST_ID + " INTEGER NOT NULL, " +
            COLUMN_LIST_POSITION + " INTEGER NOT NULL, " +

            COLUMN_PRIORITY + " INTEGER NOT NULL DEFAULT 0, " +

            COLUMN_PROGRESS + " INTEGER NOT NULL DEFAULT 0, " +
            COLUMN_DONE + " INTEGER NOT NULL DEFAULT 0, " +
            COLUMN_TRASH + " INTEGER NOT NULL DEFAULT 0, " +

            COLUMN_DEADLINE + " DATETIME DEFAULT NULL, " +
            COLUMN_RECURRENCE_TYPE + " INTEGER NOT NULL DEFAULT 0, " +
            COLUMN_RECURRENCE_INCREMENT + " INTEGER NOT NULL DEFAULT 1, " +
            COLUMN_RECURRENCE_SELECTION + " INTEGER NOT NULL DEFAULT 0, " +
            COLUMN_RECURRENCE_START_DATE + " SIGNED BIGINT NOT NULL DEFAULT 0, " +
            COLUMN_RECURRENCE_END_DATE + " SIGNED BIGINT NOT NULL DEFAULT " + Long.toString(Long.MAX_VALUE).replace("L", "") + ", " +

            COLUMN_DEADLINE_WARNING_TIME + " NUMERIC NULL DEFAULT NULL, " +

            COLUMN_NUM_SUBTASKS + "INTEGER NOT NULL DEFAULT 0, " +

            "FOREIGN KEY (" + COLUMN_TODO_LIST_ID + ") REFERENCES " + TTodoList.TABLE_NAME + "(" + TTodoList.COLUMN_ID + "));";
}
