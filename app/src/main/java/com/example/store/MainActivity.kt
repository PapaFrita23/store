package com.example.store

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.store.databinding.ActivityMainBinding
import java.util.concurrent.LinkedBlockingQueue

class MainActivity : AppCompatActivity(), onClickListener, MainAux {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mAdapter: StoreAdapter
    private lateinit var mGridLayout: GridLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        /*binding.btnSave.setOnClickListener {
            val storeEntity = StoreEntity(name = binding.etName.text.toString().trim())

            Thread {
                StoreApplication.dataBase.storeDao().addStore(storeEntity)
            }.start()


            mAdapter.add(storeEntity)
        }*/

        binding.fab.setOnClickListener {
            launcherEditFragment()
        }
    }

    private fun launcherEditFragment() {
        val fragment = EditStoreFragment()

        supportFragmentManager.beginTransaction()
            .add(R.id.containerMain, fragment)
            .addToBackStack(null)
            .commit()

        //binding.fab.hide()
        hideFab()
    }

    private fun setupRecyclerView() {
        mAdapter = StoreAdapter(mutableListOf(), this)
        mGridLayout = GridLayoutManager(this, 2)

        getStores()

        binding.recyclerView.apply {
            setHasFixedSize(true)

            layoutManager = mGridLayout
            adapter = mAdapter
        }
    }

    private fun getStores() {
        val queue = LinkedBlockingQueue<MutableList<StoreEntity>>()

        Thread {
            val stores = StoreApplication.dataBase.storeDao().getAllStores()
            queue.add(stores)
        }.start()

        mAdapter.setStores(queue.take())
    }

    override fun onClick(storeEntity: StoreEntity) {

    }

    override fun onFavoriteStore(storeEntity: StoreEntity) {
        storeEntity.isFavorite = !storeEntity.isFavorite
        val queue = LinkedBlockingQueue<StoreEntity>()

        Thread {
            StoreApplication.dataBase.storeDao().updateStore(storeEntity)
            queue.add(storeEntity)
        }.start()

        mAdapter.update(queue.take())
    }

    override fun onDeleteStore(storeEntity: StoreEntity) {
        val queue = LinkedBlockingQueue<StoreEntity>()

        Thread {
            StoreApplication.dataBase.storeDao().deleteStore(storeEntity)
            queue.add(storeEntity)
        }.start()

        mAdapter.delete(queue.take())
    }

    /*
    * MainAux
    */
    override fun hideFab(isVisible: Boolean) {
        if (isVisible) {
            binding.fab.show()
        } else {
            binding.fab.hide()
        }
    }

    override fun addStore(storeEntity: StoreEntity) {
        mAdapter.add(storeEntity)
    }
}