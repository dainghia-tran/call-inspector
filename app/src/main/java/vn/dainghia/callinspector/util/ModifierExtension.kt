package vn.dainghia.callinspector.util

import androidx.compose.ui.Modifier

inline fun Modifier.applyIf(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier =
    if (condition) {
        then(modifier())
    } else {
        this
    }