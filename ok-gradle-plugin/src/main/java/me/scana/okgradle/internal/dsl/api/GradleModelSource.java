/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.scana.okgradle.internal.dsl.api;

import me.scana.okgradle.internal.dsl.api.GradleBuildModel;
import me.scana.okgradle.internal.dsl.api.GradleModelProvider;
import me.scana.okgradle.internal.dsl.api.GradleSettingsModel;
import me.scana.okgradle.internal.dsl.api.ProjectBuildModel;
import me.scana.okgradle.internal.dsl.api.dependencies.ArtifactDependencyModel;
import me.scana.okgradle.internal.dsl.api.dependencies.ArtifactDependencySpec;
import me.scana.okgradle.internal.dsl.model.GradleBuildModelImpl;
import me.scana.okgradle.internal.dsl.model.GradleSettingsModelImpl;
import me.scana.okgradle.internal.dsl.model.ProjectBuildModelImpl;
import me.scana.okgradle.internal.dsl.model.dependencies.ArtifactDependencySpecImpl;
import com.android.tools.idea.projectsystem.GoogleMavenArtifactId;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GradleModelSource extends GradleModelProvider {

  @NotNull
  @Override
  public ProjectBuildModel getProjectModel(@NotNull Project project) {
    return ProjectBuildModelImpl.get(project);
  }

  @Override
  @Nullable
  public ProjectBuildModel getProjectModel(@NotNull Project hostProject, @NotNull String compositeRoot) {
    return ProjectBuildModelImpl.get(hostProject, compositeRoot);
  }

  @Nullable
  @Override
  public me.scana.okgradle.internal.dsl.api.GradleBuildModel getBuildModel(@NotNull Project project) {
    return GradleBuildModelImpl.get(project);
  }

  @Nullable
  @Override
  public me.scana.okgradle.internal.dsl.api.GradleBuildModel getBuildModel(@NotNull Module module) {
    return GradleBuildModelImpl.get(module);
  }

  @NotNull
  @Override
  public me.scana.okgradle.internal.dsl.api.GradleBuildModel parseBuildFile(@NotNull VirtualFile file, @NotNull Project project) {
    return GradleBuildModelImpl.parseBuildFile(file, project);
  }

  @NotNull
  @Override
  public GradleBuildModel parseBuildFile(@NotNull VirtualFile file, @NotNull Project project, @NotNull String moduleName) {
    return GradleBuildModelImpl.parseBuildFile(file, project, moduleName);
  }

  @Nullable
  @Override
  public GradleSettingsModel getSettingsModel(@NotNull Project project) {
    return GradleSettingsModelImpl.get(project);
  }

  @NotNull
  @Override
  public ArtifactDependencySpec getArtifactDependencySpec(@NotNull String name, @Nullable String group, @Nullable String version) {
    return new ArtifactDependencySpecImpl(name, group, version);
  }

  @NotNull
  @Override
  public ArtifactDependencySpec getArtifactDependencySpec(@NotNull String name,
                                                          @Nullable String group,
                                                          @Nullable String version,
                                                          @Nullable String classifier,
                                                          @Nullable String extension) {
    return new ArtifactDependencySpecImpl(name, group, version, classifier, extension);
  }

  @NotNull
  @Override
  public ArtifactDependencySpec getArtifactDependencySpec(@NotNull ArtifactDependencyModel dependency) {
    return ArtifactDependencySpecImpl.create(dependency);
  }

  @Nullable
  @Override
  public ArtifactDependencySpec getArtifactDependencySpec(@NotNull String notation) {
    return ArtifactDependencySpecImpl.create(notation);
  }

  @NotNull
  @Override
  public ArtifactDependencySpec getArtifactDependencySpec(@NotNull GoogleMavenArtifactId artifactId, @Nullable String version) {
    return new ArtifactDependencySpecImpl(artifactId, version);
  }
}
