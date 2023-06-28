import kotlincompile.definitions.SamplesDefinitions

fun main(args: Array<String>) {
    if (SamplesDefinitions.DEBUG) {
        println("DEBUG!")
    } else {
        println("not DEBUG!")
    }
}

fun isDebug(): Boolean {
    return SamplesDefinitions.DEBUG
}