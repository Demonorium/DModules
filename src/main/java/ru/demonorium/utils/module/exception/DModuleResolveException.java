package ru.demonorium.utils.module.exception;

/**
 * Critical exception in tree construction or in critical module loading
 */
public class DModuleResolveException extends DModuleException {
    public <T> DModuleResolveException(String message, T id) {
        super(message, "dependency tree iteration", id);
    }

    public <T> DModuleResolveException(String message, Throwable cause, T id) {
        super(message, cause, "dependency tree iteration", id);
    }
}
