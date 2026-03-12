package com.luisnavarro.fevertest.feature.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.luisnavarro.fevertest.core.dispatchers.AppDispatchers
import com.luisnavarro.fevertest.data.location.RandomLocationGenerator
import com.luisnavarro.fevertest.data.weather.WeatherRepository
import java.io.IOException
import java.net.UnknownHostException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class WeatherViewModel(
    private val repository: WeatherRepository,
    private val locationGenerator: RandomLocationGenerator,
    private val dispatchers: AppDispatchers,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadWeather()
    }

    fun onAction(action: WeatherUiAction) {
        when (action) {
            WeatherUiAction.RefreshClicked,
            WeatherUiAction.RetryClicked,
            -> loadWeather()
        }
    }

    private fun loadWeather() {
        if (loadJob?.isActive == true) return

        val hasExistingContent = _uiState.value.weather != null
        _uiState.update { currentState ->
            currentState.copy(
                isInitialLoading = !hasExistingContent,
                isRefreshing = hasExistingContent,
                errorMessage = null,
            )
        }

        loadJob = viewModelScope.launch {
            val result = runCatching {
                val location = locationGenerator.generate()
                withContext(dispatchers.io) {
                    repository.getCurrentWeather(location).toUiModel()
                }
            }

            result.onSuccess { weather ->
                _uiState.value = WeatherUiState(weather = weather)
            }.onFailure { throwable ->
                _uiState.update { currentState ->
                    currentState.copy(
                        isInitialLoading = false,
                        isRefreshing = false,
                        errorMessage = throwable.toUserMessage(),
                    )
                }
            }
        }.also { job ->
            job.invokeOnCompletion { loadJob = null }
        }
    }
}

class WeatherViewModelFactory(
    private val repository: WeatherRepository,
    private val locationGenerator: RandomLocationGenerator,
    private val dispatchers: AppDispatchers,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras,
    ): T {
        require(modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            "Unknown ViewModel class: ${modelClass.name}"
        }

        @Suppress("UNCHECKED_CAST")
        return WeatherViewModel(
            repository = repository,
            locationGenerator = locationGenerator,
            dispatchers = dispatchers,
        ) as T
    }
}

private fun Throwable.toUserMessage(): String = when (this) {
    is UnknownHostException,
    is IOException,
    -> "We couldn't reach the weather service. Please try again."

    is HttpException -> "We couldn't load weather for that location right now."
    else -> "We couldn't load weather for that location."
}
