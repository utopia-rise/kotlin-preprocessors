# kotlin-preprocessors
Gradle plugin to define preprocessors for kotlin language

This plugin enable to define kotlin constants from gradle.  
As constants are inlined by compiler, and checks based on constants results in NOP after compiling,
it makes constants good candidates for "conditional compiling".  

## How to use ?

In your gradle build script, add this plugin:  
```kotlin
plugins {
    kotlin("jvm") version "1.8.20"
    
    id("com.utopia-rise.kotlin-preprocessors")
}
```

Then you can add definitions to your project:
```kotlin
kotlinDefinitions {
    definitionsObjectName.set("WhateverNameYouWant") // Here to avoid overlap if a dependency uses this plugin. BuildConfig by default

    define("DEBUG") // if no value specified, default is bool with value set to true.
    define("APP_VERSION", "1.0.0")
}
```

This will generate a sourceset containing an object with your definitions in `build` directory.  
Example for sample project in this repo:  
```kotlin
package kotlincompile.definitions

import kotlin.Boolean

public object SamplesBuildConfig {
    public const val DEBUG: Boolean = true
}
```

## How does this work ?

This plugin's process is simple.  
It takes all your definitions, check type is primitive or string, and then uses
[kotlin poet](https://github.com/square/kotlinpoet) to generate source that is added to source sets.  
Generation task is set as a dependency for `compileKotlin` task, and to intellij's project sync.
