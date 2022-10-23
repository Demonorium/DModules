package ru.demonorium.utils.module;

import ru.demonorium.utils.module.exception.DModuleLoadingException;

import java.util.Set;

/**
 * Loader for module.
 *
 * @param <T> - id type
 * @param <M> - result type
 */
public interface DModuleLoader<T, M> {
    /**
     * Unique id of module.
     *
     * @return id
     */
    T getId();

    /**
     * Load/Create data and then return it.
     *
     * @param manager - current manager
     * @return loaded data
     * @throws DModuleLoadingException - exception, then loading failed
     */
    M load(DModuleManager<T, M, ? extends DModuleLoader<T, M>> manager) throws DModuleLoadingException;

    /**
     * Modify data to finish process. All dependencies already loaded on this step
     *
     * @param manager - current manager
     * @param data - loaded data
     */
    void postLoad(DModuleManager<T, M, ? extends DModuleLoader<T, M>> manager, M data);

    default Set<T> getDependencies() {
        return Set.of();
    }

    default void error(String reason) throws DModuleLoadingException {
        throw new DModuleLoadingException(reason, getId());
    }
}
