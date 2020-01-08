package com.voodoolab.eco.ui

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.voodoolab.eco.R
import com.voodoolab.eco.interfaces.SkipSplashScreenListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreen : Fragment() {

    var splashScreenListener: SkipSplashScreenListener? = null

    // todo (Получить список городов)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.splash_screen_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        GlobalScope.launch {
            delay(800)
            splashScreenListener?.splashScreenComplete()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            splashScreenListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        splashScreenListener = null
    }
}