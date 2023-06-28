# kotlin-preprocessors
Gradle plugin to define preprocessors for kotlin language

This plugins enable to define kotlin constants from gradle.  
As constants are inlined by compiler, and checks based on constants results in NOP after compiling,
it makes constants good candidates for "conditional compiling".  

## How to use

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
    definitionsObjectPrefix.set("WhateverNameYouWant") // Here to avoid overlap if a dependency uses this plugin. You can set this to your project name

    define("DEBUG") // if no value specified, default is bool with value set to true.
    define("APP_VERSION", "1.0.0")
}
```
