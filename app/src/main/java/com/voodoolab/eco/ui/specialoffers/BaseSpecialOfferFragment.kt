package com.voodoolab.eco.ui.specialoffers

import com.bumptech.glide.RequestManager
import com.voodoolab.eco.ui.UICommunicationListener
import com.voodoolab.eco.utils.ViewModelProviderFactory
import dagger.android.support.DaggerFragment
import javax.inject.Inject

abstract class BaseSpecialOfferFragment: DaggerFragment() {

    @Inject
    lateinit var requestManager: RequestManager
    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
    lateinit var uiCommunicationListener: UICommunicationListener


}