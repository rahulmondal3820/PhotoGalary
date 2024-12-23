package com.example.photogallery.API

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photogallery.PhotoRepository
import com.example.photogallery.PreferenceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
private const val TAG = "PhotoGalleryViewModel"

class PhotoGalleryViewModel() :ViewModel() {
    private val photoRepository = PhotoRepository()
     private val preferenceRepository = PreferenceRepository.get()
    private val _uiState:MutableStateFlow<PhotoGalleryUiState> = MutableStateFlow(PhotoGalleryUiState())
    val uiState: StateFlow<PhotoGalleryUiState>
        get() = _uiState.asStateFlow()

    init {
            viewModelScope.launch {
                preferenceRepository.storeQuery.collectLatest {  storeQuery->
                try {

                    val items =fetchGalleryItems(storeQuery)

                     Log.d(TAG,"Receive dada $items")
                   _uiState.update { oldDate->

                       oldDate.copy(query = storeQuery, images =items )
                   }

                }catch(ex:Exception){
                    Log.e(TAG, "Receive gallery Items  fail",ex)
            }
                }

          }
        viewModelScope.launch {
            preferenceRepository.isPolling.collect{isPolling->
                _uiState.update { it.copy(isPolling = isPolling) }
            }
        }
    }

     fun setQuery(query:String) {
        viewModelScope.launch {
              preferenceRepository.setQuery(query)
        }
    }
    fun togglePolling(){
        viewModelScope.launch { preferenceRepository.setPolling(!uiState.value.isPolling)}
    }

    private suspend fun fetchGalleryItems(query: String):List<GalleryItem> {
       return if (query.isNotEmpty()) {

            photoRepository.searchPhoto(query)
        } else { Log.d(TAG, "Fetching default photos (empty query)")
           val photos =  photoRepository.fetchPhotos()
           Log.d(TAG, "Fetched ${photos.size} photos for empty query")
           photos
        }
    }



}
data class PhotoGalleryUiState(
    val query: String="",
    val images:List<GalleryItem> = listOf(),
    val isPolling:Boolean = false
)