package com.voodoolab.eco.utils

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.voodoolab.eco.models.WashModel
import com.voodoolab.eco.utils.Constants.NOTIFICATION_VALUE_OF_TRANSACTION
import me.zhanghai.android.materialprogressbar.MaterialProgressBar
import java.text.ParseException

fun MaterialProgressBar.show(visible: Boolean) {
    if (visible) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.GONE
    }
}

fun LinearLayout.fadeOutAnimation() {
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

fun Intent.hasFullInformationForReport(): Boolean { // if we have all this data that is all
    if (hasExtra(Constants.NOTIFICATION_OPERATION_ID) && hasExtra(Constants.NOTIFICATION_TYPE) && hasExtra(
            Constants.NOTIFICATION_WASH_MODEL
        ) && hasExtra(NOTIFICATION_VALUE_OF_TRANSACTION)
    ) {
        val washString = extras?.getString(Constants.NOTIFICATION_WASH_MODEL)
        return try {

            val washJson = JsonParser().parse(washString) as JsonObject
            // get data for washModel
            washJson.apply {
                get("address").asString
                get("city").asString
                get("cashback").asInt
                get("id").asInt
                get("seats").asInt
            }
            true
        } catch (error: ParseException) {
            error.printStackTrace()
            false
        }

    } else {
        return false
    }
}

fun Intent.hasFullInfoFromBroadCast(): Boolean {
    if (hasExtra(Constants.NOTIFICATION_OPERATION_ID) && hasExtra(Constants.NOTIFICATION_WASH_MODEL
        ) && hasExtra(NOTIFICATION_VALUE_OF_TRANSACTION)
    ) {
        val washString = extras?.getString(Constants.NOTIFICATION_WASH_MODEL)
        return try {

            val washJson = JsonParser().parse(washString) as JsonObject
            // get data for washModel
            washJson.apply {
                get("address").asString
                get("city").asString
                get("cashback").asInt
                get("id").asInt
                get("seats").asInt
            }
            true
        } catch (error: ParseException) {
            error.printStackTrace()
            false
        } catch (npe: NullPointerException) {
            npe.printStackTrace()
            false
        }

    } else {
        return false
    }
}

fun Bundle.hasFullReportInfo(): Boolean {
    return containsKey(NOTIFICATION_VALUE_OF_TRANSACTION)
            && containsKey(Constants.NOTIFICATION_OPERATION_ID)
            && containsKey(Constants.NOTIFICATION_WASH_MODEL)
}

