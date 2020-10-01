/*
 * Created on Mon Sep 28 2020
 *
 * Copyright (c) storycraft. Licensed under the GNU General Public License v3.
 */

package sh.pancake.storymap.resource;

import java.io.File;

import io.heartpattern.mcremapper.MCRemapper;
import io.heartpattern.mcremapper.model.LocalVariableFixType;
import io.heartpattern.mcremapper.model.Mapping;
import io.heartpattern.mcremapper.parser.proguard.MappingProguardParser;
import io.heartpattern.mcremapper.preprocess.InheritabilityPreprocessor;
import io.heartpattern.mcremapper.preprocess.SuperTypeResolver;
import sh.pancake.storymap.Constants;
import sh.pancake.storymap.ResourceProvider;

public class MappedMinecraftServerProvider implements IResourceProvider<File> {

    @Override
    public File provide(ResourceProvider provider, String targetVersion, boolean recache) throws Exception {
        File file = new File(provider.getStorageDirectory(), targetVersion + "-mapped.jar");

        if (recache || !file.exists()) {
            File server = provider.provide(Constants.MINECRAFT_SERVER_RAW, targetVersion, recache);
            String inputMapping = provider.provide(Constants.MINECRAFT_SERVER_MAPPING, targetVersion, recache);

            Mapping originalMapping = MappingProguardParser.INSTANCE.parse(inputMapping);
            Mapping mapping = originalMapping.reversed();
            mapping = InheritabilityPreprocessor.INSTANCE.preprocess(mapping, server);

            // Always delete errors local variable cuz we will use on codespace
            MCRemapper remapper = new MCRemapper(mapping, SuperTypeResolver.Companion.fromFile(server), LocalVariableFixType.DELETE);

            remapper.applyMapping(server, file, Runtime.getRuntime().availableProcessors() * 2);
        }

        return file;
    }
    
}
