package ru.demonorium.utils.module;

import ru.demonorium.utils.module.exception.DModuleResolveException;

public interface DModuleManager<T, M, L extends DModuleLoader<T, M>> {
    boolean isLoaded(T key);
    void load() throws DModuleResolveException;

    M get(T key);
    void register(L loader, boolean critical);

    default void register(L loader) {
        register(loader, false);
    }
}
