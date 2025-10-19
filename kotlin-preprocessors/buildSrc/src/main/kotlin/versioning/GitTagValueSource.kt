package versioning

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity

abstract class GitTagValueSource : GitValueSource<String, GitTagValueSource.Params>() {
    interface Params : ValueSourceParameters {
        @get:InputDirectory
        @get:PathSensitive(PathSensitivity.RELATIVE)
        val workDir: DirectoryProperty
    }

    override fun obtain(): String {
        val workDir = parameters.workDir.asFile.get()

        val exactTag = runGitCommand(workDir, "describe", "--tags", "--exact-match")
        return if (exactTag.exitCode == 0) {
            exactTag.output.trim()
        } else {
            ""
        }
    }
}