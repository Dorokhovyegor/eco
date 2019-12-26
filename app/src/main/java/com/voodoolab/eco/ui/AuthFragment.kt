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
    private var timerTextView: TextView? = null
    private var authButton: Button? = null
    private var progressBar: MaterialProgressBar? = null
    private var hasPasswordTextView: TextView? = null
    private var messageTextView: TextView? = null

    private var authenticateListener: AuthenticateListener? = null

    private var getCodeClickListener = View.OnClickListener {                                         // получить пароль, для основной кнопки
        val number = getNumberFromDecorateNumber(inputNumberEditText?.text.toString())
        if (number?.length == 11) {
            codeViewModel.setStateEvent(CodeStateEvent.RequestCodeEvent(number))
        } else {
            inputPhoneLayout?.error = "Неверный формат номера"
        }
    }

    private var loginClickListener = View.OnClickListener {                                           // если отправили код и хотим войти
        val number = getNumberFromDecorateNumber(inputNumberEditText?.text.toString())
        val code = inputCodeEditText?.text.toString()
        loginViewModel.setStateEvent(AuthStateEvent.LoginEvent(number, code))
    }

    private var hasPasswordClickListener = View.OnClickListener {                                     // если код изначально есть и хотим его ввести
        authButton?.setOnClickListener(loginClickListener)
        inputCodeLayout?.visibility = View.VISIBLE
        inputCodeEditText?.visibility = View.VISIBLE
        authButton?.text = "Войти"
        if (it is TextView) {
            it.text = "Получить новый пароль"
        }
    }

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
        timerTextView = view.findViewById(R.id.timer_textview)
        authButton = view.findViewById(R.id.auth_button)
        progressBar = view.findViewById(R.id.progress_bar)
        hasPasswordTextView = view.findViewById(R.id.has_password)
        messageTextView = view.findViewById(R.id.message_text_view)
        inputPhoneLayout = view.findViewById(R.id.phone_input_layout)

        authButton?.isEnabled = false

        val mask = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER)
        val watcher = MaskFormatWatcher(mask)
        inputNumberEditText?.let {
            watcher.installOn(it)
        }

        val image = view.findViewById<ImageView>(R.id.image_view)
        val path = "android.resource://com.voodoolab.eco/" + R.raw.video2
        context?.let {
            Glide
                .with(it)
                .asGif()
                .load(Uri.parse(path))
                .centerCrop()
                .into(image)
        }
    }

    private fun initListeners() {
        hasPasswordTextView?.setOnClickListener(hasPasswordClickListener)
        authButton?.setOnClickListener(getCodeClickListener)
        inputNumberEditText?.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                val number = getNumberFromDecorateNumber(s.toString())
                number?.let {
                    if (it.length == 11) {
                        authButton?.isEnabled = true
                        authButton?.background = resources.getDrawable(R.drawable.active_button_drawable, null)
                        authButton?.setTextColor(resources.getColor(R.color.white, null))

                    } else {
                        authButton?.isEnabled = false
                        authButton?.background = resources.getDrawable(R.drawable.disable_button_drawable, null)
                        authButton?.setTextColor(resources.getColor(R.color.disabled_button_grey, null))
                    }
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
                    inputCodeEditText?.requestFocus()
                    startTimer()
                    authButton?.text = "Войти"
                    authButton?.setOnClickListener(loginClickListener)
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
                    Hawk.put(Constants.TOKEN, it.responseData?.token)
                    Hawk.put(Constants.TYPE_TOKEN, it.responseData?.type)
                    authenticateListener?.completeAuthenticated()
                 }
            }
        })
    }

    private fun startTimer() {
        messageTextView?.visibility = View.VISIBLE
        timerTextView?.visibility = View.VISIBLE
        hasPasswordTextView?.visibility = View.GONE
    }

    private fun changeViewForVisibleKeyboard(visible: Boolean) {
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

    override fun onVisibilityChanged(isOpen: Boolean) {
        changeViewForVisibleKeyboard(isOpen)
    }
    // =============================================================================================
}