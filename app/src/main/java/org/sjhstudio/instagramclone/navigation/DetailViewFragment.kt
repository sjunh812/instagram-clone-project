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
import org.sjhstudio.instagramclone.model.PhotoContentDTO
import org.sjhstudio.instagramclone.viewmodel.PhotoContentViewModel

class DetailViewFragment: Fragment() {

    private lateinit var binding: FragmentDetailViewBinding
    private lateinit var detailViewAdapter: DetailViewAdapter

    private val vm: PhotoContentViewModel by activityViewModels()

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
    }

    private fun setUI() {
        detailViewAdapter = DetailViewAdapter(requireContext())
            .apply {
                setHasStableIds(true)
                setDetailViewAdapterCallback(object: DetailViewAdapterCallback {
                    override fun onClickFavorite(pos: Int) {
                        vm.contentIdLiveData.value?.reversed()?.let { value ->
                            vm.updateFavorite(value[pos])
                        }
                    }
                })
            }
        binding.detailRv.apply {
            adapter = detailViewAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        vm.getAll()
    }

    private fun observePhotoContent() {
        vm.contentLiveData.observe(viewLifecycleOwner) { items ->
            println("xxx observePhotoContent()")
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

}