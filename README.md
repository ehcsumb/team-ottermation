# Team Ottermation

Productivity Tracker
Design Document — Team Ottermation
Khiem Vo 
Loi Tong
Eric Holm
David Renteria


GitHub: github.com/ehcsumb/team-ottermation
Stack: JavaFX · FXML · SQLite (JDBC) · Gradle · JUnit 5

1. Application Overview
Productivity Tracker is a JavaFX desktop app for managing tasks. Regular users register, log in, create/edit/complete tasks with due dates and priorities. Admin users manage global settings (task limits, default priority, allowed task types). All data persists in SQLite across 5 tables.

2. Use Cases

Khiem
User Registration

Main Flow (summary)
Fill username/password on Register scene; validate uniqueness; save to DB; return to Login.

Alternate Flow
Username taken: error alert, retry.

Khiem
User Login

Main Flow (summary)
Enter credentials; validate against DB; route to User or Admin dashboard by role.

Alternate Flow
Invalid credentials: error alert.


Eric
Add Task

Main Flow (summary)
Fill title, description, due date, priority, type; save to tasks table.

Alternate Flow
Task limit reached: alert shown.

Eric
View Tasks

Main Flow (summary)
Query tasks for logged-in user; display in TableView.

Alternate Flow
No tasks: prompt to add one.

Loi
Edit Task

Main Flow (summary)
Select task, edit fields, save; update DB record.

Alternate Flow
Cancel: no DB changes.

Loi
Mark Complete

Main Flow (summary)
Click Mark Complete; set status=Complete in DB; refresh list.

Alternate Flow
Already complete: Reopen option.

David
Update Settings

Main Flow (summary)
Edit max tasks, default priority; save to settings table.

Alternate Flow
Invalid value: validation error.

David
Manage Task Types

Main Flow (summary)
Add/enable/disable task types; changes reflect in user dropdowns.

Alternate Flow
Duplicate name: error alert.

3. Entity Relationship Diagram

Table          Key Fields                                                                            Relationships
users          userId (PK), username, password, role                                                 1 user : N tasks
tasks          taskId (PK), userId (FK), title, description, dueDate, priority, status, taskType     N:1 user, 1:N, ref task_types
settings       settingId (PK), maxTasks, defaultPriority                                             Global config (standalone)
task_types     typeId (PK), typeName, isEnabled                                                      Referenced by tasks.taskType

4. Testing Plan

Area                      Key Test Cases                                                                  Owner
UserDAO                   Create user, find by username, reject duplicate, validate login                 Mem 1
TaskDAO                   Insert task, get tasks by user, update task, delete task                        Mem 2
Task Status               Mark complete, reopen task, edit fields and verify persistence                  Mem 3
Settings/Types            Load defaults, update maxTasks, add type, disable type                          Mem 4

5. Scene Mockups

Scene                                          Layout Description
Login                                          Username field, password field, Log In button, Register button.
Register                                       Username, password, confirm password fields, Create Account button, Back to Login.
User Dashboard                                 Welcome message, Add Task button, View Tasks button, Log Out button, recent tasks list.
Add Task                                       Title, description, due date (DatePicker), priority dropdown, task type dropdown, Save and Cancel buttons.
Task List                                      TableView showing title, due date, priority, status. Edit button per row. Back to Dashboard.
Edit Task                                      Pre-filled fields for title, description, due date, priority, status. Save, Mark Complete, Cancel buttons.
Admin Dashboard                                Settings button, Manage Task Types button, Log Out, system overview stats.
Admin Settings                                 Max tasks field, default priority dropdown, Save and Back buttons.

6. Team Breakdown & GitHub Process
Member             Features                                        Example Issues                                                      Example Branches
Khiem              Login, Registration, SceneManager, DB setup     Create users table; Build login FXML; Add registration validation   name/login-scene, name/register-scene, name/db-setup
Loi                User Dashboard, Edit Task                       Create tasks table; Build add-task scene; TaskDAO insert/query      name/user-dashboard, name/add-task, name/task-dao 
Eric               Task List, Add Task, Mark Complete              Build task list TableView; Edit task scene; Mark complete logic     name/task-list, name/edit-task, name/task-details
David              Admin Dashboard, Settings, Task Types           Create settings table; Admin settings scene; Task type mgmt         name/admin-dashboard, name/admin-settings, name/task-types



