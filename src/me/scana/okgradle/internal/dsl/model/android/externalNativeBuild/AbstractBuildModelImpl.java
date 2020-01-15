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
package me.scana.okgradle.internal.dsl.model.android.externalNativeBuild;

import me.scana.okgradle.internal.dsl.api.android.externalNativeBuild.AbstractBuildModel;
import me.scana.okgradle.internal.dsl.api.ext.ResolvedPropertyModel;
import me.scana.okgradle.internal.dsl.model.GradleDslBlockModel;
import me.scana.okgradle.internal.dsl.model.android.externalNativeBuild.CMakeModelImpl;
import me.scana.okgradle.internal.dsl.model.android.externalNativeBuild.NdkBuildModelImpl;
import me.scana.okgradle.internal.dsl.model.ext.GradlePropertyModelBuilder;
import me.scana.okgradle.internal.dsl.model.ext.PropertyUtil;
import me.scana.okgradle.internal.dsl.parser.elements.GradlePropertiesDslElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;


/**
 * Base class for the external native build models like {@link CMakeModelImpl} and {@link NdkBuildModelImpl}.
 */
public abstract class AbstractBuildModelImpl extends GradleDslBlockModel implements AbstractBuildModel {
  @NonNls private static final String PATH = "path";

  protected AbstractBuildModelImpl(@NotNull GradlePropertiesDslElement dslElement) {
    super(dslElement);
  }

  @Override
  @NotNull
  public ResolvedPropertyModel path() {
    return GradlePropertyModelBuilder.create(myDslElement, PATH).asMethod(true).addTransform(PropertyUtil.FILE_TRANSFORM).buildResolved();
  }
}
