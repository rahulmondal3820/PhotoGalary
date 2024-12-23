package com.example.photogallery

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class PreferenceRepository private constructor(private val dataStore: DataStore<Preferences>) {
    val storeQuery: Flow<String> = dataStore.data.map {
        it[SEARCH_QUERY_KEY] ?: ""

    }.distinctUntilChanged()

    suspend fun setQuery(query: String) {
        dataStore.edit {
            it[SEARCH_QUERY_KEY] = query
        }
    }

    val lastResultId: Flow<String> = dataStore.data.map {
        it[PREF_LAST_RESULT_ID] ?: ""
    }

    suspend fun setLastResultId(lastResultId: String) {
        dataStore.edit {
            it[PREF_LAST_RESULT_ID] = lastResultId
        }
    }
   val isPolling:Flow<Boolean> = dataStore.data.map {
       it[PREF_IS_POLLING] ?: false
   }
    suspend fun setPolling(isPolling: Boolean) {
        dataStore.edit {
            it[PREF_IS_POLLING] = isPolling
        }
    }

    companion object {
        private val SEARCH_QUERY_KEY = stringPreferencesKey("search_key")
        private val PREF_LAST_RESULT_ID = stringPreferencesKey("last_result_id")
        private val PREF_IS_POLLING = booleanPreferencesKey("is_polling")

        private var INSTANCE: PreferenceRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                val dataStore = PreferenceDataStoreFactory.create {
                    context.preferencesDataStoreFile("settings")
                }
                INSTANCE = PreferenceRepository(dataStore)
            }
        }

        fun get(): PreferenceRepository {
            return INSTANCE
                ?: throw IllegalStateException("PreferencesRepository must be initialized")


        }
    }
}

