/*
 * Created on Sun Sep 27 2020
 *
 * Copyright (c) storycraft. Licensed under the GNU General Public License v3.
 */

package sh.pancake.storymap.dependency;

import org.gradle.api.Project;

import sh.pancake.storymap.ResourceProvider;

public interface IDependencyProvider {
    
    void provide(ResourceProvider resProvider, Project project) throws Exception;
    
}
