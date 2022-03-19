package org.hyperskill.musicplayer

import android.app.Activity
import android.view.View

abstract class AbstractUnitTest<T : Activity>(private val activityClass: Class<T>) {

    protected lateinit var activity: T

    protected fun identifier(id: String) : Int {
        return activity.resources.getIdentifier(id, "id", activity.packageName)
    }

    protected fun <T: View> find(id: Int) : T = activity.findViewById(id)

    protected fun <T: View> find(id: String) : T = activity.findViewById(identifier(id))

    protected fun <T: View> View.find(id: String): T = findViewById(identifier(id))
}