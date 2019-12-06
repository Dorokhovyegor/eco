package com.voodoolab.eco.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.VideoView
import androidx.fragment.app.Fragment
import com.voodoolab.eco.R
import com.voodoolab.eco.interfaces.AuthenticateListener
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.slots.PredefinedSlots
import ru.tinkoff.decoro.watchers.MaskFormatWatcher
import android.R.attr.start
import android.net.Uri


class AuthFragment: Fragment() {

    private var videoView: VideoView? = null
    private var inputNumber: EditText? = null
    private var authButton: Button?  = null
    private var authenticateListener: AuthenticateListener? = null

    private var authClickListener = View.OnClickListener {
        authenticateListener?.completeAuthenticated()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.auth_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        inputNumber = view.findViewById(R.id.phone_input)
        authButton = view.findViewById(R.id.auth_button)
        authButton?.setOnClickListener(authClickListener)
        videoView = view.findViewById(R.id.video_view)

        val string = "android.resource://com.voodoolab.eco/${R.raw.video_auth}"

        val uri = Uri.parse(string)
        videoView?.setVideoURI(uri)
        videoView?.requestFocus()
        videoView?.start()

        val mask = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER)
        val watcher = MaskFormatWatcher(mask)
        inputNumber?.let {
            watcher.installOn(it)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            authenticateListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        authenticateListener = null
    }
}