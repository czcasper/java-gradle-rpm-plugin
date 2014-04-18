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

import com.trigonic.gradle.plugins.helpers.CallableContainer;
import groovy.lang.Closure;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.Callable;
import org.gradle.api.internal.ConventionMapping;
import org.gradle.api.internal.IConventionAware;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.AbstractCopyTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;

public abstract class SystemPackagingTask extends AbstractArchiveTask {

    protected SystemPackagingExtension exten; // Not File extension or ext list of properties, different kind of Extension

    protected ProjectPackagingExtension parentExten;

    // TODO Add conventions to pull from extension
    public SystemPackagingTask() {
        super();
        exten = new SystemPackagingExtension();
        parentExten = getProject().getExtensions().findByType(ProjectPackagingExtension.class);

        if (parentExten != null) {
            getRootSpec().with(parentExten.getDelegateCopySpec());
        }
    }

    // TODO Move outside task, since it's specific to a plugin
    protected void applyConventions() {
        // For all mappings, we're only being called if it wasn't explicitly set on the task. In which case, we'll want
        // to pull from the parentExten. And only then would we fallback on some other value.
        ConventionMapping mapping = ((IConventionAware) this).getConventionMapping(); // Could come from extension
        String value = null;
        if (parentExten != null) {
            value = parentExten.getPackageName();
        }
        if (value == null) {
            value = this.getBaseName();
        }
        mapping.map("packageName", new CallableContainer<>((value == null) ? "" : value));

        value = null;
        if (parentExten != null) {
            value = parentExten.getRelease();
        }
        if (value == null) {
            value = this.getClassifier();
        }
        mapping.map("release", new CallableContainer<>((value == null) ? "" : value));

        value = null;
        if (parentExten != null) {
            value = parentExten.getVersion();
        }
        if (value == null) {
            value = getProject().getVersion().toString();
        }
        mapping.map("version", new CallableContainer<>((value == null) ? "" : value));

        value = null;
        if (parentExten != null) {
            value = parentExten.getUser();
            if (value == null) {
                value = parentExten.getPackager();
            }

        }
        mapping.map("user", new CallableContainer<>((value == null) ? "" : value));

        value = null;
        if (parentExten != null) {
            value = parentExten.getPermissionGroup();
        }
        mapping.map("permissionGroup", new CallableContainer<>((value == null) ? "" : value));

        value = null;
        if (parentExten != null) {
            value = parentExten.getPackageGroup();
        }
        mapping.map("packageGroup", new CallableContainer<>((value == null) ? "" : value));

        if (parentExten != null) {
            value = parentExten.getBuildHost();
        }else{
            value = getLocalHostName();
        }
        mapping.map("buildHost", new CallableContainer<>(value));

        value = null;
        if (parentExten != null) {
            value = parentExten.getSummary();
            if (value == null) {
                value = parentExten.getPackageName();
            }
        }
        mapping.map("summary", new CallableContainer<>((value == null) ? "" : value));

        value = null;
        if (parentExten != null) {
            value = parentExten.getPackageDescription();
        }
        if (value == null) {
            value = getProject().getDescription();
        }
        mapping.map("packageDescription", new CallableContainer<>((value == null) ? "" : value));

        value = null;
        if (parentExten != null) {
            value = parentExten.getLicense();
        }
        mapping.map("license", new CallableContainer<>((value == null) ? "" : value));

        value = null;
        if (parentExten != null) {
            value = parentExten.getPackager();
        }
        if (value == null) {
            value = System.getProperty("user.name");
        }
        mapping.map("packager", new CallableContainer<>((value == null) ? "" : value));

        value = null;
        if (parentExten != null) {
            value = parentExten.getDistribution();
        }
        mapping.map("distribution", new CallableContainer<>((value == null) ? "" : value));
        
        value = null;
        if (parentExten != null) {
            value = parentExten.getVendor();
        }
        mapping.map("vendor", new CallableContainer<>((value == null) ? "" : value));
        
        value = null;
        if (parentExten != null) {
            value = parentExten.getUrl();
        }
        mapping.map("url", new CallableContainer<>((value == null) ? "" : value));
        
        value = null;
        if (parentExten != null) {
            value = parentExten.getSourcePackage();
        }
        mapping.map("sourcePackage", new CallableContainer<>((value == null) ? "" : value));

        value = null;
        if (parentExten != null) {
            value = parentExten.getProvides();
            if (value == null) {
                value = parentExten.getPackageName();
            }
        }
        mapping.map("provides", new CallableContainer<>((value == null) ? "" : value));
        
        Boolean boolValue = null;
        if(parentExten !=null){
            boolValue = parentExten.isCreateDirectoryEntry();
        }
        mapping.map("createDirectoryEntry", new CallableContainer<>((boolValue == null) ? false : boolValue));


        // Task Specific
        mapping.map("archiveName", new CallableContainer<>(assembleArchiveName()));
    }

    public abstract String assembleArchiveName();

    protected static String getLocalHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ignore) {
            return "unknown";
        }
    }

    @Override
    @TaskAction
    protected void copy() {
        // TODO find where is this copy action comming from.
  /*      if(parentExten!=null){
            parentExten.;
        }
        use(CopySpecEnhancement) {
            super.copy()
        }
          */
    }

    public List<String> getAllPreInstallCommands() {
        List<String> retValue=null;
        if(parentExten!=null){
            retValue = parentExten.getPreInstallCommands();
        }
        return retValue;
    }

    public List<String> getAllPostInstallCommands() {
        List<String> retValue=null;
        if(parentExten!=null){
            retValue = parentExten.getPostInstallCommands();
        }
        return retValue;
    }

    public List<String> getAllPreUninstallCommands() {
        List<String> retValue=null;
        if(parentExten!=null){
            retValue = parentExten.getPreUninstallCommands();
        }
        return retValue;
    }

    public List<String> getAllPostUninstallCommands() {
        List<String> retValue=null;
        if(parentExten!=null){
            retValue = parentExten.getPostUninstallCommands();
        }
        return retValue;
    }

    public List<String> getAllCommonCommands() {
        List<String> retValue=null;
        if(parentExten!=null){
            retValue = parentExten.getCommonCommands();
        }
        return retValue;
    }

    public List<Link> getAllLinks() {
        List<Link> retValue = null;
        if (parentExten!=null) {
            retValue =parentExten.getLinks();
        }
        return retValue;
    }

    public List<Dependency> getAllDependencies() {
        List<Dependency> retValue = null;
        if (parentExten!=null) {
            retValue =parentExten.getDependencies();
        }
        return retValue;
    }

    @Override
    public AbstractCopyTask from(Object sourcePath, Closure c) {
        if(parentExten!=null){
            parentExten.from(sourcePath, c);
        }else{
            getMainSpec().from(sourcePath, c);
        }
        return this;
    }

    @Override
    public AbstractArchiveTask into(Object destPath, Closure configureClosure) {
        if(parentExten!=null){
            parentExten.into(destPath, configureClosure);
        }else{
            getMainSpec().into(destPath, configureClosure);
        }
        return this;
    }

    @Override
    public AbstractCopyTask exclude(Closure excludeSpec) {
        if(parentExten!=null){
            parentExten.exclude(excludeSpec);
        }else{
            getMainSpec().exclude(excludeSpec);
        }
        return this;
    }

    @Override
    public AbstractCopyTask filter(Closure closure) {
        if(parentExten!=null){
            parentExten.filter(closure);
        }else{
            getMainSpec().filter(closure);
        }
        return this;
    }

    @Override
    public AbstractCopyTask rename(Closure closure) {
        if(parentExten!=null){
            parentExten.rename(closure);
        }else{
            getMainSpec().rename(closure);
        }
        return this;
    }

    public ProjectPackagingExtension getParentExten() {
        return parentExten;
    }

    public void setParentExten(ProjectPackagingExtension parentExten) {
        this.parentExten = parentExten;
    }

    @Override
    public abstract AbstractPackagingCopyAction createCopyAction();

    protected abstract String getArchString();
    
}
