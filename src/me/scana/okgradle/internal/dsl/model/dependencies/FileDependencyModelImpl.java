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
package me.scana.okgradle.internal.dsl.model.dependencies;

import static me.scana.okgradle.internal.dsl.api.ext.PropertyType.REGULAR;

import me.scana.okgradle.internal.dsl.api.dependencies.FileDependencyModel;
import me.scana.okgradle.internal.dsl.api.ext.ResolvedPropertyModel;
import me.scana.okgradle.internal.dsl.model.dependencies.DependenciesModelImpl;
import me.scana.okgradle.internal.dsl.model.dependencies.DependencyModelImpl;
import me.scana.okgradle.internal.dsl.model.ext.GradlePropertyModelBuilder;
import me.scana.okgradle.internal.dsl.parser.elements.GradleDslElement;
import me.scana.okgradle.internal.dsl.parser.elements.GradleDslExpression;
import me.scana.okgradle.internal.dsl.parser.elements.GradleDslLiteral;
import me.scana.okgradle.internal.dsl.parser.elements.GradleDslMethodCall;
import me.scana.okgradle.internal.dsl.parser.elements.GradleDslSimpleExpression;
import me.scana.okgradle.internal.dsl.parser.elements.GradleNameElement;
import me.scana.okgradle.internal.dsl.parser.elements.GradlePropertiesDslElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class FileDependencyModelImpl extends DependencyModelImpl implements FileDependencyModel {
  @NonNls public static final String FILES = "files";

  @NotNull private GradleDslSimpleExpression myFileDslExpression;

  static Collection<FileDependencyModel> create(@NotNull String configurationName,
                                                @NotNull GradleDslMethodCall methodCall,
                                                @NotNull Maintainer maintainer) {
    List<FileDependencyModel> result = new ArrayList<>();
    Maintainer argumentMaintainer;
    if (maintainer == me.scana.okgradle.internal.dsl.model.dependencies.DependenciesModelImpl.Maintainers.SINGLE_ITEM_MAINTAINER) {
      argumentMaintainer = me.scana.okgradle.internal.dsl.model.dependencies.DependenciesModelImpl.Maintainers.DEEP_SINGLE_ITEM_MAINTAINER;
    }
    else if (maintainer == me.scana.okgradle.internal.dsl.model.dependencies.DependenciesModelImpl.Maintainers.ARGUMENT_LIST_MAINTAINER) {
      argumentMaintainer = me.scana.okgradle.internal.dsl.model.dependencies.DependenciesModelImpl.Maintainers.DEEP_ARGUMENT_LIST_MAINTAINER;
    }
    else if (maintainer == me.scana.okgradle.internal.dsl.model.dependencies.DependenciesModelImpl.Maintainers.EXPRESSION_LIST_MAINTAINER) {
      argumentMaintainer = DependenciesModelImpl.Maintainers.DEEP_EXPRESSION_LIST_MAINTAINER;
    }
    else {
      throw new IllegalStateException();
    }
    if (FILES.equals(methodCall.getMethodName())) {
      List<GradleDslExpression> arguments = methodCall.getArguments();
      for (GradleDslElement argument : arguments) {
        if (argument instanceof GradleDslSimpleExpression) {
          result.add(new FileDependencyModelImpl(configurationName, (GradleDslSimpleExpression)argument, argumentMaintainer));
        }
      }
    }
    return result;
  }

  static void createNew(@NotNull GradlePropertiesDslElement parent,
                        @NotNull String configurationName,
                        @NotNull String file) {
    GradleNameElement name = GradleNameElement.create(configurationName);
    GradleDslMethodCall methodCall = new GradleDslMethodCall(parent, name, FILES);
    GradleDslLiteral fileDslLiteral = new GradleDslLiteral(methodCall, name);
    fileDslLiteral.setElementType(REGULAR);
    fileDslLiteral.setValue(file);
    methodCall.addNewArgument(fileDslLiteral);
    parent.setNewElement(methodCall);
  }

  private FileDependencyModelImpl(@NotNull String configurationName,
                                  @NotNull GradleDslSimpleExpression fileDslExpression,
                                  @NotNull Maintainer maintainer) {
    super(configurationName, maintainer);
    myFileDslExpression = fileDslExpression;
  }

  @Override
  @NotNull
  protected GradleDslElement getDslElement() {
    return myFileDslExpression;
  }

  @Override
  void setDslElement(@NotNull GradleDslElement dslElement) {
    myFileDslExpression = (GradleDslSimpleExpression)dslElement;
  }

  @Override
  @NotNull
  public ResolvedPropertyModel file() {
    return GradlePropertyModelBuilder.create(myFileDslExpression).asMethod(true).buildResolved();
  }
}
