package com.example.store.activities

import com.example.store.entities.StoreEntity

interface MainAux {
    fun hideFab(isVisible: Boolean = false)
    fun addStore(storeEntity: StoreEntity)
}