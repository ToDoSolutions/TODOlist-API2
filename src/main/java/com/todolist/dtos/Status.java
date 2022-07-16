package com.todolist.dtos;

public enum Status {
    DRAFT,
    IN_PROGRESS,
    IN_REVISION,
    DONE,
    CANCELLED;

    public static Status parse(String status) {
        String statusLowerCase = status.toLowerCase();
        if (statusLowerCase.equals("draft")) {
            return DRAFT;
        } else if (statusLowerCase.equals("in_progress") || statusLowerCase.equals("in progress")) {
            return IN_PROGRESS;
        } else if (statusLowerCase.equals("in_revision") || statusLowerCase.equals("in revision")) {
            return IN_REVISION;
        } else if (statusLowerCase.equals("done")) {
            return DONE;
        } else if (statusLowerCase.equals("cancelled")) {
            return CANCELLED;
        } else {

            throw new IllegalArgumentException("The status " + status + " is not valid and it should be one of the following -> draft - in_progress - in_revision - done - cancelled.");
        }
    }
}
