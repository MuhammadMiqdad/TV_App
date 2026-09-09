package com.example.tvapp.ui.list

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

class ShowListViewModel(
    private val repository: ShowRepository = ShowRepositoryImpl(NetworkModule.tvMazeApi)
) : ViewModel() {

    private val _uiState = MutableStateFlow<ShowListUiState>(ShowListUiState.Loading)
    val uiState: StateFlow<ShowListUiState> = _uiState.asStateFlow()

    init {
        loadShows()
    }

    fun loadShows() {
        viewModelScope.launch {
            _uiState.value = ShowListUiState.Loading
            try {
                val shows = repository.getShows()
                _uiState.value = ShowListUiState.Success(shows)
            } catch (e: IOException) {
                _uiState.value = ShowListUiState.Error("Tidak ada koneksi internet. Coba lagi.")
            } catch (e: Exception) {
                _uiState.value = ShowListUiState.Error(e.message ?: "Gagal memuat data. Coba lagi.")
            }
        }
    }
}
