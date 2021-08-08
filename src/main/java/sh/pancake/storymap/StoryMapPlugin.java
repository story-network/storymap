/*
 * Created on Sat Sep 26 2020
 *
 * Copyright (c) storycraft. Licensed under the GNU General Public License v3.
 */

package sh.pancake.storymap;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;

import java.io.File;

import org.gradle.api.Plugin;

import sh.pancake.storymap.dependency.MCServerDepProvider;

public class StoryMapPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        applyConfiguration(project.getConfigurations());

        project.afterEvaluate(this::applyAfter);
    }

    public void applyConfiguration(ConfigurationContainer container) {
        container.create(Constants.STORYMAP);
    }

    public void applyAfter(Project project) {
        Configuration configuration = project.getConfigurations().getByName(Constants.STORYMAP);

        File cacheDirectory = new File(project.getGradle().getGradleUserHomeDir(), "caches" + File.separator + "storymap");
		if (!cacheDirectory.exists()) {
			cacheDirectory.mkdirs();
        }
        
        DependencyHandler handler = new DependencyHandler(project, cacheDirectory, false);

        handler.register("minecraft-server", MCServerDepProvider::new);

        configuration.getDependencies().parallelStream().forEach((Dependency dependency) -> {
            try {
                handler.handle(dependency);
            } catch (Exception e) {
                throw new RuntimeException("Error while providing dependencies." + e.getLocalizedMessage(), e);
            }
        });
    }

}
