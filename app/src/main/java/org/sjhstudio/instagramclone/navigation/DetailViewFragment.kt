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
        setUI()
        observePhotoContent()
        observeProfiles()
    }

    private fun setUI() {
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

        photoContentVm.getAll()
        profileVm.getAll()
    }

    private fun observePhotoContent() {
        photoContentVm.contentLiveData.observe(viewLifecycleOwner) { items ->
            println("xxx observePhotoContent() from DetailViewFragment")
            if(items.isNotEmpty()) {
                val uIds = arrayListOf<String>()

                for(item in items) {
                    item.uid?.let { uIds.add(it) }
                }

                detailViewAdapter.contents = items.reversed()
                detailViewAdapter.notifyDataSetChanged()
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