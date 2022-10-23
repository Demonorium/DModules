package ru.demonorium.utils.module;

import ru.demonorium.utils.module.exception.DModuleException;
import ru.demonorium.utils.module.exception.DModuleLoadingException;
import ru.demonorium.utils.module.exception.DModuleResolveException;

import java.io.Serializable;
import java.util.*;

public class DModuleManagerImpl<T, M, L extends DModuleLoader<T, M>> implements Serializable, DModuleManager<T, M, L> {
    private final Map<T, ModuleContainer<T, M, L>> modules = new HashMap<>();
    private DModuleEventHandle<T> eventHandle;

    public DModuleManagerImpl(DModuleEventHandle<T> eventHandle) {
        this.eventHandle = eventHandle;
    }

    public DModuleManagerImpl() {
        eventHandle = DModuleEventHandle.STUB_HANDLE;
    }

    @Override
    public void register(L loader, boolean critical) {
        this.modules.put(loader.getId(), new ModuleContainer<>(loader, critical));
        eventHandle.moduleRegistered(loader.getId());
    }

    @Override
    public M get(T id) {
        ModuleContainer<T, M, L> result = modules.get(id);
        if (result == null) {
            return null;
        }

        return result.getResource();
    }

    @Override
    public boolean isLoaded(T id) {
        ModuleContainer<T, M, L> result = modules.get(id);
        if (result == null) {
            return false;
        }

        return result.isLoaded();
    }

    @Override
    public void load() throws DModuleResolveException {
        for (ModuleContainer<T, M, L> container: modules.values()) {
            Set<T> dependencies = container.getLoader().getDependencies();
            for (T dependency: dependencies) {
                ModuleContainer<T, M, L> extracted = modules.get(dependency);
                if (extracted == null) {
                    if (container.isCritical()) {
                        unload();
                        throw new DModuleResolveException("Dependency '" + dependency + "' is not loaded for critical module.", container.getLoader().getId());
                    } else {
                        eventHandle.optionalModuleDependencyNotRegistered(container.getLoader().getId(), dependency);
                    }
                } else {
                    extracted.getDependents().add(container);
                }
            }
        }

        for (ModuleContainer<T, M, L> container: modules.values()) {
            if (!container.isLoaded() && container.getLoader().getDependencies().isEmpty()) {
                try {
                    loadModule(container);
                } catch (DModuleResolveException exception) {
                    throw exception;
                } catch (DModuleException exception) {
                    eventHandle.loadingException(exception);
                }
            }
        }

        for (ModuleContainer<T, M, L> container: modules.values()) {
            if (container.isLoaded()) {
                container.postLoad(this);
            } else if (container.isCritical()) {
                unload();
                throw new DModuleResolveException("Circular reference.", container.getLoader().getId());
            }
        }
    }

    private void loadModule(ModuleContainer<T, M, L> container) throws DModuleException {
        try {
            container.load(this);
            eventHandle.moduleLoaded(container.getLoader().getId());
        } catch (Exception exception) {
            if (container.isCritical()) {
                throw new DModuleLoadingException(exception, container.getLoader().getId());
            } else {
                for (ModuleContainer<T, M, L> dependent: container.getDependents()) {
                    Set<T> dependencies = dependent.getLoader().getDependencies();
                    for (T dependency: dependencies) {
                        if (dependency.equals(container.getLoader().getId())) {
                            if (dependent.isCritical()) {
                                unload();
                                throw new DModuleResolveException("Dependency '" + container.getLoader().getId() + "' is not loaded for critical module.'", exception, dependent.getLoader().getId());
                            } else {
                                throw new DModuleLoadingException("Dependency '" + container.getLoader().getId() + "' is not loaded for optional module.'", exception, dependent.getLoader().getId());
                            }
                        }
                    }
                }
            }

            throw new DModuleLoadingException(exception, container.getLoader().getId());
        }

        for (ModuleContainer<T, M, L> dependent: container.getDependents()) {
            if (dependent.getDependencies() == 1) {
                try {
                    loadModule(dependent);
                } catch (DModuleLoadingException exception) {
                    eventHandle.loadingException(exception);
                }
            } else {
                dependent.setDependencies(dependent.getDependencies() - 1);
            }
        }
    }

    public void unload() {
        for (ModuleContainer<T, M, L> container: modules.values()) {
            container.unload();
            eventHandle.moduleCleaned(container.getLoader().getId());
        }
    }

    public DModuleEventHandle<T> getEventHandle() {
        return eventHandle;
    }

    public void setEventHandle(DModuleEventHandle<T> eventHandle) {
        this.eventHandle = eventHandle;
    }

    public Map<T, ModuleContainer<T, M, L>> getModules() {
        return modules;
    }

    private static class ModuleContainer<T, M, L extends DModuleLoader<T, M>> implements Serializable {
        private final L loader;
        private final boolean critical;
        private boolean loaded;
        private M resource;

        private int dependencies;
        private final List<ModuleContainer<T, M, L>> dependents = new ArrayList<>();

        public ModuleContainer(L loader, boolean critical) {
            this.loader = loader;
            this.critical = critical;
            this.loaded = false;
            this.resource = null;
            dependencies = loader.getDependencies().size();
        }

        public void load(DModuleManager<T, M, L> manager) throws DModuleLoadingException {
            resource = loader.load(manager);
            loaded = true;
        }

        public void unload() {
            resource = null;
            loaded = false;
            dependencies = loader.getDependencies().size();
            dependents.clear();
        }

        public void postLoad(DModuleManager<T, M, L> manager) {
            if (loaded) {
                loader.postLoad(manager, resource);
            }
        }

        public List<ModuleContainer<T, M, L>> getDependents() {
            return dependents;
        }

        public int getDependencies() {
            return dependencies;
        }

        public void setDependencies(int dependencies) {
            this.dependencies = dependencies;
        }

        public void setLoaded(boolean loaded) {
            this.loaded = loaded;
        }

        public void setResource(M resource) {
            this.resource = resource;
        }

        public L getLoader() {
            return loader;
        }

        public boolean isCritical() {
            return critical;
        }

        public boolean isLoaded() {
            return loaded;
        }

        public M getResource() {
            return resource;
        }
    }
}
