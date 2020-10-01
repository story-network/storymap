/*
 * Created on Mon Sep 28 2020
 *
 * Copyright (c) storycraft. Licensed under the GNU General Public License v3.
 */

package sh.pancake.storymap.resource;

import java.io.File;
import java.io.IOException;

import sh.pancake.storymap.ResourceProvider;
import sh.pancake.common.object.VersionInfo;
import sh.pancake.common.util.FileUtil;
import sh.pancake.common.util.MCLauncherUtil;

public class VersionInfoProvider implements IResourceProvider<VersionInfo> {

    @Override
    public VersionInfo provide(ResourceProvider provider, String targetVersion, boolean recache) throws Exception {
        File versionInfoFile = new File(provider.getStorageDirectory(), targetVersion + "-info.json");

        if (!recache && versionInfoFile.exists()) {
            try {
                VersionInfo info = MCLauncherUtil.getInfoFromJson(FileUtil.readString(versionInfoFile));

                return info;
            } catch(Exception e) {

            }
        }

        String rawInfo = MCLauncherUtil.fetchRawVersionInfo(targetVersion);
        if (rawInfo == null) throw new IOException("Cannot get version info for " + targetVersion);

        FileUtil.writeString(versionInfoFile, rawInfo);

        return MCLauncherUtil.getInfoFromJson(rawInfo);
    }
    
}
