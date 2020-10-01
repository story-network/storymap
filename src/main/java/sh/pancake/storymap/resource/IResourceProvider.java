/*
 * Created on Mon Sep 28 2020
 *
 * Copyright (c) storycraft. Licensed under the GNU General Public License v3.
 */

package sh.pancake.storymap.resource;

import sh.pancake.storymap.ResourceProvider;

public interface IResourceProvider<T> {

    public T provide(ResourceProvider provider, String argument, boolean recache) throws Exception;

}
