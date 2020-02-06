package com.voodoolab.eco.utils

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.voodoolab.eco.models.WashModel
import com.voodoolab.eco.utils.Constants.NOTIFICATION_OPERATION_ID
import com.voodoolab.eco.utils.Constants.NOTIFICATION_VALUE_OF_TRANSACTION
import com.voodoolab.eco.utils.Constants.NOTIFICATION_WASH_ADDRESS
import com.voodoolab.eco.utils.Constants.NOTIFICATION_WASH_CITY
import com.voodoolab.eco.utils.Constants.NOTIFICATION_WASH_MODEL
import me.zhanghai.android.materialprogressbar.MaterialProgressBar
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

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
        animation.duration = 110
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
        animation.duration = 110
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

fun View.fadeInAnimation() {
    this.let { animatedView ->
        animatedView.alpha = 0f
        animatedView.visibility = View.VISIBLE
        val animation = animatedView.animate()
        animation.duration = 150
        animation.interpolator = LinearInterpolator()
        animation.alpha(1f)
        animation.setListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                animatedView.visibility = View.VISIBLE
                animatedView.alpha = 1f
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {

            }
        })
    }
}

fun ViewGroup.openFromUp(screenMetric: Float?) {
    this.let { animatedView ->
        val animation = animatedView.animate()
        animatedView.visibility = View.VISIBLE
        animation.duration = 300
        animation.interpolator = FastOutSlowInInterpolator()
        animation.translationYBy(500 * screenMetric!!)
        animation.setListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {

            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {

            }
        })
    }
}

fun ViewGroup.closeToUp(screenMetric: Float?) {
    this.let {animatedView ->
        val animation = animatedView.animate()
        animatedView.visibility = View.VISIBLE
        animation.duration = 300
        animation.interpolator = FastOutSlowInInterpolator()
        animation.translationYBy(-500 * screenMetric!!)
        animation.setListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                animatedView.translationY = -500 * screenMetric
                animatedView.visibility = View.INVISIBLE
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {

            }
        })
    }
}

fun Intent.hasFullInformationForReport(): Boolean { // if we have all this data that is all
    if (hasExtra(NOTIFICATION_OPERATION_ID) && hasExtra(NOTIFICATION_WASH_MODEL) && hasExtra(
            NOTIFICATION_VALUE_OF_TRANSACTION
        )
    ) {

        println("DEBUG: operationID = ${getIntExtra(NOTIFICATION_OPERATION_ID, -1)}")
        println("DEBUG: wash_model_string = ${getStringExtra(NOTIFICATION_WASH_MODEL)}")
        println(
            "DEBUG: value of transaction = ${getIntExtra(
                NOTIFICATION_VALUE_OF_TRANSACTION,
                -1
            )}"
        )
        println(
            "DEBUG: value of transaction = ${getIntExtra(
                NOTIFICATION_VALUE_OF_TRANSACTION,
                -1
            )}"
        )

        val washString = extras?.getString(NOTIFICATION_WASH_MODEL)
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

fun Intent.toBundle(): Bundle {
    val bundle = Bundle()
    // there are two sources. From the first source it will be int, from the  second it will be string.
    var value: Int? = getIntExtra(NOTIFICATION_VALUE_OF_TRANSACTION, -1)
    if (value == -1) {
        value = getStringExtra(NOTIFICATION_VALUE_OF_TRANSACTION).toIntOrNull()
    }

    var operationId: Int? = getIntExtra(NOTIFICATION_OPERATION_ID, -1)
    if (operationId == -1) {
        operationId = getStringExtra(NOTIFICATION_OPERATION_ID).toIntOrNull()
    }

    val washString = getStringExtra(NOTIFICATION_WASH_MODEL)
    if (value != -1 && operationId != -1 && washString != null) {
        val washJson = JsonParser().parse(washString) as JsonObject
        val washAddress = washJson.get("address").asString
        val washCity = washJson.get("city").asString
        bundle.putInt(NOTIFICATION_VALUE_OF_TRANSACTION, value!!)
        bundle.putInt(NOTIFICATION_OPERATION_ID, operationId!!)
        bundle.putString(NOTIFICATION_WASH_ADDRESS, washAddress)
        bundle.putString(NOTIFICATION_WASH_CITY, washCity)
    }
    return bundle
}

fun String.convertToWashModel(): WashModel {
    // защитить это от падения
    val washJson = JsonParser().parse(this) as JsonObject
    // get data for washModel
    val washAddress = washJson.get("address").asString
    val washCity = washJson.get("city").asString

    return WashModel(
        id = null,
        city = washCity,
        address = washAddress,
        coordinates = null,
        cashback = null,
        seats = null
    )
}

fun View.translateYFromToViaPercent(targetY: Float, percent: Float) {
    val delta = targetY / 100f
    this.translationY = percent * delta * 100
}

fun Long.toDate(): String? {
    return try {
        val date = Date(this)
        val df2 = SimpleDateFormat("yyyy-MM-dd")
        df2.format(date)
    } catch (e: Throwable) {
        null
    }
}

fun String.toCalendar(): Calendar? {
    return try {
        val pattern = "yyyy-MM-dd"
        val date: Date = SimpleDateFormat(pattern).parse(this)
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar
    } catch (e: Throwable) {
        e.printStackTrace()
        null
    }
}

