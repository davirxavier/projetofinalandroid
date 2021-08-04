package davi.xavier.aep.home.fragments.stats

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import davi.xavier.aep.AepApplication
import davi.xavier.aep.R
import davi.xavier.aep.data.StatsViewModel
import davi.xavier.aep.data.entities.StatEntry
import davi.xavier.aep.databinding.FragmentStatsBinding

class StatsFragment : Fragment() {
    private lateinit var binding: FragmentStatsBinding
    private lateinit var navController: NavController
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
        val navFrag = requireActivity().supportFragmentManager.findFragmentById(R.id.home_nav_host) as NavHostFragment
        navController = navFrag.navController

        val adapter = StatsListAdapter()
        adapter.itemTouchedCallback = { onStatTouched(it) }
        
        binding.statsList.adapter = adapter
        binding.statsList.layoutManager = LinearLayoutManager(requireContext())
        
        viewModel.getStats().observe(viewLifecycleOwner, {
            adapter.submitList(it.reversed())
            adapter.notifyDataSetChanged()
        })
        
        return binding.root
    }

    private fun onStatTouched(item: StatEntry) {
        item.uid?.let { navController.navigate(StatsFragmentDirections.actionNavStatsToStatInfoFragment(it)) }
    }
} 
