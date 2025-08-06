import kotlincompile.definitions.SamplesBuildConfig

fun main(args: Array<String>) {
    if (SamplesBuildConfig.DEBUG) {
        println("DEBUG!")
    } else {
        println("not DEBUG!")
    }
}

fun isDebug(): Boolean {
    return SamplesBuildConfig.DEBUG
}