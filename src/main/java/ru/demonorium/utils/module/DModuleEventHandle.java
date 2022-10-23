package ru.demonorium.utils.module;

import java.io.Serializable;

/**
 * Interface for logging events
 *
 * @param <T> - id type
 */
public interface DModuleEventHandle<T> extends Serializable {
    void optionalModuleDependencyNotRegistered(T id, T dependency);
    void loadingException(Exception exception);
    void moduleLoaded(T id);
    void moduleCleaned(T id);
    void moduleRegistered(T id);

    public static final DModuleEventHandle STUB_HANDLE = new DModuleEventHandle() {
        @Override
        public void optionalModuleDependencyNotRegistered(Object id, Object dependency) {

        }

        @Override
        public void loadingException(Exception exception) {

        }

        @Override
        public void moduleLoaded(Object id) {

        }

        @Override
        public void moduleCleaned(Object id) {

        }

        @Override
        public void moduleRegistered(Object id) {

        }
    };
}
