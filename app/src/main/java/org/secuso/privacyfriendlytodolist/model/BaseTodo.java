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

import org.secuso.privacyfriendlytodolist.model.database.DBQueryHandler;

public abstract class BaseTodo {

    // Enums
    public enum CopyMode {
        CLONE, RESET, NEXT
    }

    // Instance variables
    protected int id;
    protected String name, description;
    protected int progress;

    protected DBQueryHandler.ObjectStates dbState;

    // Constructors
    public BaseTodo() {
        dbState = DBQueryHandler.ObjectStates.NO_DB_ACTION;
    }

    public BaseTodo(BaseTodo baseTodo, CopyMode mode) {
        this.name = baseTodo.name;
        this.description = baseTodo.description;
        dbState = DBQueryHandler.ObjectStates.NO_DB_ACTION;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }


    public DBQueryHandler.ObjectStates getDBState() {
        return dbState;
    }

    public void setCreated() {
        this.dbState = DBQueryHandler.ObjectStates.INSERT_TO_DB;
    }

    public void setChanged() {
        if (this.dbState == DBQueryHandler.ObjectStates.NO_DB_ACTION)
            this.dbState = DBQueryHandler.ObjectStates.UPDATE_DB;
    }

    public void setChangedFromPomodoro() {
        this.dbState = DBQueryHandler.ObjectStates.UPDATE_FROM_POMODORO;
    }

    public void setUnchanged() {
        this.dbState = DBQueryHandler.ObjectStates.NO_DB_ACTION;
    }

}
