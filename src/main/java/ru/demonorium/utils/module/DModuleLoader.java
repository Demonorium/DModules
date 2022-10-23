package ru.demonorium.utils.module;

import ru.demonorium.utils.module.exception.DModuleLoadingException;

import java.util.Set;

public interface DModuleLoader<T, M> {
    T getId();

    M load(DModuleManager<T, M, ? extends DModuleLoader<T, M>> manager) throws DModuleLoadingException;
    void postLoad(DModuleManager<T, M, ? extends DModuleLoader<T, M>> manager, M data);

    default Set<T> getDependencies() {
        return Set.of();
    }
}
