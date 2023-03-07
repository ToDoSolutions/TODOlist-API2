<div align="center">
<h1><a href="https://todolist-api2-379012.ew.r.appspot.com/">TODOlist-API2<a></h1>
</div>
<div align="center">
<img src="src/main/resources/static/images/logo.gif" alt="TODOlist-API2" width="200" height="200" align="center">
</div>

# Index
* [Introduction](#introduction)
* [Resources](#resources)
* [Operations](https://github.com/alesanfe/TODOlist-API2/wiki)
* [AutoDoc](#autodoc)

# Introduction
To check out URIs and request bodies, refer to our [SwaggerHub Page](https://todolist-api2-379012.ew.r.appspot.com/swagger-ui/).
Using the interactive documentation you will be able to check out URIs, response and request bodies...etc.

# Resources
Here is an overview of the resources Harmony offers in the form
of their response bodies and a short description of the relationship between them.
A basic schema of the organization is the next:
<div align="center">
<img src="src/main/resources/static/images/package.png" alt="package" align="center">
</div>

## Task
The Task resource is used to create, retrieve, update, and delete tasks. If we're using this API to organise our projects on GitHub, it represents the repositories that our user have.

```json
{
  "annotation": "Some details that must be taken into account.",
  "description": "Explain the purpose of this task.",
  "difficulty": "It could be SLEEP, EASY, MEDIUM, HARD, HARDCORE and I_WANT_TO_DIE.",
  "duration": "Calculated using the start date and the end date.",
  "finishedDate": "The date that the task must be finished.",
  "idTask": "The id of the task.",
  "priority": "It must be a number between 0 and 5 (0 not important, 5 very important).",
  "startDate": "The date that the task was started.",
  "status": "It could be DRAFT, IN_PROGRESS, IN_REVISION, DONE and CANCELLED.",
  "title": "The title of the task."
}
```

## User
The User resource is used to create, retrieve, update, and delete users. I recommend that they're real users from GitHub.

```json
{
  "avatar": "The image that represents the user.",
  "bio": "Something about the user.",
  "email": "The email to contact the user.",
  "idUser": "The id of the user",
  "location": "Where the user lives.",
  "name": "The name of the user.",
  "surname": "The surname of the user.",
  "username": "The username of the user.",
  "password": "The password of the user (keep it in a safe place).",
  "token": "The token of the user will be used to get the repos or the info at GitHub.",
  "tasks": [ "The task that the user must completed." ]
}
```

## Group
The Group resource is used to create, retrieve, update, and delete groups. They can contain a group of users and assign to all of them a task or delete it.

```json
{
  "createdDate": "The date that the group was created.",
  "description": "Something about the group.",
  "idGroup": "The id of the group",
  "name": "The name of the group.",
  "numTasks": "The number of different tasks that the group have.",
  "users": [ "The users that belong to the group." ]
}
```

# AutoDoc
This is a service that enables us to automatically generate planning and analysis documents in markdown format.

## Prepare data
To access the automatic document generation service for the Design and Testing subject, the following actions must be taken.

- To register the repository, update the JSON file located at *src/main/resources/db/data/task.json* with the GitHub repository name in the 'title' field and the GitHub workspace ID in the 'workSpaceId' field (which can be obtained from the workspace settings in Clockify and is found in the URL).

```json
[
  {
    "annotation": "Some details that must be taken into account.",
    "description": "Explain the purpose of this task.",
    "difficulty": "It could be SLEEP, EASY, MEDIUM, HARD, HARDCORE and I_WANT_TO_DIE.",
    "finishedDate": "The date that the task must be finished.",
    "priority": "It must be a number between 0 and 5 (0 not important, 5 very important).",
    "startDate": "The date that the task was started.",
    "status": "It could be DRAFT, IN_PROGRESS, IN_REVISION, DONE and CANCELLED.",
    "title": "The title of the task.",
    "workSpaceId": "Workspace id in clockify."
  }
]
```

- To register each member of the repository, add their information to the JSON file located at *src/main/resources/db/data/task.json*. Include the student's first and last name in the 'name' and 'surname' fields respectively, their GitHub username in the 'username' field, and their Clockify ID in the 'clockifyId' field (which can be obtained from the following URL: *https://api.clockify.me/api/v1/workspaces/{workSpaceId}/users*).

```json
[
  {
    "avatar": "The image that represents the user.",
    "bio": "Something about the user.",
    "email": "The email to contact the user.",
    "location": "Where the user lives.",
    "name": "The name of the user.",
    "surname": "The surname of the user.",
    "username": "The username of the user.",
    "password": "The password of the user (keep it in a safe place).",
    "clockifyId": "The user's clockify id."
  }
]
```

- To establish the connection between the repository and the users, create an entry in the JSON file located at *src/main/resources/db/data/user_task.json*.

```json
[
  {
    "idUser": "The user's id.",
    "idTask": "The task's id."
  }
]
```

## Launch program
The program can be started in two different ways, using the *--mode* option in the JAR file:

- **Local**: By default, it will use the configuration provided in the Design and Testing course, which requires the creation of a table called todolist-api2. However, this configuration can be modified in the application.properties file or by using the *--db*, *--user*, and *--password* options in the JAR file.

- **Cloud**: Although it is a free database, starting the program in the cloud is significantly slower than in local mode, but it does not require the database to be started beforehand.

## Important considerations
To generate the planning or analysis document correctly (if you want the individual you must use the option individual in the url and add your name and surnames), the following things must be taken into account:

For the **planning** (https://localhost:8080/api/v1/autodoc/planning/{repoName}/{username}/md) part:
- To get paid for one of the roles you're performing in a task, you must use one of the tags (**OPERATOR**, **TESTER**, **DEVELOPER**, **MANAGER**, **ANALYST**). If no tag is used, the program will most likely fail in the parsing attempt.
- The task name in Clockify must CONTAIN (it doesn't have to be exactly the same) the name of the ISSUE in GitHub.

For the **analysis** (https://localhost:8080/api/v1/autodoc/analysis/{repoName}/{username}/md) part:
- ISSUES must follow the following structure:
```
ID: Title (this should be the format of the ISSUE title)

Description (what was stated in the requirements document)

Conclusions (what you have understood)

Decisions made (the decision that was made, for example using String instead of the DataType Money)
```








