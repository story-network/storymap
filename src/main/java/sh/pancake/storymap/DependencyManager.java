/*
 * Created on Mon Sep 28 2020
 *
 * Copyright (c) storycraft. Licensed under the GNU General Public License v3.
 */

package sh.pancake.storymap;

import org.gradle.api.Project;

import sh.pancake.storymap.dependency.IDependencyProvider;

public class DependencyManager {
    
    private ResourceProvider resProvider;
    private Project project;

    public DependencyManager(ResourceProvider resProvider, Project project) {
        this.resProvider = resProvider;
        this.project = project;
    }

    public Project getProject() {
        return project;
    }

    public void provide(IDependencyProvider provider) throws Exception {
        provider.provide(resProvider, project);
    }

}
