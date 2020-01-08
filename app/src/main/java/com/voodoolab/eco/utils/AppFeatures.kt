package com.voodoolab.eco.utils

import android.animation.Animator
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import me.zhanghai.android.materialprogressbar.MaterialProgressBar

fun MaterialProgressBar.show(visible: Boolean) {
    if (visible) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.GONE
    }
}

fun LinearLayout.fadeOutAnimation() {
    this.let {animatedView ->
        animatedView.alpha = 1f
        val animation = animatedView.animate()
        animation.duration = 350
        animation.interpolator = LinearInterpolator()
        animation.alpha(0f)
        animation.setListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                animatedView.visibility = View.INVISIBLE
                animatedView.alpha = 0f

            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {

            }
        })
        animation.start()
    }
}

fun View.fadeOutAnimation() {
    this.let { animatedView ->
        animatedView.alpha = 1f
        val animation = animatedView.animate()
        animation.duration = 350
        animation.interpolator = LinearInterpolator()
        animation.alpha(0f)
        animation.setListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                animatedView.visibility = View.INVISIBLE
                animatedView.alpha = 0f

            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {

            }
        })
        animation.start()
    }
}

