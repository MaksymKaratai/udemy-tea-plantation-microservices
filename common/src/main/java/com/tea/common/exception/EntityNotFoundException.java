package com.tea.common.exception;

public class EntityNotFoundException extends RuntimeException {
    public <Id> EntityNotFoundException(Id id) {
        this(id, "id");
    }

    public <Id> EntityNotFoundException(Id id, String idField) {
        super("Cant find entity by " + idField + "[" + id + "]!");
    }
}
