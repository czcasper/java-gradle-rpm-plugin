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

import com.trigonic.gradle.plugins.helpers.CallableContainer;
import com.trigonic.gradle.plugins.packaging.AbstractPackagingCopyAction;
import com.trigonic.gradle.plugins.packaging.SystemPackagingTask;
import groovy.lang.Closure;
import org.freecompany.redline.header.Architecture;
import org.freecompany.redline.header.Os;
import org.freecompany.redline.header.RpmType;
import org.freecompany.redline.payload.Directive;
import org.gradle.api.internal.ConventionMapping;
import org.gradle.api.internal.IConventionAware;

public class Rpm extends SystemPackagingTask {

    static final String RPM_EXTENSION = "rpm";

    Rpm() {
        super();
        super.setExtension(RPM_EXTENSION);
    }

    @Override
    public String assembleArchiveName() {
        String name = "";
        if (parentExten != null) {
            name = parentExten.getPackageName();
            name += parentExten.getVersion();
            name += parentExten.getRelease();
            name += getArchString();
            name += super.getExtension();
        }

        return name;
    }

    @Override
    protected String getArchString() {
        Architecture arch = Architecture.NOARCH;
        if (parentExten != null) {
            arch = parentExten.getArch();
        }
        return arch.name().toLowerCase();
    }

    @Override
    public AbstractPackagingCopyAction createCopyAction() {
        return new RpmCopyAction(this);
    }

    @Override
    protected void applyConventions() {
        super.applyConventions();

        // For all mappings, we're only being called if it wasn't explicitly set on the task. In which case, we'll want
        // to pull from the parentExten. And only then would we fallback on some other value.
        ConventionMapping mapping = ((IConventionAware) this).getConventionMapping();

        // Could come from extension
        Directive value = null;
        if (parentExten != null) {
            value = parentExten.getFileType();
        }
        mapping.map("fileType", new CallableContainer<>(value));

        Boolean addDirs = true;
        if (parentExten != null) {
            addDirs = parentExten.isAddParentDirs();
        }
        mapping.map("addParentDirs", new CallableContainer<>(addDirs));

        Architecture arch = Architecture.NOARCH;
        if (parentExten != null) {
            arch = parentExten.getArch();
        }
        mapping.map("arch", new CallableContainer<>(arch));

        Os os = Os.UNKNOWN;
        if (parentExten != null) {
            os = parentExten.getOs();
        }
        mapping.map("os", new CallableContainer<>(os));

        RpmType type = RpmType.BINARY;
        if (parentExten != null) {
            type = parentExten.getType();
        }
        mapping.map("type", new CallableContainer<>(type));
    }

}
