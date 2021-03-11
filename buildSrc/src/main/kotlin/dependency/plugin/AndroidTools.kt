package dependency.plugin

import dependency.Library

object AndroidTools : Library {

    override val group = "com.android.tools.build"
    override val artifact = "gradle"
    override val version = "4.0.1"
}
