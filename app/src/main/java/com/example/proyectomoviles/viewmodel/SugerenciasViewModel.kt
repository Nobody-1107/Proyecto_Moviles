package com.example.proyectomoviles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomoviles.model.Sugerencia
import com.example.proyectomoviles.network.ApiService
import com.example.proyectomoviles.network.RetrofitClient
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SugerenciasViewModel : ViewModel() {

    private val _sugerencias = MutableStateFlow<List<Sugerencia>>(emptyList())
    val sugerencias: StateFlow<List<Sugerencia>> = _sugerencias.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val apiService: ApiService by lazy {
        RetrofitClient.instance.create(ApiService::class.java)
    }

    fun loadSugerencias() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // 1. Obtenemos la lista de sugerencias inicial
                val sugerenciasSinPerfil = apiService.getSugerencias()

                // 2. Para cada sugerencia, vamos a buscar su perfil si tiene un user_id
                val sugerenciasConPerfil = sugerenciasSinPerfil.map { sugerencia ->
                    async {
                        if (sugerencia.userId != null) {
                            try {
                                // 3. Hacemos la llamada para obtener el perfil
                                val perfil = apiService.getProfileById(sugerencia.userId)
                                // 4. Devolvemos una copia de la sugerencia con el perfil ya incluido
                                sugerencia.copy(profile = perfil)
                            } catch (e: Exception) {
                                // Si falla la búsqueda del perfil, devolvemos la sugerencia original
                                sugerencia
                            }
                        } else {
                            // Si no hay user_id, es anónima, la devolvemos tal cual
                            sugerencia
                        }
                    }
                }.awaitAll() // Esperamos a que todas las búsquedas de perfiles terminen

                _sugerencias.value = sugerenciasConPerfil

            } catch (e: Exception) {
                _error.value = "No se pudieron cargar las sugerencias: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteSugerencia(sugerenciaId: Long) {
        viewModelScope.launch {
            try {
                val response = apiService.deleteSugerencia(sugerenciaId)
                if (response.isSuccessful) {
                    // Si se borra en la BD, la quitamos de la lista local
                    _sugerencias.update { currentList ->
                        currentList.filterNot { it.id == sugerenciaId }
                    }
                }
            } catch (e: Exception) {
                // Manejar posible error de red o del servidor
                _error.value = "Error al eliminar la sugerencia: ${e.message}"
                e.printStackTrace()
            }
        }
    }
}
