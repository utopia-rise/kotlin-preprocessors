package versioning

import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity

abstract class GitHashValueSource : GitValueSource<String, GitHashValueSource.Params>() {
    interface Params : ValueSourceParameters {
        @get:InputDirectory
        @get:PathSensitive(PathSensitivity.RELATIVE)
        val workDir: DirectoryProperty

        @get:Input
        val abbrev: Property<Int>
    }

    override fun obtain(): String {
        val workDir = parameters.workDir.asFile.get()
        val abbrev = parameters.abbrev.orNull ?: 7

        val shortHead = runGitCommand(workDir, "rev-parse", "--short=$abbrev", "HEAD")
        if (shortHead.exitCode == 0) {
            val hash = shortHead.output.trim()
            if (hash.matches(Regex("[0-9a-fA-F]{4,40}"))) return hash
        }
        throw GradleException("Failed to obtain short git hash (exit=${shortHead.exitCode}, out=${shortHead.output.trim()})")
    }
}