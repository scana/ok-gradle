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
import me.scana.okgradle.internal.dsl.model.ext.GradlePropertyModelBuilder;
import me.scana.okgradle.internal.dsl.parser.repositories.MavenCredentialsDslElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class MavenCredentialsModel {
  @NonNls private static final String USERNAME = "username";
  @NonNls private static final String PASSWORD = "password";

  @NotNull private final MavenCredentialsDslElement myDslElement;

  public MavenCredentialsModel(@NotNull MavenCredentialsDslElement dslElement) {
    myDslElement = dslElement;
  }

  @NotNull
  public ResolvedPropertyModel username() {
    return GradlePropertyModelBuilder.create(myDslElement, USERNAME).asMethod(true).buildResolved();
  }

  @NotNull
  public ResolvedPropertyModel password() {
    return GradlePropertyModelBuilder.create(myDslElement, PASSWORD).asMethod(true).buildResolved();
  }
}
