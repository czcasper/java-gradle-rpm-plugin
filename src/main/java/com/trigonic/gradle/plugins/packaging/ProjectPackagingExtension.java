package com.trigonic.gradle.plugins.packaging;

import groovy.lang.Closure;
import java.io.FilterReader;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.file.CopyProcessingSpec;
import org.gradle.api.file.CopySourceSpec;
import org.gradle.api.file.CopySpec;
import org.gradle.api.file.DuplicatesStrategy;
import org.gradle.api.file.FileCopyDetails;
import org.gradle.api.file.FileTree;
import org.gradle.api.file.FileTreeElement;
import org.gradle.api.file.RelativePath;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.api.internal.file.copy.CopySpecInternal;
import org.gradle.api.internal.file.copy.DefaultCopySpec;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.specs.Spec;
import org.gradle.internal.reflect.Instantiator;

/**
 * An extension which can be attached to the project. This is a superset of SystemPackagingExtension because we don't
 * want the @Delegate to inherit the copy spec parts.
 *
 * We can't extends DefaultCopySpec, since it's @NotExtensible, meaning that we won't get any convention
 * mappings. If we extend DelegatingCopySpec we get groovy compilation errors around the return types between
 * CopySourceSpec's methods and the ones overriden in DelegatingCopySpec, even though that's perfectly valid
 * Java code. The theory is that it's some bug in groovyc.
 */
public class ProjectPackagingExtension extends SystemPackagingExtension implements CopySpecInternal {

    protected CopySpecEnhancement delegateCopySpec;

    // @Inject // Not supported yet.
    public ProjectPackagingExtension(Project project) {
        FileResolver resolver = ((ProjectInternal) project).getFileResolver();
        Instantiator instantiator = ((ProjectInternal) project).getServices().get(Instantiator.class);
        delegateCopySpec = new CopySpecEnhancement(resolver, instantiator);
    }


    /*
     * Special Use cases that involve Closure's which we want to wrap:
     */
    @Override
    public CopySpec from(Object sourcePath, Closure c) {
        return delegateCopySpec.from(sourcePath, c);
    }

    @Override
    public CopySpec into(Object destPath, Closure configureClosure) {
        return delegateCopySpec.into(destPath, configureClosure);
    }

    @Override
    public CopySpec include(Closure includeSpec) {
        return delegateCopySpec.include(includeSpec);
    }

    @Override
    public CopySpec exclude(Closure excludeSpec) {
        return delegateCopySpec.exclude(excludeSpec);
    }

    @Override
    public CopySpec filter(Closure closure) {
        return delegateCopySpec.filter(closure);
    }

    @Override
    public CopySpec rename(Closure closure) {
        return delegateCopySpec.rename(closure);
    }

    @Override
    public CopySpec eachFile(Closure closure) {
        return delegateCopySpec.eachFile(closure);
    }

    /*
     * Copy and Paste from org.gradle.api.internal.file.copy.DelegatingCopySpec, since extending it causes
     * compilation problems. The methods above are special cases and are commented out below.
     */
    @Override
    public RelativePath getDestPath() {
        return delegateCopySpec.getDestPath();
    }

    @Override
    public FileTree getSource() {
        return delegateCopySpec.getSource();
    }

    @Override
    public boolean hasSource() {
        return delegateCopySpec.hasSource();
    }

    @Override
    public Collection<? extends Action<? super FileCopyDetails>> getAllCopyActions() {
        return delegateCopySpec.getAllCopyActions();
    }

    @Override
    public boolean isCaseSensitive() {
        return delegateCopySpec.isCaseSensitive();
    }

    @Override
    public void setCaseSensitive(boolean caseSensitive) {
        delegateCopySpec.setCaseSensitive(caseSensitive);
    }

    @Override
    public boolean getIncludeEmptyDirs() {
        return delegateCopySpec.getIncludeEmptyDirs();
    }

    @Override
    public void setIncludeEmptyDirs(boolean includeEmptyDirs) {
        delegateCopySpec.setIncludeEmptyDirs(includeEmptyDirs);
    }

    @Override
    public DuplicatesStrategy getDuplicatesStrategy() {
        return delegateCopySpec.getDuplicatesStrategy();
    }

    @Override
    public void setDuplicatesStrategy(DuplicatesStrategy strategy) {
        delegateCopySpec.setDuplicatesStrategy(strategy);
    }

    @Override
    public CopySpec filesMatching(String pattern, Action<? super FileCopyDetails> action) {
        return delegateCopySpec.filesMatching(pattern, action);
    }

    @Override
    public CopySpec filesNotMatching(String pattern, Action<? super FileCopyDetails> action) {
        return delegateCopySpec.filesNotMatching(pattern, action);
    }

    @Override
    public CopySpec with(CopySpec... sourceSpecs) {
        return delegateCopySpec.with(sourceSpecs);
    }

    @Override
    public CopySpec setIncludes(Iterable<String> includes) {
        return delegateCopySpec.setIncludes(includes);
    }

    @Override
    public CopySpec setExcludes(Iterable<String> excludes) {
        return delegateCopySpec.setExcludes(excludes);
    }

    @Override
    public CopySpec include(String... includes) {
        return delegateCopySpec.include(includes);
    }

    @Override
    public CopySpec include(Iterable<String> includes) {
        return delegateCopySpec.include(includes);
    }

    @Override
    public CopySpec include(Spec<FileTreeElement> includeSpec) {
        return delegateCopySpec.include(includeSpec);
    }

    @Override
    public CopySpec exclude(String... excludes) {
        return delegateCopySpec.exclude(excludes);
    }

    @Override
    public CopySpec exclude(Iterable<String> excludes) {
        return delegateCopySpec.exclude(excludes);
    }

    @Override
    public CopySpec exclude(Spec<FileTreeElement> excludeSpec) {
        return delegateCopySpec.exclude(excludeSpec);
    }

    @Override
    public CopySpec into(Object destPath) {
        return delegateCopySpec.into(destPath);
    }

    @Override
    public CopySpec rename(String sourceRegEx, String replaceWith) {
        return delegateCopySpec.rename(sourceRegEx, replaceWith);
    }

    @Override
    public CopyProcessingSpec rename(Pattern sourceRegEx, String replaceWith) {
        return delegateCopySpec.rename(sourceRegEx, replaceWith);
    }

    @Override
    public CopySpec filter(Map<String, ?> properties, Class<? extends FilterReader> filterType) {
        return delegateCopySpec.filter(properties, filterType);
    }

    @Override
    public CopySpec filter(Class<? extends FilterReader> filterType) {
        return delegateCopySpec.filter(filterType);
    }

    @Override
    public CopySpec expand(Map<String, ?> properties) {
        return delegateCopySpec.expand(properties);
    }

    @Override
    public CopySpec eachFile(Action<? super FileCopyDetails> action) {
        return delegateCopySpec.eachFile(action);
    }

    @Override
    public Integer getFileMode() {
        return delegateCopySpec.getFileMode();
    }

    @Override
    public CopyProcessingSpec setFileMode(Integer mode) {
        return delegateCopySpec.setFileMode(mode);
    }

    @Override
    public Integer getDirMode() {
        return delegateCopySpec.getDirMode();
    }

    @Override
    public CopyProcessingSpec setDirMode(Integer mode) {
        return delegateCopySpec.setDirMode(mode);
    }

    @Override
    public Set<String> getIncludes() {
        return delegateCopySpec.getIncludes();
    }

    @Override
    public Set<String> getExcludes() {
        return delegateCopySpec.getExcludes();
    }

    @Override
    public Iterable<CopySpecInternal> getChildren() {
        return delegateCopySpec.getChildren();
    }

    @Override
    public FileTree getAllSource() {
        return delegateCopySpec.getAllSource();
    }

    @Override
    public DefaultCopySpec addChild() {
        return delegateCopySpec.addChild();
    }

    @Override
    public DefaultCopySpec addFirst() {
        return delegateCopySpec.addFirst();
    }

    @Override
    public void walk(Action<? super CopySpecInternal> action) {
        action.execute(this);
        for (CopySpecInternal child : getChildren()) {
            child.walk(action);
        }
    }

    @Override
    public DefaultCopySpec addChildBeforeSpec(CopySpecInternal csi) {
        return delegateCopySpec.addChildBeforeSpec(csi);
    }

    @Override
    public CopySpec from(Object... os) {
        return delegateCopySpec.from(os);
    }

    public CopySpecEnhancement getDelegateCopySpec() {
        return delegateCopySpec;
    }

    public void setDelegateCopySpec(CopySpecEnhancement delegateCopySpec) {
        this.delegateCopySpec = delegateCopySpec;
    }

}
