/*
 * Created on Mon Sep 28 2020
 *
 * Copyright (c) storycraft. Licensed under the GNU General Public License v3.
 */

package sh.pancake.storymap.resource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import sh.pancake.sauce.PancakeSauce;
import sh.pancake.sauce.SaucePreprocessor;
import sh.pancake.sauce.parser.ConversionTable;
import sh.pancake.sauce.parser.IDupeResolver;
import sh.pancake.sauce.parser.ProguardParser;
import sh.pancake.storymap.Constants;
import sh.pancake.storymap.ResourceProvider;

public class MappedMinecraftServerProvider implements IResourceProvider<File> {

    @Override
    public File provide(ResourceProvider provider, String targetVersion, boolean recache) throws Exception {
        File file = new File(provider.getStorageDirectory(), targetVersion + "-mapped.jar");

        if (recache || !file.exists()) {
            File server = provider.provide(Constants.MINECRAFT_SERVER_RAW, targetVersion, recache);
            String inputMapping = provider.provide(Constants.MINECRAFT_SERVER_MAPPING, targetVersion, recache);

            ProguardParser parser = new ProguardParser(IDupeResolver.SUFFIX_TAG_RESOLVER);

            ConversionTable table = parser.parse(inputMapping);

            try (FileInputStream stream = new FileInputStream(server)) {
                byte[] data = stream.readAllBytes();

                try (ByteArrayInputStream input = new ByteArrayInputStream(data)) {
                    new SaucePreprocessor().process(new ZipInputStream(input), table);
                }

                try (
                    ByteArrayInputStream input = new ByteArrayInputStream(data);
                    FileOutputStream output = new FileOutputStream(file);
                ) {
                    new PancakeSauce(new ZipInputStream(input), table).remapJar(new ZipOutputStream(output));
                }
            }
        }

        return file;
    }
    
}
