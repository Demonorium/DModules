package ru.demonorium.utils.module;

import ru.demonorium.utils.module.exception.DModuleResolveException;

public interface DModuleManager<T, M, L extends DModuleLoader<T, M>> {
    /**
     * Test if dependency registered and loaded
     *
     * @param key - dependency id
     * @return true if dependency laoded
     */
    boolean isLoaded(T key);

    /**
     * Load all
     *
     * @throws DModuleResolveException - if loading process failed
     */
    void load() throws DModuleResolveException;

    /**
     * return loaded data by module id
     *
     * @param key - module id
     * @return - return loaded data by module id
     */
    M get(T key);

    /**
     * Register new module
     *
     * @param loader   - module loader
     * @param critical - isModule critical
     */
    void register(L loader, boolean critical);

    /**
     * Register new module
     *
     * @param loader - module loader
     */
    default void register(L loader) {
        register(loader, false);
    }
}
