package ru.demonorium.utils.module;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.demonorium.utils.module.exception.DModuleLoadingException;
import ru.demonorium.utils.module.exception.DModuleResolveException;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class DModuleManagerTest {
    private static class ModuleLoader implements DModuleLoader<String, Long> {
        private final String id;
        private final Set<String> dependencies;
        private long count = 0;

        public ModuleLoader(String id, Set<String> dependencies) {
            this.id = id;
            this.dependencies = dependencies;
        }

        public ModuleLoader(String id) {
            this(id, Set.of());
        }

        @Override
        public Set<String> getDependencies() {
            return dependencies;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public Long load(DModuleManager<String, Long, ? extends DModuleLoader<String, Long>> manager) throws DModuleLoadingException {
            return count++;
        }

        @Override
        public void postLoad(DModuleManager<String, Long, ? extends DModuleLoader<String, Long>> manager, Long data) {

        }
    }

    @Test
    void soloTest() {
        DModuleManager<String, Long, ModuleLoader> manager = new DModuleManagerImpl<>();
        manager.register(new ModuleLoader("test"), true);
        try {
            manager.load();
        } catch (DModuleResolveException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertTrue(manager.isLoaded("test"));
        Assertions.assertEquals(0L, (long) manager.get("test"));
    }

    @Test
    void dependencyTest() {
        DModuleManager<String, Long, ModuleLoader> manager = new DModuleManagerImpl<>();
        manager.register(new ModuleLoader("test", Set.of("dependency")), true);
        manager.register(new ModuleLoader("dependency", Set.of("dependency2")), true);
        manager.register(new ModuleLoader("dependency2"), true);

        try {
            manager.load();
        } catch (DModuleResolveException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertTrue(manager.isLoaded("test"));
        Assertions.assertEquals(0L, (long) manager.get("test"));

        Assertions.assertTrue(manager.isLoaded("dependency"));
        Assertions.assertEquals(0L, (long) manager.get("dependency"));

        Assertions.assertTrue(manager.isLoaded("dependency2"));
        Assertions.assertEquals(0L, (long) manager.get("dependency2"));
    }

    @Test
    void dependencyFailedTest() {
        AtomicBoolean handled = new AtomicBoolean(false);
        DModuleEventHandle<String> handle = new DModuleEventHandle<>() {
            @Override
            public void optionalModuleDependencyNotRegistered(String id, String dependency) {
                Assertions.assertEquals(id, "root");
                Assertions.assertEquals(dependency, "dependency");
                handled.set(true);
            }

            @Override
            public void loadingException(Exception exception) {

            }

            @Override
            public void moduleLoaded(String id) {

            }

            @Override
            public void moduleCleaned(String id) {

            }

            @Override
            public void moduleRegistered(String id) {

            }
        };

        DModuleManager<String, Long, ModuleLoader> manager = new DModuleManagerImpl<>(handle);
        manager.register(new ModuleLoader("root", Set.of("dependency", "dependency2")));
        manager.register(new ModuleLoader("dependency2"), true);

        try {
            manager.load();
        } catch (DModuleResolveException exception) {
            throw new RuntimeException(exception);
        }

        Assertions.assertTrue(handled.get());
    }
}
