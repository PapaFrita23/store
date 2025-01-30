package com.example.store.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.store.activities.MainActivity
import com.example.store.R
import com.example.store.database.StoreApplication
import com.example.store.entities.StoreEntity
import com.example.store.databinding.FragmentEditStoreBinding
import com.google.android.material.snackbar.Snackbar
import java.util.concurrent.LinkedBlockingQueue

class EditStoreFragment : Fragment() {
    private lateinit var mBinding: FragmentEditStoreBinding
    private var mActivity: MainActivity? = null

    private var isEditMode: Boolean = false
    private var mStoreEntity: StoreEntity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentEditStoreBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments?.getLong("id", 0)

        if (id != null && id != 0L) {
            isEditMode = true

            getStore(id)
        } else { // AÑADIRLO
            isEditMode = false

            mStoreEntity = StoreEntity(name = "", phone = "", website = "", photoUrl = "")
        }

        mActivity = activity as? MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity?.supportActionBar?.title = getString(R.string.edit_store_title)
        setHasOptionsMenu(true)

        mBinding.etPhotoUrl.addTextChangedListener {
            Glide.with(this)
                .load(mBinding.etPhotoUrl.text.toString())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(mBinding.imgPhoto)
        }
    }

    private fun getStore(id: Long) {
        val queue = LinkedBlockingQueue<StoreEntity>()

        Thread {
            mStoreEntity = StoreApplication.dataBase.storeDao().getStoreById(id)
            queue.add(mStoreEntity)
        }.start()

        queue.take()?.let {
            setUiStore(it)
        }
    }

    private fun setUiStore(storeEntity: StoreEntity) {
        with(mBinding) {
            etName.setText(storeEntity.name)
            etPhone.setText(storeEntity.phone)
            etWebSite.setText(storeEntity.website)
            etPhotoUrl.setText(storeEntity.photoUrl)
            activity?.let {
                Glide.with(it)
                    .load(storeEntity.photoUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(imgPhoto)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                mActivity?.onBackPressedDispatcher?.onBackPressed()
                true
            }
            R.id.action_save -> {
                /*val store = StoreEntity(name = mBinding.etName.text.toString().trim(),
                                        phone = mBinding.etPhone.text.toString().trim(),
                                        website = mBinding.etWebSite.text.toString().trim(),
                                        photoUrl = mBinding.etPhotoUrl.text.toString().trim())*/
                if (mStoreEntity != null) {
                    with(mStoreEntity!!) {
                        name = mBinding.etName.text.toString().trim()
                        phone = mBinding.etPhone.text.toString().trim()
                        website = mBinding.etWebSite.text.toString().trim()
                        photoUrl = mBinding.etPhotoUrl.text.toString().trim()

                    }
                }

                val queue = LinkedBlockingQueue<StoreEntity>()
                Thread {
                    if (isEditMode) {
                        StoreApplication.dataBase.storeDao().updateStore(mStoreEntity!!)
                    } else {
                        mStoreEntity?.id = StoreApplication.dataBase.storeDao().addStore(mStoreEntity!!)
                    }

                    queue.add(mStoreEntity)
                }

                with(queue.take()) {
                    if (isEditMode) {
                        mActivity?.updateStore(this)
                        Snackbar.make(mBinding.root, "Tienda modificada correctamente", Snackbar.LENGTH_SHORT).show()
                    } else {
                        mActivity?.addStore(this)
                        Snackbar.make(mBinding.root, "Tienda agregada correctamente", Snackbar.LENGTH_SHORT).show()
                    }
                }

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity?.supportActionBar?.title = getString(R.string.app_name)
        mActivity?.hideFab(true)

        setHasOptionsMenu(false)

        super.onDestroy()
    }
}