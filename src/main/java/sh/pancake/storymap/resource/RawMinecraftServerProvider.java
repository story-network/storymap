/*
 * Created on Mon Sep 28 2020
 *
 * Copyright (c) storycraft. Licensed under the GNU General Public License v3.
 */

package sh.pancake.storymap.resource;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.file.Files;

import com.google.gson.Gson;

import sh.pancake.launch.object.VersionInfo;
import sh.pancake.launch.util.Hash;
import sh.pancake.launch.util.Hex;

public class RawMinecraftServerProvider implements IResourceProvider {

    private IResourceProvider versionProvider;

    public RawMinecraftServerProvider(IResourceProvider versionProvider) {
        this.versionProvider = versionProvider;
    }

    @Override
    public File provide(File directory, String targetVersion, boolean recache) throws Exception {
        File serverFile = new File(directory, targetVersion + "-raw.jar");

        VersionInfo versionInfo = new Gson().fromJson(
                Files.readString(versionProvider.provide(directory, targetVersion, recache).toPath()),
                VersionInfo.class);

        if (!recache && serverFile.exists()) {
            if (versionInfo == null) return serverFile;

            try (BufferedInputStream input = new BufferedInputStream(new FileInputStream(serverFile))) {
                String hex = Hex.byteArrayToHex(Hash.sha1From(input));

                if (serverFile.length() == versionInfo.downloads.serverMappings.size
                        && hex.equalsIgnoreCase(versionInfo.downloads.server.sha1)) {
                    return serverFile;
                }
            }
        }

        try (
            BufferedInputStream downloadStream = new BufferedInputStream(new URL(versionInfo.downloads.server.url).openStream());
            FileOutputStream output = new FileOutputStream(serverFile);
        ) {
            downloadStream.transferTo(output);
        }

        return serverFile;
    }

}
