package ru.demonorium.utils.module;

import ru.demonorium.concepts.data.has.HasDependencies;
import ru.demonorium.concepts.data.has.HasId;
import ru.demonorium.utils.module.exception.DModuleLoadingException;

import java.util.Set;

/**
 * Loader for module.
 *
 * @param <T> - id type
 * @param <M> - result type
 */
public interface DModuleLoader<T, M> extends HasId<T>, HasDependencies<T> {
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

    default void error(String reason) throws DModuleLoadingException {
        throw new DModuleLoadingException(reason, getId());
    }
}
