package ru.demonorium.utils.module.exception;

public class DModuleException extends Exception {
    private final String id;

    public <T> DModuleException(String message, String stage, T id) {
        super(getText(String.valueOf(id), stage, message));
        this.id = String.valueOf(id);
    }

    public <T> DModuleException(String message, Throwable cause, String stage, T id) {
        super(getText(String.valueOf(id), stage, message), cause);
        this.id = String.valueOf(id);
    }

    public String getId() {
        return id;
    }

    protected static String getText(String id, String stage, String message) {
        return "Error in loading stage '" + stage + "' module with id '" + id + "': " + message;
    }
}
