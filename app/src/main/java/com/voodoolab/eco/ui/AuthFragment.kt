package com.voodoolab.eco.ui

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
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import com.orhanobut.hawk.Hawk
import com.voodoolab.eco.interfaces.DataStateListener
import com.voodoolab.eco.network.DataState
import com.voodoolab.eco.states.auth_state.AuthStateEvent
import com.voodoolab.eco.states.code_state.CodeStateEvent
import com.voodoolab.eco.ui.view_models.CodeViewModel
import com.voodoolab.eco.ui.view_models.LoginViewModel
import com.voodoolab.eco.utils.Constants
import com.voodoolab.eco.utils.show
import me.zhanghai.android.materialprogressbar.MaterialProgressBar
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

class AuthFragment : Fragment(), DataStateListener, KeyboardVisibilityEventListener {

    lateinit var codeViewModel: CodeViewModel
    lateinit var loginViewModel: LoginViewModel

    var dataStateHandler: DataStateListener = this

    private var inputNumberEditText: EditText? = null
    private var inputCodeEditText: EditText? = null
    private var inputCodeLayout: TextInputLayout? = null
    private var inputPhoneLayout: TextInputLayout? = null

    private var infoSMSTextView: TextView? = null
    private var getInfoAboutCardButton: Button? = null
    private var hasPasswordTextView: TextView? = null
    private var messageTextView: TextView? = null

    private var progressBar: MaterialProgressBar? = null

    private var authenticateListener: AuthenticateListener? = null

    // lifecycle methods ===========================================================================
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        KeyboardVisibilityEvent.setEventListener(activity, this)
        codeViewModel = ViewModelProvider(this).get(CodeViewModel::class.java)
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        return inflater.inflate(R.layout.auth_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews(view)
        initListeners()
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
    private fun initViews(view: View) {
        inputNumberEditText = view.findViewById(R.id.phone_edit_text)
        inputCodeEditText = view.findViewById(R.id.password_input_edit_text)
        inputCodeLayout = view.findViewById(R.id.password_input_layout)
        inputPhoneLayout = view.findViewById(R.id.phone_input_layout)
        progressBar = view.findViewById(R.id.progress_bar)

        val mask = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER)
        val watcher = MaskFormatWatcher(mask)
        inputNumberEditText?.let {
            watcher.installOn(it)
        }
    }

    private fun initListeners() {
        inputNumberEditText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val number = getNumberFromDecorateNumber(s.toString())
                number?.let {
                    if (it.length == 11) {
                        codeViewModel.setStateEvent(CodeStateEvent.RequestCodeEvent(number))
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        inputCodeEditText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length == 6) {
                    loginViewModel.setStateEvent(AuthStateEvent.LoginEvent(getNumberFromDecorateNumber(inputNumberEditText?.text.toString()), s.toString()))
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
    }

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
                    inputCodeEditText?.visibility = View.VISIBLE
                    inputCodeLayout?.visibility = View.VISIBLE
                    infoSMSTextView?.visibility = View.VISIBLE
                    inputCodeEditText?.requestFocus()
                    startTimer()
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

        loginViewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState.loginResponse?.let {
                if (it.status == "ok") {
                    Hawk.put(Constants.TOKEN, it.responseData?.token)
                    Hawk.put(Constants.TYPE_TOKEN, it.responseData?.type)
                    authenticateListener?.completeAuthenticated()
                }
            }
        })
    }

    private fun startTimer() {
        // todo do it like here https://abhiandroid.com/ui/countdown-timer
    }

    /*private fun changeViewForVisibleKeyboard(visible: Boolean) {
        if (visible) {
            val paramPhoneInput = inputPhoneLayout?.layoutParams as ConstraintLayout.LayoutParams
            paramPhoneInput.verticalBias = 0.0f
            inputPhoneLayout?.requestLayout()
            val paramsButton = authButton?.layoutParams as ConstraintLayout.LayoutParams
            paramsButton.verticalBias = 0.40f
            authButton?.requestLayout()
        } else {
            val paramPhoneInput = inputPhoneLayout?.layoutParams as ConstraintLayout.LayoutParams
            paramPhoneInput.verticalBias = 0.1f
            inputPhoneLayout?.requestLayout()
            val paramsButton = authButton?.layoutParams as ConstraintLayout.LayoutParams
            paramsButton.verticalBias = 1f
            authButton?.requestLayout()
        }
    }
*/
    private fun handleDataStateChange(dataState: DataState<*>?) {
        dataState?.let {
            // handle loading
            progressBar?.show(it.loading)

            // handle message
            it.message?.let { event ->
                event.getContentIfNotHandled()?.let {
                    showToast(it)   // todo snackbar instead
                }
            }
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
                if (it.isDigit()) {
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

    override fun onVisibilityChanged(isOpen: Boolean) {
        //changeViewForVisibleKeyboard(isOpen)
    }
    // =============================================================================================
}