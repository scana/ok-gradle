/*
 * Copyright (C) 2016 The Android Open Source Project
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
package me.scana.okgradle.internal.dsl.model.repositories;

import me.scana.okgradle.internal.dsl.api.ext.ResolvedPropertyModel;
import me.scana.okgradle.internal.dsl.api.repositories.MavenRepositoryModel;
import me.scana.okgradle.internal.dsl.model.ext.GradlePropertyModelBuilder;
import me.scana.okgradle.internal.dsl.model.repositories.MavenCredentialsModel;
import me.scana.okgradle.internal.dsl.model.repositories.UrlBasedRepositoryModelImpl;
import me.scana.okgradle.internal.dsl.parser.elements.GradlePropertiesDslElement;
import me.scana.okgradle.internal.dsl.parser.repositories.MavenCredentialsDslElement;
import me.scana.okgradle.internal.dsl.parser.repositories.MavenRepositoryDslElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static me.scana.okgradle.internal.dsl.parser.repositories.MavenCredentialsDslElement.CREDENTIALS_BLOCK_NAME;

/**
 * Represents a repository defined with maven {}.
 */
public class MavenRepositoryModelImpl extends UrlBasedRepositoryModelImpl implements MavenRepositoryModel {
  @NotNull
  private GradlePropertiesDslElement myPropertiesDslElement;

  @NonNls private static final String ARTIFACT_URLS = "artifactUrls";

  public MavenRepositoryModelImpl(@NotNull GradlePropertiesDslElement holder, @NotNull MavenRepositoryDslElement dslElement) {
    this(holder, dslElement, "maven", "https://repo1.maven.org/maven2/");
    myPropertiesDslElement = dslElement;
  }

  protected MavenRepositoryModelImpl(@NotNull GradlePropertiesDslElement holder,
                                     @NotNull MavenRepositoryDslElement dslElement,
                                     @NotNull String defaultRepoName,
                                     @NotNull String defaultRepoUrl) {
    super(holder, dslElement, defaultRepoName, defaultRepoUrl);
    myPropertiesDslElement = dslElement;
  }

  @NotNull
  public ResolvedPropertyModel artifactUrls() {
    return GradlePropertyModelBuilder.create(myPropertiesDslElement, ARTIFACT_URLS).asMethod(true).buildResolved();
  }

  @Nullable
  public me.scana.okgradle.internal.dsl.model.repositories.MavenCredentialsModel credentials() {
    MavenCredentialsDslElement credentials = myPropertiesDslElement.getPropertyElement(CREDENTIALS_BLOCK_NAME, MavenCredentialsDslElement.class);
    return credentials != null ? new MavenCredentialsModel(credentials) : null;
  }

  @NotNull
  @Override
  public RepositoryType getType() {
    return RepositoryType.MAVEN;
  }
}
