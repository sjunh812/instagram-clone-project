package org.sjhstudio.instagramclone.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import org.sjhstudio.instagramclone.adapter.DetailViewAdapter
import org.sjhstudio.instagramclone.adapter.DetailViewAdapterCallback
import org.sjhstudio.instagramclone.databinding.FragmentDetailViewBinding
import org.sjhstudio.instagramclone.viewmodel.PhotoContentViewModel
import org.sjhstudio.instagramclone.viewmodel.ProfileViewModel

class DetailViewFragment: Fragment() {

    private lateinit var binding: FragmentDetailViewBinding
    private lateinit var detailViewAdapter: DetailViewAdapter

    private val photoContentVm: PhotoContentViewModel by activityViewModels()
    private val profileVm: ProfileViewModel by activityViewModels()

    override fun onStop() {
        super.onStop()
        photoContentVm.remove()
        profileVm.remove()
    }

    override fun onResume() {
        super.onResume()
        photoContentVm.getAll()
        profileVm.getAll()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUi()
        observePhotoContent()
        observeProfiles()
    }

    private fun setUi() {
        detailViewAdapter = DetailViewAdapter(requireContext())
            .apply {
                setHasStableIds(true)
                setDetailViewAdapterCallback(object: DetailViewAdapterCallback {
                    override fun onClickFavorite(pos: Int) {
                        photoContentVm.contentIdLiveData.value?.reversed()?.let { value ->
                            photoContentVm.updateFavorite(value[pos])
                        }
                    }
                })
            }
        binding.detailRv.apply {
            adapter = detailViewAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observePhotoContent() {
        photoContentVm.contentLiveData.observe(viewLifecycleOwner) { items ->
            println("xxx observePhotoContent(contentLiveData) from DetailViewFragment")
            if(items.isNotEmpty()) {
                detailViewAdapter.apply {
                    contents = items.reversed()
                    notifyDataSetChanged()
                }
            }
        }
        photoContentVm.contentIdLiveData.observe(viewLifecycleOwner) { items ->
            println("xxx observePhotoContent(contentIdLiveData) from DetailViewFragment")
            if(items.isNotEmpty()) {
                detailViewAdapter.contentUids = items.reversed()
            }
        }
    }

    private fun observeProfiles() {
        profileVm.profilesLiveData.observe(viewLifecycleOwner) {
            println("xxx observeProfile() from DetailViewFragment")
            if(it.isNotEmpty()) {
                detailViewAdapter.profiles = it
                detailViewAdapter.notifyDataSetChanged()
            }
        }
    }

}