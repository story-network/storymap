/*
 * Created on Mon Sep 28 2020
 *
 * Copyright (c) storycraft. Licensed under the GNU General Public License v3.
 */

package sh.pancake.storymap.resource;

import java.io.File;
import java.io.IOException;

import sh.pancake.storymap.Constants;
import sh.pancake.storymap.ResourceProvider;
import sh.pancake.common.object.VersionInfo;
import sh.pancake.common.util.DownloadUtil;
import sh.pancake.common.util.FileUtil;
import sh.pancake.common.util.Hash;
import sh.pancake.common.util.Hex;

public class RawMinecraftServerProvider implements IResourceProvider<File> {

    @Override
    public File provide(ResourceProvider provider, String targetVersion, boolean recache) throws Exception {
        File file = new File(provider.getStorageDirectory(), targetVersion + "-raw.jar");

        VersionInfo versionInfo = null;
        
        try {
            versionInfo = provider.provide(Constants.VERSION_INFO, targetVersion);
        } catch(Exception e) {

        }

        if (!recache && file.exists()) {
            if (versionInfo == null) return file;

            byte[] data = FileUtil.readData(file);
            String hex = Hex.byteArrayToHex(Hash.sha1From(data));

            if (data.length == versionInfo.downloads.serverMappings.size && hex.equalsIgnoreCase(versionInfo.downloads.server.sha1)) {
                return file;
            }
        }

        if (versionInfo == null) throw new IOException("Cannot get version info for " + targetVersion);

        byte[] data = DownloadUtil.fetchData(versionInfo.downloads.server.url);

        FileUtil.writeData(file, data);

        return file;
    }
    
}
