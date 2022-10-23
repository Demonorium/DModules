package ru.demonorium.utils.module.exception;

public class DModuleLoadingException extends DModuleException {
    public <T> DModuleLoadingException(T id) {
        super("Critical module loading failed", "loading", id);
    }

    public <T> DModuleLoadingException(Throwable cause, T id) {
        super("Critical module loading failed", cause, "loading",  id);
    }


    public <T> DModuleLoadingException(String message, Throwable cause, T id) {
        super(message, cause, "loading", id);
    }

    public <T> DModuleLoadingException(String message, T id) {
        super(message, "loading", id);
    }

}
