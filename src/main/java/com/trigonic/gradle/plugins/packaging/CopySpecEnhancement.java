package com.trigonic.gradle.plugins.packaging;

import org.freecompany.redline.payload.Directive;
import org.gradle.api.internal.file.copy.CopySpecInternal;

import org.gradle.api.internal.file.FileResolver;
import org.gradle.api.internal.file.copy.DefaultCopySpec;
import org.gradle.internal.reflect.Instantiator;

/**
 * Try to mark up CopySpec
 */
//@Category(CopySpecInternal)
/**
 * CopySpec will nest in into() blocks, and Gradle will instantiate DefaultCopySpec itself, we have no ability to inject
 * our own. When appending another copy spec to the task, it'll be created a WrapperCopySpec. In 1.8, there's a slight
 * possibility that we override addChild so we can provide our own instances, or we mod the instantiator.
 *
 * Support some additional properties on CopySpecs. Using CopySpecInternal since its the parent of both DefaultCopySpec
 * and RelativizedCopySpec. The old way used the metaClass on the Class, which effected all CopySpecs in the build. This
 * limits the scope to just without our from/into/rename methods. This style also gives us type safety.
 *
 * It's implemented so that either format of setting is supported, e.g. the Gradle "name value" form or the typical
 * assignment form "name = value".
 *
 */
public class CopySpecEnhancement extends DefaultCopySpec {
    
    protected int gid;
    protected int uid;
    protected boolean createDirectoryEntry;
    protected boolean addParentDirs;
    protected Directive fileType;
    protected String permissionGroup;
    protected String user;

    public CopySpecEnhancement(FileResolver resolver, Instantiator instantiator, DefaultCopySpec parentSpec) {
        super(resolver, instantiator, parentSpec);
    }

    public CopySpecEnhancement(FileResolver resolver, Instantiator instantiator) {
        super(resolver, instantiator);
    }    

    public void user(String user) {
        this.user=user;
    }

    public void setUser(String user) {
        this.user=user;
    }

    public void permissionGroup(String permissionGroup) {
        this.permissionGroup=permissionGroup;
    }

    public void setPermissionGroup(String permissionGroup) {
        this.permissionGroup=permissionGroup;
    }

    /**
     * RPM Only
     */
    public void fileType(Directive fileType) {
        this.fileType=fileType;
    }

    /**
     * RPM Only
     */
    public void setfileType(Directive fileType) {
        this.fileType=fileType;
    }

    /**
     * RPM Only
     */
    public void addParentDirs(boolean addParentDirs) {
        this.addParentDirs=addParentDirs;
    }

    /**
     * RPM Only
     */
    public void setAddParentDirs(boolean addParentDirs) {
        this.addParentDirs=addParentDirs;
    }

    /**
     * RPM Only
     */
    public void createDirectoryEntry(boolean createDirectoryEntry) {
        this.createDirectoryEntry=createDirectoryEntry;
    }

    /**
     * RPM Only
     */
    public void setCreateDirectoryEntry(boolean createDirectoryEntry) {
        this.createDirectoryEntry=createDirectoryEntry;
    }

    /**
     * DEB Only
     */
    public void uid(int uid) {
        this.uid=uid;
    }

    /**
     * DEB Only
     */
    public void setUid(int uid) {
        this.uid=uid;
    }

    /**
     * DEB Only
     */
    public void gid(CopySpecInternal spec, int gid) {
        this.gid=gid;
    }

    /**
     * DEB Only
     */
    public void setGid(int gid) {
        this.gid=gid;
    }
}