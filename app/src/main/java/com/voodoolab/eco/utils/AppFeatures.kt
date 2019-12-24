package com.voodoolab.eco.utils

import android.view.View
import me.zhanghai.android.materialprogressbar.MaterialProgressBar

fun MaterialProgressBar.show(visible: Boolean) {
    if (visible) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.GONE
    }
}

