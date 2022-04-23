package org.sjhstudio.instagramclone.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import org.sjhstudio.instagramclone.MyApplication.Companion.userUid
import org.sjhstudio.instagramclone.adapter.AlarmAdapter
import org.sjhstudio.instagramclone.databinding.FragmentAlarmBinding
import org.sjhstudio.instagramclone.model.AlarmDTO
import org.sjhstudio.instagramclone.viewmodel.AlarmViewModel
import org.sjhstudio.instagramclone.viewmodel.ProfileViewModel

class AlarmFragment: Fragment() {

    private lateinit var binding: FragmentAlarmBinding
    private val alarmsVm by activityViewModels<AlarmViewModel>()
    private lateinit var profileVm: ProfileViewModel
    private lateinit var alarmAdapter: AlarmAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlarmBinding.inflate(inflater, container, false)
        profileVm = ViewModelProvider(requireActivity())[ProfileViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        alarmAdapter = AlarmAdapter(requireContext())
            .apply {
                setHasStableIds(true)
            }
        binding.alarmRv.apply {
            adapter = alarmAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        userUid?.let { alarmsVm.getAll(it) }
        observeAlarms()
        observeProfile()
    }

    fun observeAlarms() {
        alarmsVm.alarmsLiveData.observe(viewLifecycleOwner) { list ->
            println("xxx observeAlarms() from AlarmFragment")
            if(list.isNotEmpty()) {
                // 알림에 맞는 프로필이미지 요청
                for(item in list) {
                    profileVm.getAllWhereUid(item.uid!!)
                }
                alarmAdapter.alarms = list as ArrayList<AlarmDTO>
                alarmAdapter.notifyDataSetChanged()
            }
        }
    }

    fun observeProfile() {
        profileVm.profileLiveData.observe(viewLifecycleOwner) {
            println("xxx observeProfile() from AlarmFragment")
            alarmAdapter.profile = it
            alarmAdapter.notifyDataSetChanged()
        }
    }

}