# Productivity / Task Tracker
## Team Otterman
* Khiem V
* Loi T
* Eric H
* David R

GitHub: https://github.com/ehcsumb/team-ottermation

Stack: JavaFX · FXML · SQLite (JDBC) · Gradle · JUnit 5

## Application Overview

Productivity Tracker is a JavaFX desktop app for managing tasks. Regular users register, log in, create/edit/complete tasks with due dates and priorities. Admin users manage global settings  (default priority, allowed task types). All data persists in SQLite across 5 tables.

## Use Cases

| Use Case | Owner | Main Flow | Alternate Flow                      |
|---|---|---|-------------------------------------|
| User Registration | Khiem | Fill username/password on Register scene; validate uniqueness; save to DB; return to Login. | Username taken: error alert, retry. |
| User Login | Khiem | Enter credentials; validate against DB; route to User or Admin dashboard by role. | Invalid credentials: error alert.   |
| Add Task | Eric | Fill title, description, due date, priority, type; save to tasks table. | Cancel                              |
| View Tasks | Eric | Query tasks for logged-in user; display in TableView. | Go back                             |
| Edit Task | Loi | Select task, edit fields, save; update DB record. | Cancel: no DB changes.              |
| Mark Complete | Loi | Click Mark Complete; set status=Complete in DB; refresh list. | Already complete: Reopen option.    |
| Update Settings | David | Edit max tasks, default priority; save to settings table. | Invalid value: validation error.    |
| Manage Task Types | David | Add/enable/disable task types; changes reflect in user dropdowns. | Duplicate name: error alert.        |

## Entity Relationship Diagram

| Table | Key Fields | Relationships |
|---|---|---|
| users | userId (PK), username, password, role | 1 user : N tasks |
| tasks | taskId (PK), userId (FK), title, description, dueDate, priority, status, taskType | N:1 user |
| settings | settingId (PK), maxTasks, defaultPriority | Global config (standalone) |
| task_types | typeId (PK), typeName, isEnabled | |

![ERD Diagram](/erd.png "ERD Diagram")

### [UML Diagrams](uml.md)

## Testing Plan

| Area | Key Test Cases | Owner   |
|---|---|---------|
| UserDAO | Create user, find by username, reject duplicate, validate login | Khiem V |
| TaskDAO | Insert task, get tasks by user, update task, delete task | Eric H  |
| Task Status | Mark complete, reopen task, edit fields and verify persistence | Loi T   |
| Settings/Types | Load defaults, update maxTasks, add type, disable type | David R |

## Scene Mockups

| Scene | Layout Description | Owner
|---|---|---|
| Login | Username field, password field, Log In button, Register button. | Khiem V
| Register | Username, password, confirm password fields, Create Account button, Back to Login. | Khiem V
| User Dashboard | Welcome message, Add Task button, View Tasks button, Log Out button, recent tasks list. | Loi T
| Add Task | Title, description, due date (DatePicker), priority dropdown, task type dropdown, Save and Cancel buttons. | Eric H
| Task List | TableView showing title, due date, priority, status. Edit button per row. Back to Dashboard. | Eric H
| Edit Task | Pre-filled fields for title, description, due date, priority, status. Save, Mark Complete, Cancel buttons. | Loi T
| Admin Dashboard | Settings button, Manage Task Types button, Log Out, system overview stats. | David R
| Admin Settings | Max tasks field, default priority dropdown, Save and Back buttons. | David R

## Team Breakdown & GitHub Process

| Member | Features | Example Issues | Example Branches |
|---|---|---|---|
| Khiem | Login, Registration, SceneManager, DB setup | Create users table; Build login FXML; Add registration validation | name/login-scene, name/register-scene, name/db-setup |
| Loi | User Dashboard, Edit Task | Create tasks table; Build add-task scene; TaskDAO insert/query | name/user-dashboard, name/add-task, name/task-dao |
| Eric | Task List, Add Task, Mark Complete | Build task list TableView; Edit task scene; Mark complete logic | name/task-list, name/edit-task, name/task-details |
| David | Admin Dashboard, Settings, Task Types | Create settings table; Admin settings scene; Task type mgmt | name/admin-dashboard, name/admin-settings, name/task-types |


