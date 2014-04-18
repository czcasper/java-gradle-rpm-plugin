/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.trigonic.gradle.plugins.packaging;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.logging.Level;
import org.gradle.api.file.FileVisitDetails;
import org.gradle.api.internal.file.CopyActionProcessingStreamAction;
import org.gradle.api.internal.file.copy.CopyAction;
import org.gradle.api.internal.file.copy.CopyActionProcessingStream;
import org.gradle.api.internal.file.copy.CopySpecInternal;
import org.gradle.api.internal.file.copy.DefaultFileCopyDetails;
import org.gradle.api.internal.file.copy.FileCopyDetailsInternal;
import org.gradle.api.internal.tasks.SimpleWorkResult;
import org.gradle.api.tasks.WorkResult;
import org.gradle.internal.UncheckedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPackagingCopyAction implements CopyAction {

    static final Logger logger = LoggerFactory.getLogger(AbstractPackagingCopyAction.class);

    protected SystemPackagingTask task;
    protected File tempDir;
    protected Collection<File> filteredFiles;

    protected AbstractPackagingCopyAction(SystemPackagingTask task) {
        this.task = task;
    }

    @Override
    public WorkResult execute(CopyActionProcessingStream stream) {
        startVisit(this);
        stream.process(new StreamAction());
        endVisit();
        return new SimpleWorkResult(true);
    }

    // Not a static class
    private class StreamAction implements CopyActionProcessingStreamAction {

        @Override
        public void processFile(FileCopyDetailsInternal details) {
            // While decoupling the spec from the action is nice, it contains some needed info
            CopySpecInternal ourSpec = extractSpec(details); // Can be null
            if (details.isDirectory()) {
                visitDir(details, ourSpec);
            } else {
                visitFile(details, ourSpec);
            }
        }
    }

    protected abstract void visitDir(FileCopyDetailsInternal dirDetails, CopySpecInternal specToLookAt);

    protected abstract void visitFile(FileCopyDetailsInternal fileDetails, CopySpecInternal specToLookAt);

    protected abstract void addLink(Link link);

    protected abstract void addDependency(Dependency dependency);

    protected abstract void end();

    public void startVisit(CopyAction action) {
        // Delay reading destinationDir until we start executing
        tempDir = task.getTemporaryDir();
    }

    void visitFinally(Exception e) {
    }

    public void endVisit() {

        for (Link link : task.getAllLinks()) {
            logger.debug("adding link " + link.getPath() + " -> " + link.getTarget());
            addLink(link);
        }

        for (Dependency dep : task.getAllDependencies()) {
            logger.debug("adding dependency on " + dep.getPackageName() + " " + dep.getVersion());
            addDependency(dep);
        }

        end(); // TODO Clean up filteredFiles
        // TODO Investigate, we seem to always set to true.
    }
    
    // TODO suport for scripts from files.

    protected String concat(Collection<String> scripts) {
        String shebang = "";
        StringBuilder result = new StringBuilder();
        if (scripts != null && !scripts.isEmpty()) {
            for (String script : scripts) {
                if (script != null && !script.isEmpty()) {
                    BufferedReader bufReader = new BufferedReader(new StringReader(script));
                    String line;
                    try {
                        while ((line = bufReader.readLine()) != null) {
                            if (line.matches("^#!.*$")) {
                                if (shebang.isEmpty()) {
                                    shebang = line;
                                } else if (shebang.compareTo(line) != 0) {
                                    throw new IllegalArgumentException("mismatching #! script lines");
                                }
                            } else {
                                result.append(line);
                                result.append("\n");

                            }
                        }
                    } catch (IOException ex) {
                        java.util.logging.Logger.getLogger(AbstractPackagingCopyAction.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }
            if (!shebang.isEmpty()) {
                result.insert(0, shebang + "\n");
            }

        }

        return result.toString();
    }

    protected String stripSheBank(File scrip) {
        StringBuilder result = new StringBuilder();
        if ((scrip != null) && (scrip.exists())) {
            try {
                BufferedReader bufReader = new BufferedReader(new FileReader(scrip));
                String line;
                try {
                    while ((line = bufReader.readLine()) != null) {
                        if (!line.matches("^#!.*$")) {
                            result.append(line);
                            result.append("\n");
                        }
                    }
                } catch (IOException ex) {
                    java.util.logging.Logger.getLogger(AbstractPackagingCopyAction.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (FileNotFoundException ex) {
                java.util.logging.Logger.getLogger(AbstractPackagingCopyAction.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return result.toString();
    }

    /**
     * Works with nulls, Strings.
     *
     * @param script
     * @return
     */
    protected String stripShebang(String script) {
        StringBuilder result = new StringBuilder();
        if (script != null && !script.isEmpty()) {
            BufferedReader bufReader = new BufferedReader(new StringReader(script));
            String line;
            try {
                while ((line = bufReader.readLine()) != null) {
                    if (!line.matches("^#!.*$")) {
                        result.append(line);
                        result.append("\n");
                    }
                }
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(AbstractPackagingCopyAction.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result.toString();

    }

    /* def

     static lookup(def specToLookAt, String propertyName) {
     if (specToLookAt ?  {

     }.metaClass ?.hasProperty(specToLookAt, propertyName) != null
        
            
     ) {
     return specToLookAt.metaClass.getProperty(specToLookAt, propertyName)
     }else {
     return null
     }
     }
     */
    public CopySpecInternal extractSpec(FileCopyDetailsInternal fileDetails) {
        CopySpecInternal retValue = null;
        if (fileDetails instanceof DefaultFileCopyDetails) {
            Class<?> startingClass = fileDetails.getClass(); // It's in there somewhere
            while (startingClass != null && startingClass.equals(DefaultFileCopyDetails.class)) {
                startingClass = startingClass.getSuperclass();
            }
            if (startingClass != null) {
                try {
                    Field specField = startingClass.getDeclaredField("spec");
                    specField.setAccessible(true);
                    retValue = (CopySpecInternal) specField.get(fileDetails);
                } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {
                    java.util.logging.Logger.getLogger(AbstractPackagingCopyAction.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return retValue;
    }

    /**
     * Look at FileDetails to get a file. If it's filtered file, we need to
     * write it out to the filesystem ourselves. Issue #30, FileVisitDetailsImpl
     * won't give us file, since it filters on the fly.
     */
    public File extractFile(FileCopyDetailsInternal fileDetails) {
        File outputFile = null;
        try {
            outputFile = fileDetails.getFile();
        } catch (UnsupportedOperationException uoe) {
            // Can't access MappingCopySpecVisitor.FileVisitDetailsImpl since it's private, so we have to probe. We would test this:
            // if (fileDetails instanceof MappingCopySpecVisitor.FileVisitDetailsImpl && fileDetails.filterChain.hasFilters())
            outputFile = new File(tempDir, fileDetails.getName());
            fileDetails.copyTo(outputFile);
            filteredFiles.add(outputFile);
        }
        return outputFile;
    }
}
