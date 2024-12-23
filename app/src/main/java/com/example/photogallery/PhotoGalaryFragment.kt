package com.example.photogallery
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.photogallery.API.PhotoGalleryViewModel
import com.example.photogallery.databinding.FragmentPhotoGalleryBinding
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


private const val TAG = "PhotoGalleryFragment"
private const val POLL_WORK = "POLL_WORK"

class PhotoGalleryFragment:Fragment() {
  private  var searchView: SearchView? = null
    private var pollingMenuItem:MenuItem? = null
    private var _binding : FragmentPhotoGalleryBinding?=null
    private  val binding
        get() = checkNotNull(_binding){
            "not have any view in FragmentPhotoGallery"
        }
    val photoGalleryViewModel :PhotoGalleryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)



    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
      _binding=FragmentPhotoGalleryBinding.inflate(inflater,container,false)
        binding.photoGrid.layoutManager = GridLayoutManager(context,3)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewLifecycleOwner.lifecycleScope.launch {



                photoGalleryViewModel.uiState.collect { state ->

                    binding.photoGrid.adapter = PhotoRecyclerAdapter(state.images){photoPageUri->

                       findNavController().navigate(
                           PhotoGalleryFragmentDirections.showPhoto(photoPageUri.html.toUri())
                       )
                        CustomTabsIntent.Builder()
                            .setToolbarColor(
                                ContextCompat.getColor(
                                requireContext(), com.google.android.material.R.color.design_default_color_primary))
                            .setShowTitle(true)
                            .build()
                            .launchUrl(requireContext(),photoPageUri.html.toUri())

                            }
//                    searchView?.setQuery(state.query,false)
//                    updatePollingState(state.isPolling)

                }


        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_photo_gallery,menu)
        val menuItem:MenuItem = menu.findItem(R.id.menu_item_search)
        searchView= menuItem.actionView as? SearchView
        pollingMenuItem = menu.findItem(R.id.menu_item_toggle_polling)
        searchView?.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d(TAG, "QueryTextSubmit: $query")

                photoGalleryViewModel.setQuery(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return  when (item.itemId) {
            R.id.menu_item_clear_search -> {
                photoGalleryViewModel.setQuery("")
                true
            }
            R.id.menu_item_toggle_polling -> {
                photoGalleryViewModel.togglePolling()
                true
            }
            else ->  super.onOptionsItemSelected(item)
        }

    }
    private fun updatePollingState(isPolling:Boolean) {
        val togglePoling = if (isPolling) {
            R.string.stop_polling

        }else{
            R.string.start_polling
        }
        pollingMenuItem?.setTitle(togglePoling)
        if (isPolling){
            val constraint = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<PullWorker>(15,TimeUnit.MINUTES)
                .setConstraints(constraint)
                .build()
            WorkManager.getInstance(requireContext())
                .enqueueUniquePeriodicWork(POLL_WORK,ExistingPeriodicWorkPolicy.KEEP,workRequest)
        }else{
            WorkManager.getInstance(requireContext()).cancelUniqueWork(POLL_WORK)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        searchView =null
        pollingMenuItem = null
    }

}