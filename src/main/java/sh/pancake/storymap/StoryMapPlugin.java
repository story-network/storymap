/*
 * Created on Sat Sep 26 2020
 *
 * Copyright (c) storycraft. Licensed under the GNU General Public License v3.
 */

package sh.pancake.storymap;

import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.tasks.TaskContainer;

import java.io.File;

import org.gradle.api.Plugin;

import sh.pancake.storymap.dependency.IDependencyProvider;
import sh.pancake.storymap.dependency.MCServerDepProvider;
import sh.pancake.storymap.resource.*;

public class StoryMapPlugin implements Plugin<Project> {

    private ResourceProvider resProvider;
    private DependencyManager depManager;

    @Override
    public void apply(Project project) {
        // Init start
        File userCache = new File(project.getGradle().getGradleUserHomeDir(), "caches" + File.separator + "storymap");
		if (!userCache.exists()) {
			userCache.mkdirs();
        }
        
        this.resProvider = new ResourceProvider(userCache);
        this.depManager = new DependencyManager(resProvider, project);

        // Init end

        resProvider.addProvider(Constants.MINECRAFT_SERVER_RAW, new RawMinecraftServerProvider());
        resProvider.addProvider(Constants.MINECRAFT_SERVER_MAPPED, new MappedMinecraftServerProvider());
        resProvider.addProvider(Constants.MINECRAFT_SERVER_MAPPING, new MinecraftServerMappingProvider());
        resProvider.addProvider(Constants.VERSION_INFO, new VersionInfoProvider());

        applyConfiguration(project.getConfigurations());
        applyExtension(project.getExtensions());
        applyTask(project.getTasks());

        project.afterEvaluate(this::applyAfter);
    }

    public void applyTask(TaskContainer container) {
        
    }

    public void applyConfiguration(ConfigurationContainer container) {
        
    }

    public void applyExtension(ExtensionContainer container) {
        container.create(Constants.MINECRAFT, MinecraftExt.class);
    }

    public void applyAfter(Project project) {
        MinecraftExt ext = (MinecraftExt) project.getExtensions().getByName(Constants.MINECRAFT);

        IDependencyProvider mcServerProvider = new MCServerDepProvider(ext.version);
        // Put unmapped minecraft server as dependency

        try {
            depManager.provide(mcServerProvider);
        } catch (Exception e) {
            System.err.println("ERR: Cannot provide minecraft server");
            e.printStackTrace();
        }
        
    }

}
