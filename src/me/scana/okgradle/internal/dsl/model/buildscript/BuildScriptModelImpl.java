/*
 * Copyright (C) 2015 The Android Open Source Project
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
package me.scana.okgradle.internal.dsl.model.buildscript;

import me.scana.okgradle.internal.dsl.api.BuildScriptModel;
import me.scana.okgradle.internal.dsl.api.dependencies.DependenciesModel;
import me.scana.okgradle.internal.dsl.api.ext.ExtModel;
import me.scana.okgradle.internal.dsl.api.ext.GradlePropertyModel;
import me.scana.okgradle.internal.dsl.api.repositories.RepositoriesModel;
import me.scana.okgradle.internal.dsl.model.GradleDslBlockModel;
import me.scana.okgradle.internal.dsl.model.dependencies.DependenciesModelImpl;
import me.scana.okgradle.internal.dsl.model.ext.ExtModelImpl;
import me.scana.okgradle.internal.dsl.model.repositories.RepositoriesModelImpl;
import me.scana.okgradle.internal.dsl.parser.apply.ApplyDslElement;
import me.scana.okgradle.internal.dsl.parser.buildscript.BuildScriptDslElement;
import me.scana.okgradle.internal.dsl.parser.dependencies.DependenciesDslElement;
import me.scana.okgradle.internal.dsl.parser.elements.GradleDslElement;
import me.scana.okgradle.internal.dsl.parser.ext.ExtDslElement;
import me.scana.okgradle.internal.dsl.parser.repositories.RepositoriesDslElement;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import static me.scana.okgradle.internal.dsl.parser.dependencies.DependenciesDslElement.DEPENDENCIES_BLOCK_NAME;
import static me.scana.okgradle.internal.dsl.parser.ext.ExtDslElement.EXT_BLOCK_NAME;
import static me.scana.okgradle.internal.dsl.parser.repositories.RepositoriesDslElement.REPOSITORIES_BLOCK_NAME;

public class BuildScriptModelImpl extends GradleDslBlockModel implements BuildScriptModel {

  public BuildScriptModelImpl(@NotNull BuildScriptDslElement dslElement) {
    super(dslElement);
  }

  @NotNull
  @Override
  public DependenciesModel dependencies() {
    DependenciesDslElement dependenciesDslElement = myDslElement.getPropertyElement(DEPENDENCIES_BLOCK_NAME, DependenciesDslElement.class);
    if (dependenciesDslElement == null) {
      dependenciesDslElement = new DependenciesDslElement(myDslElement);
      myDslElement.setNewElement(dependenciesDslElement);
    }
    return new DependenciesModelImpl(dependenciesDslElement);
  }

  @NotNull
  @Override
  public RepositoriesModel repositories() {
    RepositoriesDslElement repositoriesDslElement = myDslElement.getPropertyElement(REPOSITORIES_BLOCK_NAME, RepositoriesDslElement.class);
    if (repositoriesDslElement == null) {
      repositoriesDslElement = new RepositoriesDslElement(myDslElement);
      myDslElement.setNewElement(repositoriesDslElement);
    }
    return new RepositoriesModelImpl(repositoriesDslElement);
  }

  /**
   * Removes property {@link RepositoriesDslElement#REPOSITORIES_BLOCK_NAME}.
   */
  @Override
  @TestOnly
  public void removeRepositoriesBlocks() {
    myDslElement.removeProperty(REPOSITORIES_BLOCK_NAME);
  }

  @NotNull
  @Override
  public ExtModel ext() {
    ExtDslElement extDslElement = myDslElement.getPropertyElement(EXT_BLOCK_NAME, ExtDslElement.class);
    if (extDslElement == null) {
      extDslElement = new ExtDslElement(myDslElement);
      List<GradleDslElement> elements = myDslElement.getAllElements();
      int index = (!elements.isEmpty() && elements.get(0) instanceof ApplyDslElement) ? 1 : 0;
      myDslElement.addNewElementAt(index, extDslElement);
    }
    return new ExtModelImpl(extDslElement);
  }

  @NotNull
  @Override
  public Map<String, GradlePropertyModel> getInScopeProperties() {
    return super.getDeclaredProperties().stream().collect(Collectors.toMap(e -> e.getName(), e -> e));
  }
}
