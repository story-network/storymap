/*
 * Created on Mon Sep 28 2020
 *
 * Copyright (c) storycraft. Licensed under the GNU General Public License v3.
 */

package sh.pancake.storymap.resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import sh.pancake.common.util.MCLauncherUtil;

public class VersionInfoProvider implements IResourceProvider {

    @Override
    public File provide(File directory, String targetVersion, boolean recache) throws IOException {
        File versionInfoFile = new File(directory, targetVersion + "-info.json");

        if (!recache && versionInfoFile.exists()) {
            return versionInfoFile;
        }

        String rawInfo = MCLauncherUtil.fetchRawVersionInfo(targetVersion);
        if (rawInfo == null) throw new IOException("Cannot get version info for " + targetVersion);

        Files.writeString(versionInfoFile.toPath(), rawInfo, StandardOpenOption.CREATE);

        return versionInfoFile;
    }
    
}
