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

public class MinecraftServerMappingProvider implements IResourceProvider<String> {

    @Override
    public String provide(ResourceProvider provider, String targetVersion, boolean recache) throws Exception {
        File mappingFile = new File(provider.getStorageDirectory(), targetVersion + "-mapping.txt");
        
        VersionInfo versionInfo = null;
        
        try {
            versionInfo = provider.provide(Constants.VERSION_INFO, targetVersion);
        } catch(Exception e) {

        }

        if (!recache && mappingFile.exists()) {
            byte[] data = FileUtil.readData(mappingFile);

            if (versionInfo != null) {
                String hex = Hex.byteArrayToHex(Hash.sha1From(data));

                if (data.length == versionInfo.downloads.serverMappings.size && hex.equalsIgnoreCase(versionInfo.downloads.serverMappings.sha1)) {
                    return new String(data);
                }
            } else {
                // We cannot validate file :(
                return new String(data);
            }
        }

        if (versionInfo == null) throw new IOException("Cannot get version info for " + targetVersion);

        String mapping = DownloadUtil.fetchString(versionInfo.downloads.serverMappings.url);

        if (mapping == null) throw new IOException("Cannot get mapping for " + targetVersion);

        FileUtil.writeString(mappingFile, mapping);

        return mapping;
    }
    
}
