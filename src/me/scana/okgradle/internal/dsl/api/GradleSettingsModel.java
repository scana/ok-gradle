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
import me.scana.okgradle.internal.dsl.api.GradleFileModel;
import me.scana.okgradle.internal.dsl.api.GradleModelProvider;
import me.scana.okgradle.internal.dsl.api.ProjectBuildModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import org.jetbrains.plugins.gradle.settings.GradleProjectSettings;

public interface GradleSettingsModel extends GradleFileModel {
  /**
   * @deprecated Use {@link ProjectBuildModel#getProjectSettingsModel()} instead.
   */
  @Deprecated
  @Nullable
  static GradleSettingsModel get(@NotNull Project project) {
    return GradleModelProvider.get().getSettingsModel(project);
  }

  @NotNull
  List<String> modulePaths();

  void addModulePath(@NotNull String modulePath);

  void removeModulePath(@NotNull String modulePath);

  void replaceModulePath(@NotNull String oldModulePath, @NotNull String newModulePath);

  @Nullable
  File moduleDirectory(String modulePath);

  @Nullable
  String moduleWithDirectory(@NotNull File moduleDir);

  @Nullable
  me.scana.okgradle.internal.dsl.api.GradleBuildModel moduleModel(@NotNull String modulePath);

  @Nullable
  String parentModule(@NotNull String modulePath);

  @Nullable
  GradleBuildModel getParentModuleModel(@NotNull String modulePath);

  @Nullable
  File buildFile(@NotNull String modulePath);

  /**
   * If models are available you might want to use {@link GradleProjectSettings#getCompositeBuild()} instead.
   *
   * @return files representing the root folders of the included builds
   */
  @NotNull
  List<VirtualFile> includedBuilds();
}
