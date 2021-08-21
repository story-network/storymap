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
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import sh.pancake.sauce.PancakeSauce;
import sh.pancake.sauce.SaucePreprocessor;
import sh.pancake.sauce.parser.ConversionTable;
import sh.pancake.sauce.parser.IDupeResolver;
import sh.pancake.sauce.parser.ProguardParser;

public class MappedMinecraftServerProvider implements IResourceProvider {

    private IResourceProvider serverProvider;
    private IResourceProvider mappingProvider;

    public MappedMinecraftServerProvider(IResourceProvider serverProvider, IResourceProvider mappingProvider) {
        this.serverProvider = serverProvider;
        this.mappingProvider = mappingProvider;
    }

    private boolean filterEntry(ZipEntry entry) {
        String name = entry.getName();

        return !name.contains("/") || name.startsWith("com/mojang") || name.startsWith("net/minecraft");
    }

    @Override
    public File provide(File directory, String targetVersion, boolean recache) throws Exception {
        File file = new File(directory, targetVersion + "-mapped.jar");

        if (recache || !file.exists()) {
            File server = serverProvider.provide(directory, targetVersion, recache);
            String inputMapping = Files.readString(mappingProvider.provide(directory, targetVersion, recache).toPath());

            ProguardParser parser = new ProguardParser(IDupeResolver.SUFFIX_TAG_RESOLVER);

            ConversionTable table = parser.parse(inputMapping);

            try (FileInputStream stream = new FileInputStream(server)) {
                byte[] data = stream.readAllBytes();

                try (ZipInputStream input = new ZipInputStream(new ByteArrayInputStream(data))) {
                    new SaucePreprocessor(input, this::filterEntry).process(table);
                }

                try (
                    ZipInputStream input = new ZipInputStream(new ByteArrayInputStream(data));
                    ZipOutputStream output = new ZipOutputStream(new FileOutputStream(file));
                ) {
                    ExecutorService service = Executors.newCachedThreadPool();

                    new PancakeSauce(
                        input,
                        table,
                        this::filterEntry
                    ).remapJarAsync(service, output).join();

                    service.shutdown();
                }
            }
        }

        return file;
    }
    
}
