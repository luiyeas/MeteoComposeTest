package com.luisnavarro.fevertest.feature.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luisnavarro.fevertest.core.dispatchers.AppDispatchers
import com.luisnavarro.fevertest.data.location.RandomLocationGenerator
import com.luisnavarro.fevertest.data.weather.WeatherRepository
import java.io.IOException
import java.net.UnknownHostException
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val locationGenerator: RandomLocationGenerator,
    private val dispatchers: AppDispatchers,
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
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

        val previousContent = _uiState.value as? WeatherUiState.Content
        _uiState.update {
            previousContent?.copy(
                isRefreshing = true,
                recoverableErrorMessage = null,
            ) ?: WeatherUiState.Loading
        }

        loadJob = viewModelScope.launch {
            val result = runCatching {
                val location = locationGenerator.generate()
                withContext(dispatchers.io) {
                    repository.getCurrentWeather(location).toUiModel()
                }
            }

            result.onSuccess { weather ->
                _uiState.value = WeatherUiState.Content(
                    weather = weather,
                )
            }.onFailure { throwable ->
                _uiState.value = previousContent?.copy(
                    isRefreshing = false,
                    recoverableErrorMessage = throwable.toUserMessage(),
                ) ?: WeatherUiState.Error(
                    message = throwable.toUserMessage(),
                )
            }
        }.also { job ->
            job.invokeOnCompletion { loadJob = null }
        }
    }
}

private fun Throwable.toUserMessage(): String = when (this) {
    is UnknownHostException,
    is IOException,
    -> "We couldn't reach the weather service. Please try again."

    is HttpException -> "We couldn't load weather for that location right now."
    else -> "We couldn't load weather for that location."
}
