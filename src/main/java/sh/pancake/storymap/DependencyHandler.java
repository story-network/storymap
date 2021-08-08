/*
 * Created on Sun Aug 08 2021
 *
 * Copyright (c) storycraft. Licensed under the GNU General Public License v3.
 */

package sh.pancake.storymap;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;

import sh.pancake.storymap.dependency.IDependencyProvider;

public class DependencyHandler {

    private File cacheDirectory;

    private Project project;

    private Map<String, BiFunction<String, File, IDependencyProvider>> map;

    private boolean recache;

    public DependencyHandler(Project project, File cacheDirectory, boolean recache) {
        this.project = project;

        this.cacheDirectory = cacheDirectory;

        this.map = new HashMap<>();

        this.recache = recache;
    }

    public void register(String name, BiFunction<String, File, IDependencyProvider> generator) {
        map.put(name, generator);
    }

    public void handle(Dependency dependency) throws Exception {
        BiFunction<String, File, IDependencyProvider> providerConstructor = map.get(dependency.getName());
        if (providerConstructor == null) throw new Exception("Cannot find handler for type " + dependency.getName());

        IDependencyProvider provider = providerConstructor.apply(dependency.getVersion(), cacheDirectory);

        provider.provide(project, recache);
    }
    
}
