package com.trigonic.gradle.plugins.packaging;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.freecompany.redline.header.Architecture;
import org.freecompany.redline.header.Os;
import org.freecompany.redline.header.RpmType;
import org.freecompany.redline.payload.Directive;

/**
 * Extension that can be used to configure both DEB and RPM.
 *
 * Ideally we'd have multiple levels, e.g. an extension for everything, one for deb which extends the, one for rpm
 * that extends the base and have all the tasks take from the specific extension. But since tasks.withType can be used,
 * there isn't really a need for "all rpm" extension.
 */

public class SystemPackagingExtension {
    // File name components
    protected String packageName;
    protected String release;
    protected String version;

    // Metadata, some are probably specific to a type
    protected String user;
    protected String permissionGroup; // Group is used by Gradle on tasks.

    /**
     * In Debian, this is the Section and has to be provided. Valid values are: admin, cli-mono, comm, database, debug,
     * devel, doc, editors, education, electronics, embedded, fonts, games, gnome, gnu-r, gnustep, graphics, hamradio,
     * haskell, httpd, interpreters, introspection, java, kde, kernel, libdevel, libs, lisp, localization, mail, math,
     * metapackages, misc, net, news, ocaml, oldlibs, otherosfs, perl, php, python, ruby, science, shells, sound, tasks,
     * tex, text, utils, vcs, video, web, x11, xfce, zope. The section can be prefixed with contrib or non-free, if
     * not part of main.
     */
    protected String packageGroup;
    protected String buildHost;
    protected String summary;
    protected String packageDescription;
    protected String license;
    protected String packager;
    protected String distribution;
    protected String vendor;
    protected String url;
    protected String sourcePackage;
    protected String provides;

    // RPM Only
    protected Directive fileType;
    protected Boolean createDirectoryEntry;
    protected Boolean addParentDirs;
    protected Architecture arch;
    protected Os os;
    protected RpmType type;

    // DEB Only
    protected Integer uid;
    protected Integer gid;

    // Scripts
    protected List<String> preInstallCommands;
    protected List<String> postInstallCommands;
    protected List<String> preUninstallCommands;
    protected List<String> postUninstallCommands;
    protected List<String> commonCommands;

    
    protected List<Dependency> dependencies = new ArrayList<Dependency>();
    protected List<Link> links = new ArrayList<Link>();    
    /**
     * For backwards compatibility
     * @param script
     */
  /*  public SystemPackagingExtension setInstallUtils(File script) {
        return installUtils(script);
    }
*/
    public SystemPackagingExtension installUtils(String script) {
        commonCommands.add(script);
        return this;
    }

  /*  public SystemPackagingExtension installUtils(File script) {
        commonCommands.add(script);
        return this;
    }
*/
    /**
     * For backwards compatibility
     * @param script
     */
 /*   public SystemPackagingExtension setPreInstall(File script) {
        return preInstall(script);
    }
*/
    public SystemPackagingExtension preInstall(String script) {
        preInstallCommands.add(script);
        return this;
    }

 /*   public SystemPackagingExtension preInstall(File script) {
        preInstallCommands.add(script);
        return this;
    }
*/
    /**
     * For backwards compatibility
     * @param script
     */
 /*   public SystemPackagingExtension setPostInstall(File script) {
        return postInstall(script);
    }
*/
    public SystemPackagingExtension postInstall(String script) {
        postInstallCommands.add(script);
        return this;
    }

  /*  public SystemPackagingExtension postInstall(File script) {
        postInstallCommands.add(script);
        return this;
    }
*/

    /**
     * For backwards compatibility
     * @param script
     */
    /*public SystemPackagingExtension setPreUninstall(File script) {
        return preUninstall(script);
    }*/

    public SystemPackagingExtension  preUninstall(String script) {
        preUninstallCommands.add(script);
        return this;
    }

/*    public SystemPackagingExtension preUninstall(File script) {
        preUninstallCommands.add(script);
        return this;
    }*/

    /**
     * For backwards compatibility
     * @param script
     */
    /*public SystemPackagingExtension setPostUninstall(File script) {
        return preUninstall(script);
    }*/

    public SystemPackagingExtension postUninstall(String script) {
        postUninstallCommands.add(script);
        return this;
    }

    /*public SystemPackagingExtension postUninstall(File script) {
        postUninstallCommands.add(script);
        return this;
    }*/


    public Link link(String path, String target) {
        return link(path, target, -1);
    }

    public Link link(String path, String target, int permissions) {
        Link link = new Link(path, target, permissions);
        links.add(link);
        return link;
    }


    public Dependency requires(String packageName, String version, int flag) {
        Dependency dep = new Dependency(packageName, version, flag);
        dependencies.add(dep);
        return dep;
    }

    public Dependency requires(String packageName) {
        return requires(packageName, "", 0);
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPermissionGroup() {
        return permissionGroup;
    }

    public void setPermissionGroup(String permissionGroup) {
        this.permissionGroup = permissionGroup;
    }

    public String getPackageGroup() {
        return packageGroup;
    }

    public void setPackageGroup(String packageGroup) {
        this.packageGroup = packageGroup;
    }

    public String getBuildHost() {
        return buildHost;
    }

    public void setBuildHost(String buildHost) {
        this.buildHost = buildHost;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPackageDescription() {
        return packageDescription;
    }

    public void setPackageDescription(String packageDescription) {
        this.packageDescription = packageDescription;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getPackager() {
        return packager;
    }

    public void setPackager(String packager) {
        this.packager = packager;
    }

    public String getDistribution() {
        return distribution;
    }

    public void setDistribution(String distribution) {
        this.distribution = distribution;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSourcePackage() {
        return sourcePackage;
    }

    public void setSourcePackage(String sourcePackage) {
        this.sourcePackage = sourcePackage;
    }

    public String getProvides() {
        return provides;
    }

    public void setProvides(String provides) {
        this.provides = provides;
    }

    public Directive getFileType() {
        return fileType;
    }

    public void setFileType(Directive fileType) {
        this.fileType = fileType;
    }

    public Boolean isCreateDirectoryEntry() {
        return createDirectoryEntry;
    }

    public void setCreateDirectoryEntry(Boolean createDirectoryEntry) {
        this.createDirectoryEntry = createDirectoryEntry;
    }

    public Boolean isAddParentDirs() {
        return addParentDirs;
    }

    public void setAddParentDirs(Boolean addParentDirs) {
        this.addParentDirs = addParentDirs;
    }

    public Architecture getArch() {
        return arch;
    }

    public void setArch(Architecture arch) {
        this.arch = arch;
    }

    public Os getOs() {
        return os;
    }

    public void setOs(Os os) {
        this.os = os;
    }

    public RpmType getType() {
        return type;
    }

    public void setType(RpmType type) {
        this.type = type;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getGid() {
        return gid;
    }

    public void setGid(Integer gid) {
        this.gid = gid;
    }

    public List<String> getPreInstallCommands() {
        return preInstallCommands;
    }

    public void setPreInstallCommands(List<String> preInstallCommands) {
        this.preInstallCommands = preInstallCommands;
    }

    public List<String> getPostInstallCommands() {
        return postInstallCommands;
    }

    public void setPostInstallCommands(List<String> postInstallCommands) {
        this.postInstallCommands = postInstallCommands;
    }

    public List<String> getPreUninstallCommands() {
        return preUninstallCommands;
    }

    public void setPreUninstallCommands(List<String> preUninstallCommands) {
        this.preUninstallCommands = preUninstallCommands;
    }

    public List<String> getPostUninstallCommands() {
        return postUninstallCommands;
    }

    public void setPostUninstallCommands(List<String> postUninstallCommands) {
        this.postUninstallCommands = postUninstallCommands;
    }

    public List<String> getCommonCommands() {
        return commonCommands;
    }

    public void setCommonCommands(List<String> commonCommands) {
        this.commonCommands = commonCommands;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

}
