-- drop column description from todo_list
PRAGMA foreign_keys=off;
BEGIN TRANSACTION;
ALTER TABLE todo_task RENAME TO temp_table;
CREATE TABLE todo_task (
            _id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            description TEXT NOT NULL,

            todo_list_id INTEGER NOT NULL,
            position_in_todo_list INTEGER NOT NULL,

            priority INTEGER NOT NULL DEFAULT 0,

            progress INTEGER NOT NULL DEFAULT 0,
            done INTEGER NOT NULL DEFAULT 0,
            in_trash INTEGER NOT NULL DEFAULT 0,

            deadline DATETIME DEFAULT NULL,
            type_of_recurrence INTEGER NOT NULL DEFAULT 0,
            encoded_recurrence_selection INTEGER NOT NULL DEFAULT 0,

            deadline_warning_time NUMERIC NULL DEFAULT NULL,

            num_subtasks NOT NULL DEFAULT 0,

            FOREIGN KEY (todo_list_id) REFERENCES todo_list(_id)
);
INSERT INTO todo_task (_id, name, description, todo_list_id, position_in_todo_list, priority, progress, done, in_trash, deadline, deadline_warning_time)
  SELECT _id, name, description, todo_list_id, position_in_todo_list, priority, progress, done, in_trash, deadline, deadline_warning_time
  FROM temp_table;
DROP TABLE temp_table;
COMMIT;
PRAGMA foreign_keys=on;