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
package me.scana.okgradle.internal.dsl.parser.android;

import me.scana.okgradle.internal.dsl.api.android.SourceSetModel;
import me.scana.okgradle.internal.dsl.model.android.SourceSetModelImpl;
import me.scana.okgradle.internal.dsl.parser.android.SourceSetDslElement;
import me.scana.okgradle.internal.dsl.parser.elements.GradleDslElement;
import me.scana.okgradle.internal.dsl.parser.elements.GradleDslElementMap;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class SourceSetsDslElement extends GradleDslElementMap {
  @NonNls public static final String SOURCE_SETS_BLOCK_NAME = "sourceSets";

  public SourceSetsDslElement(@NotNull GradleDslElement parent) {
    super(parent, SOURCE_SETS_BLOCK_NAME);
  }

  @Override
  public boolean isBlockElement() {
    return true;
  }

  @NotNull
  public List<SourceSetModel> get() {
    List<SourceSetModel> result = new ArrayList<>();
    for (me.scana.okgradle.internal.dsl.parser.android.SourceSetDslElement dslElement : getValues(SourceSetDslElement.class)) {
      result.add(new SourceSetModelImpl(dslElement));
    }
    return result;
  }
}
