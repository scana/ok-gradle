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
package me.scana.okgradle.internal.dsl.parser.android;

import me.scana.okgradle.internal.dsl.api.android.ProductFlavorModel;
import me.scana.okgradle.internal.dsl.model.android.ProductFlavorModelImpl;
import me.scana.okgradle.internal.dsl.parser.android.ProductFlavorDslElement;
import me.scana.okgradle.internal.dsl.parser.elements.GradleDslElement;
import me.scana.okgradle.internal.dsl.parser.elements.GradleDslElementMap;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class ProductFlavorsDslElement extends GradleDslElementMap {
  @NonNls public static final String PRODUCT_FLAVORS_BLOCK_NAME = "productFlavors";

  public ProductFlavorsDslElement(@NotNull GradleDslElement parent) {
    super(parent, PRODUCT_FLAVORS_BLOCK_NAME);
  }

  @Override
  public boolean isBlockElement() {
    return true;
  }

  @NotNull
  public List<ProductFlavorModel> get() {
    List<ProductFlavorModel> result = Lists.newArrayList();
    for (me.scana.okgradle.internal.dsl.parser.android.ProductFlavorDslElement dslElement : getValues(ProductFlavorDslElement.class)) {
      result.add(new ProductFlavorModelImpl(dslElement));
    }
    return result;
  }
}
