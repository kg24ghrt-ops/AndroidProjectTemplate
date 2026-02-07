import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings

class DependenciesPlugin : Plugin<Any> {
    override fun apply(target: Any) {
        when (target) {
            is Settings -> { /* Settings logic if needed */ }
            is Project -> { /* Project logic if needed */ }
        }
    }
}
