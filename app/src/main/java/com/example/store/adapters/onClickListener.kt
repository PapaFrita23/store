package com.example.store.adapters

import com.example.store.entities.StoreEntity

interface onClickListener {
    fun onClick(storeId: Long)
    fun onFavoriteStore(storeEntity: StoreEntity)
    fun onDeleteStore(storeEntity: StoreEntity)
}