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
package com.trigonic.gradle.plugins.rpm;

import com.google.common.base.Preconditions;
import com.trigonic.gradle.plugins.packaging.AbstractPackagingCopyAction;
import com.trigonic.gradle.plugins.packaging.Dependency;
import com.trigonic.gradle.plugins.packaging.Link;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.logging.Level;
import org.freecompany.redline.Builder;
import org.freecompany.redline.header.Header.HeaderTag;
import org.freecompany.redline.header.Os;
import org.freecompany.redline.payload.Directive;
import org.gradle.api.internal.ConventionMapping;
import org.gradle.api.internal.IConventionAware;
import org.gradle.api.internal.file.copy.CopyAction;
import org.gradle.api.internal.file.copy.CopySpecInternal;
import org.gradle.api.internal.file.copy.FileCopyDetailsInternal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpmCopyAction extends AbstractPackagingCopyAction {

    static final Logger logger = LoggerFactory.getLogger(RpmCopyAction.class);

    Rpm rpmTask;
    Builder builder;
    boolean includeStandardDefines = true;// candidate for being pushed up to packaging level

    RpmCopyAction(Rpm rpmTask) {
        super(rpmTask);
        this.rpmTask = rpmTask;
    }

    @Override
    public void startVisit(CopyAction action) {
        super.startVisit(action);

        assert rpmTask.getVersion() != null;

        builder = new Builder();
        builder.setPackage(rpmTask.getParentExten().getPackageName(), rpmTask.getParentExten().getVersion(), rpmTask.getParentExten().getRelease());
        builder.setType(rpmTask.getParentExten().getType());
        builder.setPlatform(rpmTask.getParentExten().getArch(), rpmTask.getParentExten().getOs());
        builder.setGroup(rpmTask.getParentExten().getPackageGroup());
        builder.setBuildHost(rpmTask.getParentExten().getBuildHost());
        builder.setSummary(rpmTask.getParentExten().getSummary());
        builder.setDescription(rpmTask.getParentExten().getPackageDescription());
        builder.setLicense(rpmTask.getParentExten().getLicense());
        builder.setPackager(rpmTask.getParentExten().getPackager());
        builder.setDistribution(rpmTask.getParentExten().getDistribution());
        builder.setVendor(rpmTask.getParentExten().getVendor());
        builder.setUrl(rpmTask.getParentExten().getUrl());
        builder.setProvides(rpmTask.getParentExten().getProvides());

        String sourcePackage = rpmTask.getParentExten().getSourcePackage();
        if (sourcePackage == null || sourcePackage.isEmpty()) {
            //TODO Fix this missing implementation, value is comming somewhere proably from groovy injection
            // need a source package because createrepo will assume your package is a source package without it
//            sourcePackage = builder.defaultSourcePackage;
        }
        builder.addHeaderEntry(HeaderTag.SOURCERPM, sourcePackage);

        builder.setPreInstallScript(scriptWithUtils(rpmTask.getAllCommonCommands(), rpmTask.getAllPreInstallCommands()));
        builder.setPostInstallScript(scriptWithUtils(rpmTask.getAllCommonCommands(), rpmTask.getAllPostInstallCommands()));
        builder.setPreUninstallScript(scriptWithUtils(rpmTask.getAllCommonCommands(), rpmTask.getAllPostUninstallCommands()));
        builder.setPostUninstallScript(scriptWithUtils(rpmTask.getAllCommonCommands(), rpmTask.getAllPostUninstallCommands()));
    }

    @Override
    public void visitFile(FileCopyDetailsInternal fileDetails, CopySpecInternal specToLookAt) {
        logger.debug("Adding file {0}", fileDetails.getRelativePath().getPathString());
        File outputFile = extractFile(fileDetails);
//        ConventionMapping mapping = ((IConventionAware) rpmTask).getConventionMapping();
        
        String path = "/" + fileDetails.getRelativePath().getPathString();
        //TODO Find way how to get addtional infromation from method parameters
/*        int fileMode = lookup(specToLookAt, 'fileMode'
        ) ?: -1
        Directive fileType = lookup(specToLookAt, 'fileType'
        )
        String user = lookup(specToLookAt, 'user'
        ) ?: rpmTask.user String group = lookup(specToLookAt, 'permissionGroup'
        ) ?: rpmTask.permissionGroup def specAddParentsDir = lookup(specToLookAt, 'addParentDirs'
        )
        boolean addParentsDir = specAddParentsDir != null ? specAddParentsDir : rpmTask.addParentDirs
*/
//        builder.addFile(path, outputFile, fileMode, -1, fileType, user, group, addParentsDir);
    }

    @Override
    public void visitDir(FileCopyDetailsInternal dirDetails, CopySpecInternal specToLookAt) {
        // TODO find way how to extrac parameters from method parameters.
/*        if (specToLookAt == null) {
            logger.info("Got an empty spec from ${dirDetails.class.name} for ${dirDetails.path}/${dirDetails.name}")
            return
        }
        // Have to take booleans specially, since they would fail an elvis operator if set to false
        def specCreateDirectoryEntry = lookup(specToLookAt, 'createDirectoryEntry'
        )
        boolean createDirectoryEntry = specCreateDirectoryEntry != null ? specCreateDirectoryEntry : rpmTask.createDirectoryEntry
        def specAddParentsDir = lookup(specToLookAt, 'addParentDirs'
        )
        boolean addParentsDir = specAddParentsDir != null ? specAddParentsDir : rpmTask.addParentDirs
        if (createDirectoryEntry) {
            logger.debug "adding directory {}", dirDetails.relativePath.pathString builder
            .addDirectory(
                    "/" + dirDetails.relativePath.pathString,
                    (int) (lookup(specToLookAt, 'dirMode') ?: -1),
                    (Directive
            ) lookup(specToLookAt, 'fileType') ?: rpmTask.fileType,
                    (String
            ) lookup(specToLookAt, 'user') ?: rpmTask.user,
                    (String
            ) lookup(specToLookAt, 'permissionGroup') ?: rpmTask.permissionGroup,
                    (boolean) addParentsDir
        
    

    )
        }*/
    }

    @Override
    protected void addLink(Link link) {
        try {
            builder.addLink(link.getPath(), link.getTarget(), link.getPermissions());
        } catch (NoSuchAlgorithmException | IOException ex) {
            java.util.logging.Logger.getLogger(RpmCopyAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void addDependency(Dependency dep) {
        builder.addDependency(dep.getPackageName(), dep.getFlag(), dep.getVersion());
    }

    @Override
    protected void end() {
        try {
            File rpmFile = rpmTask.getArchivePath();
            FileChannel fc = new RandomAccessFile(rpmFile, "rw").getChannel();
            builder.build(fc);
            logger.info("Created rpm"+rpmFile);  
        } catch (NoSuchAlgorithmException | IOException ex) {
            java.util.logging.Logger.getLogger(RpmCopyAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected String standardScriptDefines() {
        String retValue = "";
        if (includeStandardDefines) {
            retValue=String.format(" RPM_ARCH=%s \n RPM_OS=%s \n RPM_PACKAGE_NAME=%s \n RPM_PACKAGE_VERSION=%s \n RPM_PACKAGE_RELEASE=%s \n\n",
                    rpmTask.getArchString(),
                    rpmTask.getParentExten().getOs().toString().toLowerCase(),
                    rpmTask.getParentExten().getPackageName(),
                    rpmTask.getParentExten().getVersion(),
                    rpmTask.getParentExten().getRelease());
        }
        return retValue;
    }

    protected String scriptWithUtils(Collection<String> utils, Collection<String> scripts) {
        StringBuilder retValue = new StringBuilder();
        if(includeStandardDefines){
            retValue.append(standardScriptDefines());
        }
        if(utils!=null){
            for(String s : utils){
                retValue.append(s);
            }
        }
        
        if(scripts!=null){
            for (String s : scripts) {
                retValue.append(s);
            }
        }
        return retValue.toString();
                
    }
}
