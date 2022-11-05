package ru.demonorium.utils.module;

import ru.demonorium.utils.module.exception.DModuleResolveException;

import java.util.List;

public interface DModuleManager<T, M, L extends DModuleLoader<T, M>> {
    /**
     * Test if dependency registered and loaded
     *
     * @param key - dependency id
     * @return true if dependency laoded
     */
    boolean isLoaded(T key);

    /**
     * Test if dependency registered
     *
     * @param key - dependency id
     * @return true if dependency registered
     */
    boolean isRegistered(T key);

    /**
     * Load all
     *
     * @throws DModuleResolveException - if loading process failed
     */
    void load() throws DModuleResolveException;

    /**
     * All module loaders ordered by loading order
     *
     * @throws DModuleResolveException - if loading process failed
     * @return all module loaders ordered by loading order
     */
    List<L> getLoadingSequence() throws DModuleResolveException;

    /**
     * All modules ordered by loading order
     *
     * @throws DModuleResolveException - if loading process failed
     * @return all modules ordered by loading order
     */
    List<M> getModulesSequence() throws DModuleResolveException;

    /**
     * return loaded data by module id
     *
     * @param key - module id
     * @return - return loaded data by module id
     */
    M get(T key);

    /**
     * return loader by module id
     *
     * @param key - module id
     * @return - return loader
     */
    L getLoader(T key);

    /**
     * Register new module loader
     *
     * @param loader   - module loader
     * @param critical - is module critical
     */
    void register(L loader, boolean critical);

    /**
     * Register new optional module loader
     *
     * @param loader - module loader
     */
    default void register(L loader) {
        register(loader, false);
    }
}
