
<h1 align="center">Ok, Gradle!</h1>
<p align="center">
  <img src="https://img.shields.io/badge/version-1.0.1-blue"/>
  <img src="https://img.shields.io/github/workflow/status/scana/ok-gradle/Java%20CI"/>
</p>
<p align="center">IntelliJ/Android Studio Plugin for searching artifacts ids of popular Java libraries.</p>

---

This plugin is in JetBrains Plugins Repository! Get it from [here](https://plugins.jetbrains.com/plugin/10102-ok-gradle-).


![Take a look](images/look_and_feel.gif)

Use those for adding new dependencies easily to your build.gradle/build.gradle.kts files.

#### Retrofit - which one was it?
```
compile 'com.square.retrofit:retrofit2:2.9.0'
compile 'com.squareup.retrofit2:retrofit:2.9.0'
```

#### Android Design library - which one was it?
```
compile 'androidx.material:1.2.1'
compile 'com.google.android.material:material:1.2.1'
```

With this plugin, you won't have to remember it anymore nor open an external browser to search for it on the Internet.

Usage:

Find the **OK, Gradle** action  with `CTRL/CMD+SHIFT+A` shortcut and type library's name that you are looking for in an input field.

To make your life even more easier, assign a shortcut to it. Personally, I prefer `CMD+K` - as in **"OK!"** (I don't use commit action in the IDE anyway).

You need to have an active Internet connection to be able to use this tool.

## Would you like to automatically update your dependencies?

Take a look at [refreshVersions](https://jmfayard.github.io/refreshVersions/) project by @jmfayard

## Roadmap (take it with a grain of salt)
- local storage for collecting info about most used artifacts (for sorting purposes)
- global online db for storing information about most used artifacts (for sorting purposes)
- settings panel for tweaking things 
- ???

## Disclaimer
I want to thank <a href="https://chrisbroadfoot.id.au">Chris Broadfoot</a> for creating <a href="http://gradleplease.appspot.com">http://gradleplease.appspot.com</a> website, which was an inspiration for this plugin.

## License
```
Copyright 2021 Marcin Robaczyński "Scana"

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
