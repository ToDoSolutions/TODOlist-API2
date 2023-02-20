package com.todolist.dtos;

import com.todolist.exceptions.BadRequestException;

public enum Status {
    DRAFT,
    IN_PROGRESS,
    IN_REVISION,
    DONE,
    CANCELLED,
    UNKNOWN;

    public static Status parse(String status) {
        String statusLowerCase = status.toLowerCase();
        return switch (statusLowerCase) {
            case "draft" -> DRAFT;
            case "in_progress", "in progress" -> IN_PROGRESS;
            case "in_revision", "in revision" -> IN_REVISION;
            case "done" -> DONE;
            case "cancelled" -> CANCELLED;
            case "unknown" -> UNKNOWN;
            default ->
                    throw new BadRequestException("The status " + status + " is not valid and it should be one of the following -> draft - in_progress - in_revision - done - cancelled - unknown.");
        };
    }
}
