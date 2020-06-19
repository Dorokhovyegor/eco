package com.voodoolab.eco.ui

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.JsonParser
import com.orhanobut.hawk.Hawk
import com.voodoolab.eco.R
import com.voodoolab.eco.interfaces.AuthenticateListener
import com.voodoolab.eco.interfaces.DataStateListener
import com.voodoolab.eco.network.DataState
import com.voodoolab.eco.states.auth_state.AuthStateEvent
import com.voodoolab.eco.states.code_state.CodeStateEvent
import com.voodoolab.eco.ui.view_models.CodeViewModel
import com.voodoolab.eco.ui.view_models.LoginViewModel
import com.voodoolab.eco.utils.Constants
import com.voodoolab.eco.utils.show
import kotlinx.android.synthetic.main.auth_layout.*
import me.zhanghai.android.materialprogressbar.MaterialProgressBar
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.slots.PredefinedSlots
import ru.tinkoff.decoro.watchers.MaskFormatWatcher
import java.text.SimpleDateFormat
import kotlin.math.log

class AuthFragment : Fragment(), DataStateListener {

    lateinit var codeViewModel: CodeViewModel
    lateinit var loginViewModel: LoginViewModel

    var dataStateHandler: DataStateListener = this

    private var inputNumberEditText: EditText? = null
    private var inputCodeEditText: EditText? = null
    private var inputCodeLayout: TextInputLayout? = null
    private var inputPhoneLayout: TextInputLayout? = null

    private var infoSMSTextView: TextView? = null
    private var getInfoAboutCardButton: Button? = null
    private var repeatSMSButton: Button? = null
    private var infoCardTextView: TextView? = null

    private var progressBar: MaterialProgressBar? = null

    private var authenticateListener: AuthenticateListener? = null
    private var timer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.auth_layout, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        codeViewModel = ViewModelProvider(this).get(CodeViewModel::class.java)
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews()
        initListeners()
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
    private fun initViews() {
        setNormalLayoutParams()
        val mask = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER)
        val watcher = MaskFormatWatcher(mask)
        phone_edit_text?.let {
            watcher.installOn(it)
        }
    }

    private fun initListeners() {
        repeatButton?.setOnClickListener {
            if (getNumberFromDecorateNumber(inputNumberEditText?.text.toString())?.length == 11) {
                repeatButton?.visibility = View.INVISIBLE
                codeViewModel.setStateEvent(CodeStateEvent.RequestCodeEvent(inputNumberEditText?.text.toString()))
                if (!codeViewModel.dataState.hasObservers() && !codeViewModel.viewState.hasObservers()) {
                    subscriberCodeObservers()
                }
            }
        }

        phone_edit_text?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val number = getNumberFromDecorateNumber(s.toString())
                number?.let {
                    if (it.length == 11) {
                        codeViewModel.setStateEvent(CodeStateEvent.RequestCodeEvent(number))
                        if (!codeViewModel.dataState.hasObservers() && !codeViewModel.viewState.hasObservers()) {
                            subscriberCodeObservers()
                        }
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        password_input_edit_text?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length == 6 && getNumberFromDecorateNumber(inputNumberEditText?.text.toString())?.length == 11) {
                    loginViewModel.setStateEvent(
                        AuthStateEvent.LoginEvent(
                            getNumberFromDecorateNumber(inputNumberEditText?.text.toString()),
                            s.toString()
                        )
                    )
                    if (!loginViewModel.viewState.hasObservers() && !loginViewModel.dataState.hasObservers()) {
                        subscriberLoginObservers()
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
    }

    private fun subscriberCodeObservers() {
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
                    startTimer(it.time?.times(1000)?.toLong())
                }
            }
        })
    }

    private fun subscriberLoginObservers() {
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

    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
        timer = null
        codeViewModel.dataState.removeObservers(viewLifecycleOwner)
        codeViewModel.viewState.removeObservers(viewLifecycleOwner)
        loginViewModel.dataState.removeObservers(viewLifecycleOwner)
        loginViewModel.viewState.removeObservers(viewLifecycleOwner)
    }

    private fun startTimer(time: Long?) {
        time?.let {
            timer = object : CountDownTimer(it, 1000) {
                override fun onFinish() {
                    infoSMSTextView?.visibility = View.INVISIBLE
                    repeatSMSButton?.visibility = View.VISIBLE
                }

                override fun onTick(millisUntilFinished: Long) {
                    val timeFormat = SimpleDateFormat("mm:ss")
                    val normalTime = timeFormat.format(millisUntilFinished)
                    infoSMSTextView?.text =
                        Html.fromHtml(getString(R.string.time_text, normalTime), 0)
                }
            }
            timer?.start()
        }
    }

    private fun setNormalLayoutParams() {
        val buttonParams = getInfoAboutCardButton?.layoutParams as ConstraintLayout.LayoutParams
        val infoCardParams = infoCardTextView?.layoutParams as ConstraintLayout.LayoutParams
        buttonParams.verticalBias = 1.0f
        infoCardParams.verticalBias = 1.0f
        getInfoAboutCardButton?.requestLayout()
        infoCardTextView?.requestLayout()
    }

    private fun handleDataStateChange(dataState: DataState<*>?) {
        dataState?.let {
            // handle loading
            progressBar?.show(it.loading)

            // handle message
            it.message?.let { event ->
                event.getContentIfNotHandled()?.let {
                    try {
                        showSnackBar(JsonParser().parse(it).asJsonObject.get("msg").asString)
                    } catch (t: Throwable) {
                        showSnackBar("Произошла ошибка, возможно, Вашего телефона нет в базе")
                        t.printStackTrace()
                    }
                }
            }
        }
    }

    private fun showSnackBar(message: String?) {
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

    override fun onDataStateChange(dataState: DataState<*>?) {
        handleDataStateChange(dataState)
    }
}