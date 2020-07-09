package com.voodoolab.eco.di.modules

import androidx.lifecycle.ViewModelProvider
import com.voodoolab.eco.utils.ViewModelProviderFactory

import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactoryModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelProviderFactory): ViewModelProvider.Factory
}