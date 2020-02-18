/*
 * Copyright (C) 2018 The Android Open Source Project
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
package me.scana.okgradle.internal.dsl.model.java;

import me.scana.okgradle.internal.dsl.api.java.LanguageLevelPropertyModel;
import me.scana.okgradle.internal.dsl.api.util.LanguageLevelUtil;
import me.scana.okgradle.internal.dsl.model.ext.GradlePropertyModelImpl;
import me.scana.okgradle.internal.dsl.model.ext.ResolvedPropertyModelImpl;
import com.intellij.pom.java.LanguageLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import static me.scana.okgradle.internal.dsl.api.util.LanguageLevelUtil.convertToGradleString;
import static me.scana.okgradle.internal.dsl.api.util.LanguageLevelUtil.parseFromGradleString;

public class LanguageLevelPropertyModelImpl extends ResolvedPropertyModelImpl implements LanguageLevelPropertyModel {
  public LanguageLevelPropertyModelImpl(@NotNull GradlePropertyModelImpl realModel) {
    super(realModel);
  }

  @TestOnly
  @Override
  @Nullable
  public LanguageLevel toLanguageLevel() {
    String stringToParse = LanguageLevelUtil.getStringToParse(this);
    return stringToParse == null ? null : parseFromGradleString(stringToParse);
  }

  @Override
  public void setLanguageLevel(@NotNull LanguageLevel level) {
    setValue(convertToGradleString(level, LanguageLevelUtil.getStringToParse(this)));
  }
}
