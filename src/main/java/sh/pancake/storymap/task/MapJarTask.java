/*
 * Created on Sun Sep 27 2020
 *
 * Copyright (c) storycraft. Licensed under the GNU General Public License v3.
 */

package sh.pancake.storymap.task;

import java.io.File;

import org.gradle.api.DefaultTask;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.internal.logging.progress.ProgressLogger;
import org.gradle.internal.logging.progress.ProgressLoggerFactory;
import org.gradle.internal.service.ServiceRegistry;
import io.heartpattern.mcremapper.MCRemapper;
import io.heartpattern.mcremapper.model.LocalVariableFixType;
import io.heartpattern.mcremapper.model.Mapping;
import io.heartpattern.mcremapper.parser.proguard.MappingProguardParser;
import io.heartpattern.mcremapper.preprocess.InheritabilityPreprocessor;
import io.heartpattern.mcremapper.preprocess.SuperTypeResolver;

/*
 * Remap jar file using proguard mapping
 */
public class MapJarTask extends DefaultTask {

    private String inputJar;
    private String inputMapping;
    private boolean reversed;

    private int threadCount = Runtime.getRuntime().availableProcessors() * 2;
    
    private String outputJar;

    @Input
    public String getInputJar() {
        return inputJar;
    }

    @Input
    public String getInputMapping() {
        return inputMapping;
    }

    @Input
    public boolean getReversed() {
        return reversed;
    }

    @Input
    public int getThreadCount() {
        return threadCount;
    }

    @OutputFile
    public String getOutputJar() {
        return outputJar;
    }

    @TaskAction
    public void runTask() throws Exception {
        File inputJarFile = new File(inputJar);
        File outputJarFile = new File(outputJar);

        File parentDir = outputJarFile.getParentFile();

        if (!parentDir.exists()) parentDir.mkdirs();

        ServiceRegistry registry = ((ProjectInternal) getProject()).getServices();
        ProgressLoggerFactory factory = registry.get(ProgressLoggerFactory.class);
        ProgressLogger progressGroup = factory.newOperation(getClass()).setDescription("MapJar");

        progressGroup.started();
        
        Mapping originalMapping = MappingProguardParser.INSTANCE.parse(inputMapping);
        Mapping mapping = reversed ? originalMapping.reversed() : originalMapping;
        mapping = InheritabilityPreprocessor.INSTANCE.preprocess(mapping, inputJarFile);

        // Always delete errors local variable
        MCRemapper remapper = new MCRemapper(mapping, SuperTypeResolver.Companion.fromFile(inputJarFile), LocalVariableFixType.DELETE);

        remapper.applyMapping(inputJarFile, outputJarFile, Runtime.getRuntime().availableProcessors() * 2);

        progressGroup.completed();

    }

}
