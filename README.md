[![GitHub release](https://img.shields.io/github/release/alesanfe/TODOlist-API2)](https://github.com/alesanfe/TODOlist-API2/releases)
[![Java support](https://img.shields.io/badge/Java-17+-green?logo=java&logoColor=white)](https://openjdk.java.net/)
[![License](https://img.shields.io/github/license/alesanfe/TODOlist-API2?color=4D7A97&logo=apache)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Gitpod Ready-to-Code](https://img.shields.io/badge/Gitpod-ready--to--code-green?label=gitpod&logo=gitpod&logoColor=white)](https://gitpod.io/#https://github.com/alesanfe/TODOlist-API2)
[![GitHub Stars](https://img.shields.io/github/stars/alesanfe/TODOlist-API2)](https://github.com/alesanfe/TODOlist-API2/stargazers)
[![GitHub Forks](https://img.shields.io/github/forks/alesanfe/TODOlist-API2)](https://github.com/alesanfe/TODOlist-API2/fork)
[![user repos](https://badgen.net/github/dependents-repo/alesanfe/TODOlist-API2?label=user%20repos)](https://github.com/alesanfe/TODOlist-API2/network/dependents)
[![GitHub Contributors](https://img.shields.io/github/contributors/alesanfe/TODOlist-API2)](https://github.com/alesanfe/TODOlist-API2/graphs/contributors)
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
To check out URIs and request bodies, refer to our [SwaggerHub Page](https://localhost:8080/swagger-ui/).
Using the interactive documentation you will be able to check out URIs, response and request bodies...etc.

# Resources
Here is an overview of the resources Harmony offers in the form
of their response bodies and a short description of the relationship between them.
A basic schema of the organization is the next:
<div align="center">
<img src="src/main/resources/static/images/package.png" alt="package" align="center">
</div>

## Task
The Task resource is a representation of a specific work item that needs to be completed. It can be used to organize and manage a project by creating, retrieving, updating, and deleting tasks. In the context of GitHub, the Task resource represents the ISSUES that a repository has. In Clockify, it represents the time that must be spent on each ISSUE on GitHub.

The JSON snippet provided is an example of the attributes that can be associated with a Task resource. These attributes are:

```json
{
  "title": "A brief summary of the task."
  "position": "An optional attribute that can be used to specify the position of the task."
  "description": "A more detailed explanation of what the task involves and what needs to be done to complete it.",
  "conslusion": "An optional attribute that can be used to summarize the outcome or result of completing the task."
  "decision": "An optional attribute that can be used to document any decisions that were made during the course of completing the task."
  "annotation": "An optional attribute that can be used to include any additional details that are relevant to the task, such as references to related tasks or documents.",
  "status": "An optional attribute that can be used to track the progress of the task. It can have values like DRAFT, IN_PROGRESS, IN_REVISION, DONE, or CANCELLED.",
  "priority": "An optional attribute that can be used to indicate the relative importance of the task. It must be a number between 0 (not important) and 5 (very important).",
  "difficulty": "An optional attribute that can be used to indicate the level of difficulty of the task. It can have values like SLEEP, EASY, MEDIUM, HARD, HARDCORE, or I_WANT_TO_DIE.",
}
```
These attributes provide useful information for managing and tracking tasks in a project. They can be used to prioritize tasks, assign them to team members, and monitor progress.

## User
The User resource is an entity that represents a user in a system. It is used to create, retrieve, update, and delete user data. In this context, the recommendation is to connect real users from GitHub to the Clockify account.

The JSON snippet provided is an example of the attributes that can be associated with a User resource. These attributes are:

```json
{
  "name": "The first name of the user.",
  "surname": "The last name of the user.",
  "username": "The unique identifier of the user in the system.",
  "email": "The email address of the user, which can be used to contact them.",
  "avatar": "An image that represents the user, which can be used to personalize their profile.",
  "location": "The physical location of the user, such as their city or country.",
  "bio": "An optional attribute that can be used to include a brief description or biography of the user.",
  "password": "The password associated with the user account, which should be kept secure and confidential.",
  "token": "A token associated with the user account, which can be used to authenticate the user and access their repositories or other information in GitHub.",
}
```
These attributes provide useful information for managing user accounts and personalizing the user experience. It's important to note that the password and token attributes should be kept secure to prevent unauthorized access to user accounts.

## Group
The Group resource is an entity used to create, retrieve, update, and delete groups in a system. It is typically used to manage collections of users who share common attributes or interests. In the context of GitHub, a Group resource represents a repository, which is a collection of files, folders, and other resources that are managed together as a single project.

The JSON snippet provided is an example of the attributes that can be associated with a Group resource. These attributes are:

```json
{
  "name": "The name of the group or repository, which provides a brief summary of what the group represents.",
  "createdDate": "The date that the group was created, which provides a reference point for tracking the history of the group or repository.",
  "description": "An optional attribute that can be used to include additional information about the group or repository, such as its purpose or goals."
  "workSpaceId": "The Id of your group in Clockify."
}
```
These attributes provide useful information for managing groups or repositories in a project. They can be used to identify and organize collections of users, assign tasks or issues to all members, and provide context for the group's or repository's purpose and history.

## Role
The Role resource is used to represent the role or position that a user has in relation to a task or project. It is typically used to assign specific responsibilities and permissions to different users in order to manage and coordinate their contributions to the project.

The JSON snippet provided is an example of the attributes that can be associated with a Role resource. These attributes are:

```json
{
  "status": "The status or name of the role, which represents one of several possible roles that a user can have in relation to a task or project and it must be one of the following OPERATOR, TESTER, MANAGER, ANALYST, and UNDEFINED, among others."
  "duration": "The time that the user is expected to spend in that role for the task or project. This can be useful for tracking and managing the allocation of resources, as well as for planning and scheduling tasks and activities."
}
```
These attributes provide useful information for managing roles in a project. They can be used to assign specific responsibilities and permissions to different users, track the time and resources required for different roles, and provide a clear and consistent framework for managing and coordinating the contributions of different users to the project.

# AutoDoc
This is a service that enables us to automatically generate planning and analysis (not very tested) documents in markdown format.

## Prepare data
The given instructions describe the steps needed to access the automatic document generation service for the Design and Testing subject.

- To register the repository and use the service, the JSON file located at *src/main/resources/db/data/group.json* needs to be updated with the GitHub repository name and the GitHub workspace ID. The repository name should be entered in the 'name' field, and the workspace ID should be entered in the 'workSpaceId' field. The workspace ID can be obtained from the workspace settings in Clockify and is found in the URL.

```json
[
  {
    "name": "The name of the group or repository, which provides a brief summary of what the group represents.",
    "createdDate": "The date that the group was created, which provides a reference point for tracking the history of the group or repository.",
    "description": "An optional attribute that can be used to include additional information about the group or repository, such as its purpose or goals."
    "workSpaceId": "The Id of your group in Clockify."
  }
]
```

- To register each member of the repository, add their information to the JSON file located at *src/main/resources/db/data/user.json*. Include the student's first and last name in the 'name' and 'surname' fields respectively, their GitHub username in the 'username' field, and their Clockify ID in the 'clockifyId' field (which can be obtained from the following URL: *https://api.clockify.me/api/v1/workspaces/{workSpaceId}/users*).

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

- To establish the connection between the repository and the users, create an entry in the JSON file located at *src/main/resources/db/data/group_user.json* file to establish a relationship between the registered users and the registered group. In this JSON file, each entry represents a user that is a member of a group or repository.

The "idUser" attribute corresponds to the ID of the user, which can be obtained from the *src/main/resources/db/data/user.json* file, and the "idGroup" attribute corresponds to the ID of the group, which can be obtained from the *src/main/resources/db/data/group.json* file.

```json
[
  {
    "idUser": "The user's id.",
    "idGroup": "The group's id."
  }
]
```

## Launch program
The program can be started in two different ways, using the *--mode* option in the JAR file:

- **Local**: By default, it will use the configuration provided in the Design and Testing course, which requires the creation of a table called todolist-api2. However, this configuration can be modified in the application.properties file or by using the *--db*, *--user*, and *--password* options in the JAR file.

- **Cloud**: Although it is a free database, starting the program in the cloud is significantly slower than in local mode, but it does not require the database to be started beforehand.

## Important considerations
To generate the planning or analysis document correctly (if you want the individual you must use the option individual in the url and add your name and surnames), the following things must be taken into account:

For the **planning** (/api/v1/autodoc/planning/{repoName}/{username}/md or /api/v1/autodoc/planning/{repoName}/{username}/individual/{individual}/md) part:
- To get paid for one of the roles you're performing in a task, you must use one of the tags (**OPERATOR**, **TESTER**, **DEVELOPER**, **MANAGER**, **ANALYST**). If no tag is used, the program will take the role UNDEFINED.
- The task name in Clockify must CONTAIN (it doesn't have to be exactly the same) the name of the ISSUE in GitHub.

For the **analysis** (/api/v1/autodoc/analysis/{repoName}/{username}/md or /api/v1/autodoc/analysis/{repoName}/{username}/individual/{individual}/md) part:
- ISSUES must follow the following structure:
```
Task i(jI): Title (this should be the format of the ISSUE title)

Description (what was stated in the requirements document)

Conclusions (what you have understood)

Decisions made (the decision that was made, for example using String instead of the DataType Money)
```

```
Task i(G): Title (this should be the format of the ISSUE title)

Description (what was stated in the requirements document)

Conclusions (what you have understood)

Decisions made (the decision that was made, for example using String instead of the DataType Money)
```

```
Task i: jI - Title (this should be the format of the ISSUE title)

Description (what was stated in the requirements document)

Conclusions (what you have understood)

Decisions made (the decision that was made, for example using String instead of the DataType Money)
```

```
Task i: G - Title (this should be the format of the ISSUE title)

Description (what was stated in the requirements document)

Conclusions (what you have understood)

Decisions made (the decision that was made, for example using String instead of the DataType Money)
```








