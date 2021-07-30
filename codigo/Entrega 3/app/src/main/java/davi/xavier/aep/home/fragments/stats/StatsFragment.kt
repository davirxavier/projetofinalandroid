package davi.xavier.aep.home.fragments.stats

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import davi.xavier.aep.AepApplication
import davi.xavier.aep.data.StatsViewModel
import davi.xavier.aep.databinding.FragmentStatsBinding
import kotlinx.coroutines.launch

class StatsFragment : Fragment() {
    private lateinit var binding: FragmentStatsBinding
    private val viewModel: StatsViewModel by activityViewModels {
        StatsViewModel.StatsViewModelFactory(
            (activity?.application as AepApplication).statRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStatsBinding.inflate(layoutInflater)

        val adapter = StatsListAdapter()
        binding.statsList.adapter = adapter
        binding.statsList.layoutManager = LinearLayoutManager(requireContext())
        
        viewModel.getStats().observe(viewLifecycleOwner, {
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        })
        
        return binding.root
    }

} 
