/*
 * Created on Mon Sep 28 2020
 *
 * Copyright (c) storycraft. Licensed under the GNU General Public License v3.
 */

package sh.pancake.storymap.resource;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import com.google.gson.Gson;

import sh.pancake.common.object.VersionInfo;
import sh.pancake.common.util.DownloadUtil;
import sh.pancake.launch.util.Hash;
import sh.pancake.launch.util.Hex;

public class MinecraftServerMappingProvider implements IResourceProvider {

    private IResourceProvider versionProvider;

    public MinecraftServerMappingProvider(IResourceProvider versionProvider) {
        this.versionProvider = versionProvider;
    }

    @Override
    public File provide(File directory, String targetVersion, boolean recache) throws Exception {
        File mappingFile = new File(directory, targetVersion + "-mapping.txt");

        VersionInfo versionInfo = new Gson().fromJson(
                Files.readString(versionProvider.provide(directory, targetVersion, recache).toPath()),
                VersionInfo.class);

        if (!recache && mappingFile.exists()) {
            if (versionInfo != null) {
                try (BufferedInputStream input = new BufferedInputStream(new FileInputStream(mappingFile))) {
                    String hex = Hex.byteArrayToHex(Hash.sha1From(input));

                    if (mappingFile.length() == versionInfo.downloads.serverMappings.size
                            && hex.equalsIgnoreCase(versionInfo.downloads.serverMappings.sha1)) {
                        return mappingFile;
                    }
                }

            } else {
                // We cannot validate file :(
                return mappingFile;
            }
        }

        String mapping = DownloadUtil.fetchString(new URL(versionInfo.downloads.serverMappings.url));

        Files.writeString(mappingFile.toPath(), mapping, StandardOpenOption.CREATE);

        return mappingFile;
    }

}
