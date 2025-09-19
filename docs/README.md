# XiaoDu User Guide

XiaoDu is a personal task management chatbot with a graphical user interface built using JavaFX.

## Getting Started

1. Run the application using `./gradlew run`
2. The GUI will open with XiaoDu ready to help you manage your tasks
3. Type commands in the text field and press Enter or click Send

## Features

### Adding Tasks

**Todo Tasks**
```
todo read book
todo buy groceries
```

**Deadline Tasks** 
```
deadline homework /by 2025-12-01
deadline project submission /by 2025-11-30
```

**Event Tasks**
```
event meeting /from 2pm /to 4pm
event conference /from Monday /to Friday
```

### Managing Tasks

**View All Tasks**
```
list
```

**Mark Tasks as Done**
```
mark 1
mark 3
```

**Mark Tasks as Not Done**
```
unmark 2
```

**Delete Tasks**
```
delete 1
delete 5
```

### Finding Tasks

**Search by Keyword**
```
find book
find meeting
```

### View Schedule

**View Tasks for Specific Date**
```
schedule 2025-09-20
schedule 2025-12-01
```

### Exit Application

```
bye
```

## Data Storage

XiaoDu automatically saves your tasks to `./data/xiaoDu.txt` and loads them when you restart the application.