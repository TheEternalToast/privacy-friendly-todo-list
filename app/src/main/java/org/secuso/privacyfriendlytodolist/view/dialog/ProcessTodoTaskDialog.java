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

package org.secuso.privacyfriendlytodolist.view.dialog;

import android.content.Context;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.secuso.privacyfriendlytodolist.R;
import org.secuso.privacyfriendlytodolist.model.Helper;
import org.secuso.privacyfriendlytodolist.model.Recurrence;
import org.secuso.privacyfriendlytodolist.model.TodoList;
import org.secuso.privacyfriendlytodolist.model.TodoTask;
import org.secuso.privacyfriendlytodolist.model.database.DBQueryHandler;
import org.secuso.privacyfriendlytodolist.model.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sebastian Lutz on 12.03.2018.
 * Altered by Ben Westerath on 25.04.2021.
 * <p>
 * This class creates a dialog that lets the user create/edit a task.
 */

public class ProcessTodoTaskDialog extends FullScreenDialog {

    // Instance variables

    // Views
    private TextView prioritySelector;
    private TextView deadlineTextView;
    private RelativeLayout recurrenceView;
    private TextView recurrenceTextView;
    private TextView reminderTextView;
    private TextView listSelector;
    private TextView dialogTitleNew;
    private TextView dialogTitleEdit;
    private TextView progressText;
    private TextView progressPercent;

    private RelativeLayout progress_layout;

    private SeekBar progressSelector;
    private EditText taskName;
    private EditText taskDescription;

    // Values
    private TodoTask.Priority taskPriority = null;
    private int selectedListID;
    private List<TodoList> lists = new ArrayList<>();
    private DatabaseHelper dbHelper;
    private int taskProgress = 0;
    private String name, description;
    private long deadline = -1;
    private long reminderTime = -1;

    private TodoTask.Priority defaultPriority = TodoTask.Priority.MEDIUM;

    private TodoTask task;

    // Constructors
    public ProcessTodoTaskDialog(final Context context) {
        super(context, R.layout.add_task_dialog);

        initGui();
        task = new TodoTask();
        task.setCreated();
        //task.setDbState(DBQueryHandler.ObjectStates.INSERT_TO_DB);
    }

    public ProcessTodoTaskDialog(Context context, TodoTask task) {
        super(context, R.layout.add_task_dialog);

        initGui();
        task.setChanged();
        //task.setDbState(DBQueryHandler.ObjectStates.UPDATE_DB);
        deadline = task.getDeadline();
        reminderTime = task.getReminderTime();
        taskName.setText(task.getName());
        taskDescription.setText(task.getDescription());
        prioritySelector.setText(Helper.priority2String(context, task.getPriority()));
        taskPriority = task.getPriority();
        progressSelector.setProgress(task.getProgress());

        if (task.getDeadline() <= 0)
            deadlineTextView.setText(context.getString(R.string.no_deadline));
        else
            deadlineTextView.setText(Helper.getDate(context, deadline));
        recurrenceTextView.setText(task.getRecurrence().toString(getContext()));
        if (task.getReminderTime() <= 0)
            reminderTextView.setText(context.getString(R.string.reminder));
        else
            reminderTextView.setText(Helper.getDateTime(context, reminderTime));
        adjustRecurrenceViewToDeadline();
        this.task = task;
    }

    // Inits
    private void initGui() {
        initPrioritySelector();
        initTitles();
        initListSelector();
        initProgressBar();
        initOkayButton();
        initCancelButton();

        // initialize TextViews to get deadline and reminder time
        initDeadline();
        initRecurrence();
        initReminder();

        taskName = (EditText) findViewById(R.id.et_new_task_name);
        taskDescription = (EditText) findViewById(R.id.et_new_task_description);
    }

    //  TextView that displays the selected priority
    private void initPrioritySelector() {
        prioritySelector = (TextView) findViewById(R.id.tv_new_task_priority);
        prioritySelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerForContextMenu(prioritySelector);
                openContextMenu(prioritySelector);
            }
        });
        prioritySelector.setOnCreateContextMenuListener(this);
        taskPriority = defaultPriority;
        prioritySelector.setText(Helper.priority2String(getContext(), taskPriority));
    }

    //  titles of the dialog
    private void initTitles() {
        dialogTitleNew = (TextView) findViewById(R.id.dialog_title);
        dialogTitleEdit = (TextView) findViewById(R.id.dialog_edit);
    }

    //  TextView that displays selected list
    private void initListSelector() {
        listSelector = (TextView) findViewById(R.id.tv_new_task_listchoose);
        listSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerForContextMenu(listSelector);
                openContextMenu(listSelector);
            }
        });
        listSelector.setOnCreateContextMenuListener(this);
    }

    //  progress bar
    private void initProgressBar() {
        progressText = (TextView) findViewById(R.id.tv_task_progress);
        progressPercent = (TextView) findViewById(R.id.new_task_progress);
        progress_layout = (RelativeLayout) findViewById(R.id.progress_relative);

        // initialize SeekBar that allows to select the progress
        final TextView selectedProgress = (TextView) findViewById(R.id.new_task_progress);
        progressSelector = (SeekBar) findViewById(R.id.sb_new_task_progress);

        if (!hasAutoProgress()) {
            progressSelector.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    taskProgress = progress;
                    selectedProgress.setText(String.valueOf(progress) + "%");
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        } else {
            progress_layout.setVisibility(View.GONE);
        }
    }

    //  buttons
    private void initOkayButton() {
        Button okayButton = (Button) findViewById(R.id.bt_new_task_ok);
        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = taskName.getText().toString();
                String description = taskDescription.getText().toString();

                String listName = listSelector.getText().toString();

                if (name.equals("")) {
                    Toast.makeText(getContext(), getContext().getString(R.string.todo_name_must_not_be_empty), Toast.LENGTH_SHORT).show();
                } /* else if (listName.equals(getContext().getString(R.string.click_to_choose))) {
                    Toast.makeText(getContext(), getContext().getString(R.string.to_choose_list), Toast.LENGTH_SHORT).show();
                } */ else {

                    task.setName(name);
                    task.setDescription(description);
                    task.setDeadline(deadline);
                    task.setPriority(taskPriority);
                    task.setListId(selectedListID);
                    task.setProgress(taskProgress);
                    task.setReminderTime(reminderTime);
                    callback.finish(task);
                    ProcessTodoTaskDialog.this.dismiss();
                }
            }
        });
    }

    private void initCancelButton() {
        Button cancelButton = (Button) findViewById(R.id.bt_new_task_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ProcessTodoTaskDialog.this.dismiss();
            }
        });
    }

    //  deadline view
    private void initDeadline() {
        RelativeLayout deadlineView = findViewById(R.id.rl_todo_task_deadline);
        deadlineTextView = deadlineView.findViewById(R.id.tv_todo_task_deadline_text);
        deadlineView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog deadlineDialog = new DatePickerDialog(
                        getContext(),
                        deadline,
                        R.string.no_deadline
                );
                deadlineDialog.setCallback(new DatePickerDialog.DateCallback() {
                    @Override
                    public void setDate(long d) {
                        deadline = d;
                        deadlineTextView.setText(Helper.getDate(getContext(), deadline));
                        adjustRecurrenceViewToDeadline();
                    }

                    @Override
                    public void removeDate() {
                        deadline = -1;
                        deadlineTextView.setText(getContext().getResources().getString(R.string.deadline));
                        task.setRecurrence(new Recurrence());
                        adjustRecurrenceViewToDeadline();
                    }
                });
                deadlineDialog.show();
            }
        });
    }

    //  recurrence view
    private void initRecurrence() {
        recurrenceView = findViewById(R.id.rl_todo_task_recurrence);
        recurrenceTextView = recurrenceView.findViewById(R.id.tv_todo_task_recurrence_text);
        recurrenceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecurrenceDialog recurrenceDialog = new RecurrenceDialog(getContext(), task.getRecurrence());
                recurrenceDialog.setCallback(new RecurrenceDialog.RecurrenceCallback() {
                    @Override
                    public void setRecurrence(Recurrence recurrence) {
                        task.setRecurrence(recurrence);
                        recurrenceTextView.setText(recurrence.toString(getContext()));
                    }

                    @Override
                    public void removeRecurrence() {
                        task.setRecurrence(new Recurrence());
                        recurrenceTextView.setText(getContext().getResources().getText(R.string.no_recurrence));
                    }
                });
                recurrenceDialog.show();
            }
        });
        adjustRecurrenceViewToDeadline();
    }

    //  reminder view
    private void initReminder() {
        RelativeLayout reminderView = findViewById(R.id.rl_todo_task_reminder);
        reminderTextView = reminderView.findViewById(R.id.tv_todo_task_reminder_text);
        reminderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimePickerDialog reminderDialog = new DateTimePickerDialog(getContext(), reminderTime, deadline, R.string.no_reminder);
                reminderDialog.setCallback(new DateTimePickerDialog.DateTimeCallback() {
                    @Override
                    public void setDateTime(long reminder) {

                        /*if(deadline == -1) {
                            Toast.makeText(getContext(), getContext().getString(R.string.set_deadline_before_reminder), Toast.LENGTH_SHORT).show();
                            return;
                        }*/

                        if (deadline != -1 && Helper.endOfDay(deadline) < reminder) {
                            Toast.makeText(getContext(), getContext().getString(R.string.deadline_smaller_reminder), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        reminderTime = reminder;
                        reminderTextView.setText(
                                reminderTime == -1 ? getContext().getResources().getString(R.string.no_reminder) : Helper.getDateTime(getContext(), reminderTime)
                        );
                    }

                    @Override
                    public void removeDateTime() {
                        reminderTime = -1;
                        reminderTextView.setText(getContext().getResources().getString(R.string.reminder));
                    }
                });
                reminderDialog.show();
            }
        });
    }

    // Overrides

    /**
     * @see android.app.Dialog
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        switch (v.getId()) {
            case R.id.tv_new_task_priority:
                menu.setHeaderTitle(R.string.select_priority);
                for (TodoTask.Priority prio : TodoTask.Priority.values()) {
                    menu.add(Menu.NONE, prio.getValue(), Menu.NONE, Helper.priority2String(getContext(), prio));

                }
                break;

            case R.id.tv_new_task_listchoose:
                menu.setHeaderTitle(R.string.select_list);
                updateLists();
                menu.add(Menu.NONE, -3, Menu.NONE, R.string.select_no_list);
                for (TodoList tl : lists) {
                    //+3 so that IDs are non-overlapping with prio-IDs
                    menu.add(Menu.NONE, tl.getId() + 3, Menu.NONE, tl.getName());
                }
                break;
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int numValues = TodoTask.Priority.values().length;
        if (item != null && item.getItemId() < numValues && item.getItemId() >= 0) {
            taskPriority = TodoTask.Priority.values()[item.getItemId()];
            prioritySelector.setText(Helper.priority2String(getContext(), taskPriority));
        }

        for (TodoList tl : lists) {
            if (item.getItemId() - 3 == tl.getId()) {
                this.selectedListID = tl.getId();
                listSelector.setText(tl.getName());
            } else if (item.getTitle() == getContext().getString(R.string.to_choose_list) || item.getTitle() == getContext().getString(R.string.select_no_list)) {
                this.selectedListID = -3;
                listSelector.setText(item.getTitle());
            }
        }

        return super.onMenuItemSelected(featureId, item);
    }


    //updates the lists array
    public void updateLists() {
        dbHelper = DatabaseHelper.getInstance(getContext());
        lists = DBQueryHandler.getAllToDoLists(dbHelper.getReadableDatabase());
    }


    //change the dialogue title from "new task" to "edit task"
    public void titleEdit() {
        dialogTitleNew.setVisibility(View.GONE);
        dialogTitleEdit.setVisibility(View.VISIBLE);

    }

    //sets the textview either to listname in context or if no context to default
    public void setListSelector(int id, boolean idExists) {
        updateLists();
        for (TodoList tl : lists) {
            if (id == tl.getId() && idExists == true) {
                listSelector.setText(tl.getName());
                selectedListID = tl.getId();
            } else if (!idExists) {
                listSelector.setText(getContext().getString(R.string.click_to_choose));
                selectedListID = -3;
            }
        }

    }

    private boolean hasAutoProgress() {
        //automatic-progress enabled?
        if (!PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("pref_progress", false))
            return false;
        return true;
    }

    private void adjustRecurrenceViewToDeadline() {
        if (deadline == -1)
            recurrenceView.setVisibility(View.GONE);
        else recurrenceView.setVisibility(View.VISIBLE);
    }

    private void autoProgress() {
        int size = task.getSubTasks().size();
        int i = 5;
        int j = 3;
        double computedProgress = ((double) j / (double) i) * 100;
        taskProgress = (int) computedProgress;
        progressPercent.setText(String.valueOf(computedProgress) + "%");
    }
}
