package me.scana.okgradle.internal.dsl.api.android;

import me.scana.okgradle.internal.dsl.api.ext.ResolvedPropertyModel;
import me.scana.okgradle.internal.dsl.api.util.GradleDslModel;
import org.jetbrains.annotations.NotNull;

public interface DexOptionsModel extends GradleDslModel {
  @NotNull
  ResolvedPropertyModel additionalParameters();

  @NotNull
  ResolvedPropertyModel javaMaxHeapSize();

  @NotNull
  ResolvedPropertyModel jumboMode();

  @NotNull
  ResolvedPropertyModel keepRuntimeAnnotatedClasses();

  @NotNull
  ResolvedPropertyModel maxProcessCount();

  @NotNull
  ResolvedPropertyModel optimize();

  @NotNull
  ResolvedPropertyModel preDexLibraries();

  @NotNull
  ResolvedPropertyModel threadCount();
}
