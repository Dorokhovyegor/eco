package com.voodoolab.eco.ui

import android.R.attr.data
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.voodoolab.eco.R
import com.voodoolab.eco.interfaces.AuthenticateListener
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.slots.PredefinedSlots
import ru.tinkoff.decoro.watchers.MaskFormatWatcher
import android.R.attr.start
import android.net.Uri
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.orhanobut.hawk.Hawk
import com.voodoolab.eco.interfaces.DataStateListener
import com.voodoolab.eco.network.DataState
import com.voodoolab.eco.states.auth_state.AuthStateEvent
import com.voodoolab.eco.states.code_state.CodeStateEvent
import com.voodoolab.eco.utils.Constants
import me.zhanghai.android.materialprogressbar.MaterialProgressBar

enum class AuthStateScreen {

}

class AuthFragment : Fragment(), DataStateListener {

    lateinit var codeViewModel: CodeViewModel
    lateinit var loginViewModel: LoginViewModel

    var dataStateHandler: DataStateListener = this

    private var inputNumber: EditText? = null
    private var inputCode: EditText? = null

    private var timerTextView: TextView? = null
    private var authButton: Button? = null

    private var progressBar: MaterialProgressBar? = null

    private var authenticateListener: AuthenticateListener? = null

    private var getCodeListener = View.OnClickListener {
        val number = getNumberFromDecorateNumber(inputNumber?.text.toString())
        if (number?.length == 11) {
            codeViewModel.setStateEvent(CodeStateEvent.RequestCodeEvent(number))
        } else {
            showToast("Допишите номер")
        }
    }

    private var logingListener = View.OnClickListener {
        val number = getNumberFromDecorateNumber(inputNumber?.text.toString())
        val code = inputCode?.text.toString()
        loginViewModel.setStateEvent(AuthStateEvent.LoginEvent(number, code))
    }

    // lifecycle methods ===========================================================================
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        codeViewModel = ViewModelProvider(this).get(CodeViewModel::class.java)
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        return inflater.inflate(R.layout.auth_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        inputNumber = view.findViewById(R.id.phone_input)
        inputCode = view.findViewById(R.id.code_input)

        timerTextView = view.findViewById(R.id.timer_textview)
        authButton = view.findViewById(R.id.auth_button)

        progressBar = view.findViewById(R.id.progress_bar)

        authButton?.setOnClickListener(logingListener)

        val mask = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER)
        val watcher = MaskFormatWatcher(mask)
        inputNumber?.let {
            watcher.installOn(it)
        }
        subscribeObservers()
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
    //==============================================================================================

    private fun subscribeObservers() {

        codeViewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            dataStateHandler.onDataStateChange(dataState)
            dataState.data?.let { codeViewState ->
                codeViewState.getContentIfNotHandled()?.let {
                    it.codeResponse?.let {
                        codeViewModel.setCodeResponse(it)
                    }
                }
            }
        })

        codeViewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState.codeResponse?.let {
                if (it.status == "ok") {
                    inputCode?.visibility = View.VISIBLE
                    inputCode?.requestFocus()
                    timerTextView?.visibility = View.VISIBLE
                    startTimer()
                    authButton?.text = "Войти"
                    authButton?.setOnClickListener(logingListener)
                }
            }
        })

        loginViewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            dataStateHandler.onDataStateChange(dataState)
            dataState.data?.let { loginViewState ->
                loginViewState.getContentIfNotHandled()?.let {
                    it.loginResponse?.let {
                        loginViewModel.setLoginResponse(it)
                    }
                }
            }
        })

        loginViewModel.viewState.observe(viewLifecycleOwner, Observer { viewState->
            viewState.loginResponse?.let {
                if (it.status == "ok") {
                    println("DEBUG ${it.responseData?.token}")
                    Hawk.put(Constants.TOKEN, it.responseData?.token)
                    Hawk.put(Constants.TYPE_TOKEN, it.responseData?.type)
                    authenticateListener?.completeAuthenticated()
                 }
            }
        })
    }

    private fun startTimer() {

    }

    private fun handleDataStateChange(dataState: DataState<*>?) {
        dataState?.let {
            // handle loading
            showProgressBar(it.loading)

            // handle message
            it.message?.let { event ->
                event.getContentIfNotHandled()?.let {
                    showToast(it)
                }
            }
        }
    }

    private fun showProgressBar(visible: Boolean) {
        if (visible) {
            progressBar?.visibility = View.VISIBLE
        } else {
            progressBar?.visibility = View.GONE
        }
    }

    private fun showToast(message: String?) {
        message?.let {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getNumberFromDecorateNumber(decorateString: String?): String? {
        var number: String? = ""
        decorateString?.let {
            it.toCharArray().forEach {
                if(it.isDigit()) {
                   number += it
                }
            }
        }
        return number
    }

    // overrides methods ===========================================================================
    override fun onDataStateChange(dataState: DataState<*>?) {
        handleDataStateChange(dataState)
    }

    // =============================================================================================
}