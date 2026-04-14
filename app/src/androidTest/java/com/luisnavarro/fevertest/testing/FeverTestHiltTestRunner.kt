package com.luisnavarro.fevertest.testing

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

class FeverTestHiltTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        classLoader: ClassLoader,
        className: String,
        context: Context,
    ): Application {
        return super.newApplication(
            classLoader,
            HiltTestApplication::class.java.name,
            context,
        )
    }
}
