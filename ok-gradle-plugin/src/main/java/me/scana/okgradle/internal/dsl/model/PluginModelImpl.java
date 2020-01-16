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
package me.scana.okgradle.internal.dsl.model;

import me.scana.okgradle.internal.dsl.api.PluginModel;
import me.scana.okgradle.internal.dsl.api.ext.ResolvedPropertyModel;
import me.scana.okgradle.internal.dsl.model.ext.GradlePropertyModelBuilder;
import me.scana.okgradle.internal.dsl.model.ext.PropertyUtil;
import me.scana.okgradle.internal.dsl.parser.apply.ApplyDslElement;
import me.scana.okgradle.internal.dsl.parser.elements.GradleDslElement;
import me.scana.okgradle.internal.dsl.parser.elements.GradleDslExpressionList;
import me.scana.okgradle.internal.dsl.parser.elements.GradleDslExpressionMap;
import me.scana.okgradle.internal.dsl.parser.elements.GradleDslSimpleExpression;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static me.scana.okgradle.internal.dsl.api.ext.GradlePropertyModel.ValueType.STRING;

public class PluginModelImpl implements PluginModel {
  @NonNls private static final String PLUGIN = "plugin";

  @NotNull
  private final GradleDslElement myCompleteElement;
  @NotNull
  private final GradleDslSimpleExpression myDslElement;

  @NotNull
  public static List<PluginModelImpl> create(@NotNull ApplyDslElement dslElement) {
    List<GradleDslElement> elements = dslElement.getAllPropertyElements();
    List<PluginModelImpl> results = new ArrayList<>();

    for (GradleDslElement e : elements) {
      if (e instanceof GradleDslSimpleExpression) {
        results.add(new PluginModelImpl(e, (GradleDslSimpleExpression)e));
      }
      else if (e instanceof GradleDslExpressionMap) {
        GradleDslElement element = ((GradleDslExpressionMap)e).getElement(PLUGIN);
        if (element instanceof GradleDslSimpleExpression) {
          results.add(new PluginModelImpl(e, (GradleDslSimpleExpression)element));
        }
      }
      else if (e instanceof GradleDslExpressionList) {
        GradleDslExpressionList element = (GradleDslExpressionList)e;
        for (GradleDslSimpleExpression item : element.getSimpleExpressions()) {
          results.add(new PluginModelImpl(item, item));
        }
      }
    }

    return results;
  }

  public static Map<String, PluginModel> deduplicatePlugins(@NotNull List<PluginModelImpl> models) {
    Map<String, PluginModel> modelMap = new LinkedHashMap<>();
    for (PluginModel model : models) {
      ResolvedPropertyModel propertyModel = model.name();
      if (propertyModel.getValueType() == STRING) {
        modelMap.put(propertyModel.forceString(), model);
      }
    }
    return modelMap;
  }

  public static void removePlugins(@NotNull List<PluginModelImpl> models, @NotNull String name) {
    for (PluginModelImpl model : models) {
      if (name.equals(model.name().toString())) {
        model.remove();
      }
    }
  }

  public PluginModelImpl(@NotNull GradleDslElement completeElement, @NotNull GradleDslSimpleExpression element) {
    myDslElement = element;
    myCompleteElement = completeElement;
  }

  @NotNull
  @Override
  public ResolvedPropertyModel name() {
    return GradlePropertyModelBuilder.create(myDslElement).asMethod(true).buildResolved();
  }

  public void remove() {
    PropertyUtil.removeElement(myCompleteElement);
  }
}
