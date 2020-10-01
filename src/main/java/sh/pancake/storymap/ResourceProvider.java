/*
 * Created on Mon Sep 28 2020
 *
 * Copyright (c) storycraft. Licensed under the GNU General Public License v3.
 */

package sh.pancake.storymap;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import sh.pancake.storymap.resource.IResourceProvider;

public class ResourceProvider {

    private File storageDirectory;
    private Map<String, IResourceProvider<?>> providerMap;
    
    public ResourceProvider(File storageDirectory) {
        this.storageDirectory = storageDirectory;
        this.providerMap = new HashMap<>();
    }

    public File getStorageDirectory() {
        return storageDirectory;
    }
    
    public void addProvider(String name, IResourceProvider<?> provider) {
        providerMap.put(name, provider);
    }

    public IResourceProvider<?> getProvider(String name) {
        return providerMap.get(name);
    }

    public boolean hasProvider(String name) {
        return providerMap.containsKey(name);
    }

    public IResourceProvider<?> removeProvider(String name) {
        return providerMap.remove(name);
    }

    public <T>T provide(String name, String argument, boolean recache) throws Exception {
        if (!hasProvider(name)) return null;

        IResourceProvider<T> provider = (IResourceProvider<T>) getProvider(name);

        return provider.provide(this, argument, recache);
    }

    public <T>T provide(String name, String argument) throws Exception {
        return provide(name, argument, false);
    }

}
