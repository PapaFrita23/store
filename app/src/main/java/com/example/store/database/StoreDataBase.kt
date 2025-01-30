package com.example.store.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.store.dao.StoreDAO
import com.example.store.entities.StoreEntity

@Database(entities = arrayOf(StoreEntity::class), version = 2)
abstract class StoreDataBase: RoomDatabase() {
    abstract fun storeDao(): StoreDAO
}