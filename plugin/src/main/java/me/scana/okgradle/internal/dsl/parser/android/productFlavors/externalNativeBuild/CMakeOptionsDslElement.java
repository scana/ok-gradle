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
package me.scana.okgradle.internal.dsl.parser.android.productFlavors.externalNativeBuild;

import me.scana.okgradle.internal.dsl.parser.android.productFlavors.externalNativeBuild.AbstractBuildOptionsDslElement;
import me.scana.okgradle.internal.dsl.parser.elements.GradleDslElement;
import me.scana.okgradle.internal.dsl.parser.elements.GradleNameElement;
import org.jetbrains.annotations.NotNull;

import static me.scana.okgradle.internal.dsl.parser.android.externalNativeBuild.CMakeDslElement.CMAKE_BLOCK_NAME;

public final class CMakeOptionsDslElement extends AbstractBuildOptionsDslElement {
  public CMakeOptionsDslElement(@NotNull GradleDslElement parent) {
    super(parent, GradleNameElement.create(CMAKE_BLOCK_NAME));
  }
}
