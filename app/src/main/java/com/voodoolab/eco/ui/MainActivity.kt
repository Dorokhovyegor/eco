package com.voodoolab.eco.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.voodoolab.eco.R
import com.voodoolab.eco.interfaces.SkipListener
import com.voodoolab.eco.ui.onboarding.OnBoardContainerFragment

class MainActivity : AppCompatActivity(), SkipListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, OnBoardContainerFragment(this), "on_board")
            .commit()

    }

    override fun onResume() {
        super.onResume()
    }

    override fun skipGuide() {
        supportFragmentManager?.beginTransaction()
            .setCustomAnimations(R.anim.enter_from_right, R.anim.nav_default_exit_anim)
            .replace(R.id.fragment_container, AuthFragment(), "auth")
            .commit()
    }
}
