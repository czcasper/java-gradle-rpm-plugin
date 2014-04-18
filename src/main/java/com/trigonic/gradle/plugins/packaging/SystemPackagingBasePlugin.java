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

import com.trigonic.gradle.plugins.deb.DebPlugin;
import com.trigonic.gradle.plugins.rpm.RpmPlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.internal.ConventionMapping;
import org.gradle.api.internal.IConventionAware;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.plugins.PluginContainer;

public class SystemPackagingBasePlugin implements Plugin<Project> {
    private static Logger logger = Logging.getLogger(SystemPackagingBasePlugin.class);

    protected Project project;
    protected ProjectPackagingExtension extension;

    public static final String taskBaseName = "ospackage";

    @Override
    public void apply(Project project) {

        this.project = project;

        // Extension is created before plugins are, so tasks
        ExtensionContainer extensions = project.getExtensions();
        extension = extensions.create(taskBaseName, ProjectPackagingExtension.class, project);
           // Ensure extension is IConventionAware
        ConventionMapping mapping = ((IConventionAware) extension).getConventionMapping();
     
//        RpmPlugin.applyAliases(extension); // RPM Specific aliases

        PluginContainer plugins = project.getPlugins();
        plugins.apply(BasePlugin.class);
        plugins.apply(RpmPlugin.class);
        plugins.apply(DebPlugin.class);
    }

 }