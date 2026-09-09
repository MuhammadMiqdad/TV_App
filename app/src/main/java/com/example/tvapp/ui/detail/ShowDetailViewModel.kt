package com.example.tvapp.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tvapp.data.network.NetworkModule
import com.example.tvapp.data.repository.ShowRepository
import com.example.tvapp.data.repository.ShowRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException


class ShowDetailViewModel(
    private val showId: Int,
    private val repository: ShowRepository = ShowRepositoryImpl(NetworkModule.tvMazeApi)
) : ViewModel() {

    private val _uiState = MutableStateFlow<ShowDetailUiState>(ShowDetailUiState.Loading)
    val uiState: StateFlow<ShowDetailUiState> = _uiState.asStateFlow()

    init {
        loadShowDetail()
    }

    fun loadShowDetail() {
        viewModelScope.launch {
            _uiState.value = ShowDetailUiState.Loading
            try {
                val show = repository.getShowDetail(showId)
                _uiState.value = ShowDetailUiState.Success(show)
            } catch (e: IOException) {
                _uiState.value = ShowDetailUiState.Error("Tidak ada koneksi internet. Coba lagi.")
            } catch (e: Exception) {
                _uiState.value = ShowDetailUiState.Error(e.message ?: "Gagal memuat detail. Coba lagi.")
            }
        }
    }
}
