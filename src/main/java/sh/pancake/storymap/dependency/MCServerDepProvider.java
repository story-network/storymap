/*
 * Created on Sun Sep 27 2020
 *
 * Copyright (c) storycraft. Licensed under the GNU General Public License v3.
 */

package sh.pancake.storymap.dependency;

import java.io.File;

import org.gradle.api.Project;

import sh.pancake.storymap.Constants;
import sh.pancake.storymap.ResourceProvider;

/*
 * Provide mapped mc server as dependency
 */
public class MCServerDepProvider implements IDependencyProvider {

    private String targetVersion;

    public MCServerDepProvider(String targetVersion) {
        this.targetVersion = targetVersion;
    }

    public String getTargetVersion() {
        return targetVersion;
    }

    @Override
    public void provide(ResourceProvider resProvider, Project project) throws Exception {
        project.getDependencies().add("compileOnly", project.files(resProvider.<File>provide(Constants.MINECRAFT_SERVER_MAPPED, targetVersion)));
    }
    
}
