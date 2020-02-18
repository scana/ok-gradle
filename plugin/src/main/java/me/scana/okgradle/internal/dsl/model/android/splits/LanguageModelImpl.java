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
package me.scana.okgradle.internal.dsl.model.android.splits;

import me.scana.okgradle.internal.dsl.api.android.splits.LanguageModel;
import me.scana.okgradle.internal.dsl.api.ext.ResolvedPropertyModel;
import me.scana.okgradle.internal.dsl.model.GradleDslBlockModel;
import me.scana.okgradle.internal.dsl.model.ext.GradlePropertyModelBuilder;
import me.scana.okgradle.internal.dsl.parser.android.splits.LanguageDslElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class LanguageModelImpl extends GradleDslBlockModel implements LanguageModel {
  @NonNls private static final String ENABLE = "enable";
  @NonNls private static final String INCLUDE = "include";

  public LanguageModelImpl(@NotNull LanguageDslElement dslElement) {
    super(dslElement);
  }

  @Override
  @NotNull
  public ResolvedPropertyModel enable() {
    return GradlePropertyModelBuilder.create(myDslElement, ENABLE).asMethod(true).buildResolved();
  }

  @Override
  @NotNull
  public ResolvedPropertyModel include() {
    return GradlePropertyModelBuilder.create(myDslElement, INCLUDE).asMethod(true).buildResolved();
  }
}
