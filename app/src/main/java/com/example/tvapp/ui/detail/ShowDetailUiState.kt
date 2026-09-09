package com.example.tvapp.ui.detail

import com.example.tvapp.data.model.Show

sealed interface ShowDetailUiState {
    data object Loading : ShowDetailUiState
    data class Success(val show: Show) : ShowDetailUiState
    data class Error(val message: String) : ShowDetailUiState
}
