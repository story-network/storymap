/*
 * Created on Sun Sep 27 2020
 *
 * Copyright (c) storycraft. Licensed under the GNU General Public License v3.
 */

package sh.pancake.storymap.dependency;

import java.io.File;

import org.gradle.api.Project;

import sh.pancake.storymap.resource.MappedMinecraftServerProvider;
import sh.pancake.storymap.resource.MinecraftServerMappingProvider;
import sh.pancake.storymap.resource.RawMinecraftServerProvider;
import sh.pancake.storymap.resource.VersionInfoProvider;

/*
 * Provide mapped mc server as dependency
 */
public class MCServerDepProvider implements IDependencyProvider {

    private File cacheDirectory;
    private String targetVersion;

    public MCServerDepProvider(String targetVersion, File cacheDirectory) {
        this.targetVersion = targetVersion;
        this.cacheDirectory = cacheDirectory;
    }

    @Override
    public void provide(Project project, boolean recache) throws Exception {
        VersionInfoProvider versionProvider = new VersionInfoProvider();

        MappedMinecraftServerProvider provider = new MappedMinecraftServerProvider(
            new RawMinecraftServerProvider(versionProvider),
            new MinecraftServerMappingProvider(versionProvider)
        );
    
        project.getDependencies().add("compileOnly",
                project.files(provider.provide(cacheDirectory, targetVersion, recache)));
    }

}
