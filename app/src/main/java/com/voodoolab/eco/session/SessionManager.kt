package com.voodoolab.eco.session

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.voodoolab.eco.persistance.AuthTokenDao
import com.voodoolab.eco.persistance.entities.AuthToken
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val application: Application
) {

    private val TAG = "AppDebug"

    private val _cashedToken = MutableLiveData<AuthToken>()

    val cashedToken: LiveData<AuthToken>
        get() = _cashedToken

    fun login(newValue: AuthToken) {
        setValue(newValue)
    }

    fun logout() {
        GlobalScope.launch(IO) {
            var errorMessage: String? = null
            try {
                cashedToken.value!!.account_pk.let {
                    authTokenDao.nullifyToken(it)
                }
            } catch (e: CancellationException) {
                errorMessage = e.message
            } catch (e: Exception) {
                errorMessage = e.message + "\n" + e.message
            } finally {
                setValue(null)
            }
        }
    }

    fun setValue(newValue: AuthToken?) {
        GlobalScope.launch(Main) {
            if (_cashedToken.value != newValue) {
                _cashedToken.value = newValue
            }
        }
    }

    fun isConnectedToTheInternet(): Boolean{
        val cm = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        try{
            return cm.activeNetworkInfo.isConnected
        } catch (e: Exception){

        }
        return false
    }
}