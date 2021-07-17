package davi.xavier.aep.home.ui.stats

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import davi.xavier.aep.data.StatsViewModel
import davi.xavier.aep.databinding.FragmentStatsBinding

class StatsFragment : Fragment() {
    private lateinit var binding: FragmentStatsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStatsBinding.inflate(layoutInflater)

        val adapter = StatsListAdapter()
        binding.statsList.adapter = adapter
        binding.statsList.layoutManager = LinearLayoutManager(requireContext())
        
        val model: StatsViewModel by viewModels()
        model.getStats().observe(viewLifecycleOwner, {
            adapter.setAll(it)
        })
        
        return binding.root
    }

} 
