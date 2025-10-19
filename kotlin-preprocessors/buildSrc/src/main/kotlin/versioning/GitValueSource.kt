package versioning

import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import java.io.File

abstract class GitValueSource<T, P : ValueSourceParameters> : ValueSource<T, P> {
    protected fun runGitCommand(workingDir: File, vararg args: String): Result {
        return try {
            val pb = ProcessBuilder(listOf("git") + args.toList())
            pb.directory(workingDir)
            pb.redirectErrorStream(true)
            val proc = pb.start()
            val output = proc.inputStream.bufferedReader().readText()
            val exit = proc.waitFor()
            Result(exit, output)
        } catch (e: Exception) {
            Result(1, e.message ?: e::class.java.name)
        }
    }

    protected data class Result(val exitCode: Int, val output: String)
}