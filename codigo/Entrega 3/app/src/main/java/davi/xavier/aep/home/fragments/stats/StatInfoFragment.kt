package davi.xavier.aep.home.fragments.stats

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import davi.xavier.aep.AepApplication
import davi.xavier.aep.R
import davi.xavier.aep.data.StatsViewModel
import davi.xavier.aep.data.entities.StatEntry
import davi.xavier.aep.databinding.FragmentStatInfoBinding
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class StatInfoFragment : Fragment() {
    private lateinit var binding: FragmentStatInfoBinding
    private val viewModel: StatsViewModel by activityViewModels {
        StatsViewModel.StatsViewModelFactory(
            (activity?.application as AepApplication).statRepository
        )
    }
    private lateinit var navController: NavController
    val args: StatInfoFragmentArgs by navArgs()
    var currentStat: StatEntry? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentStatInfoBinding.inflate(layoutInflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navFrag = requireActivity().supportFragmentManager.findFragmentById(R.id.home_nav_host) as NavHostFragment
        navController = navFrag.navController
        
        val hourFormatter = DateTimeFormatter.ofPattern(getString(R.string.hour_format))
        val dateFormatter = DateTimeFormatter.ofPattern(getString(R.string.date_format))
        
        binding.buttonSave.setOnClickListener { onUpdate() }
        binding.deleteButton.setOnClickListener { onDelete() }
        binding.routeButton.setOnClickListener { showRoute() }
        
        viewModel.getStat(args.statUid).observe(viewLifecycleOwner) {
            it?.let { stat ->
                currentStat = stat
                
                var startTime: String? = null
                stat.startTime?.let { time ->
                    binding.textDate.text = time.format(dateFormatter)
                    startTime = time.format(hourFormatter)
                }
                
                stat.endTime?.let { endTime ->
                    binding.textPeriod.text = getString(R.string.periodo, startTime, endTime.format(hourFormatter))
                    
                    stat.startTime?.let { startTime ->
                        binding.textTime.visibility = View.VISIBLE
                        binding.textTime.text = getString(R.string.time_spent, 
                            LocalTime.MIDNIGHT.plus(Duration.between(startTime, endTime)).format(DateTimeFormatter.ISO_TIME))
                    }
                } ?: run { 
                    binding.textPeriod.text = getString(R.string.periodo_short, startTime)
                    binding.textTime.visibility = View.INVISIBLE
                }
                
                binding.textCalories.text = getString(R.string.calorias_no_break, stat.calories ?: 0)
                binding.textDistance.text = getString(R.string.distancia_no_break, String.format("%.2f", stat.distance?.toFloat() ?: 0f))
                binding.textObsField.setText(stat.obs ?: "")
                binding.routeButton.visibility = if (it.endTime != null) View.VISIBLE else View.INVISIBLE
            }
        }
    }
    
    fun onUpdate() {
        lifecycleScope.launch {
            currentStat?.let {
                val obs = binding.textObsField.text.toString()
                try {
                    viewModel.updateStat(it.distance, it.calories, obs, args.statUid)
                    Toast.makeText(requireContext(), R.string.save_success, Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), R.string.unknown_error, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    fun onDelete() {
        currentStat?.let {
            val builder = AlertDialog.Builder(requireContext())

            builder.setMessage(R.string.delete_confirm)
                .setCancelable(false)
                .setPositiveButton(R.string.yes) { _, _ ->
                    lifecycleScope.launch {
                        try {
                            viewModel.removeStat(args.statUid)
                            navigateBack()
                            Toast.makeText(requireContext(), R.string.save_success, Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), R.string.unknown_error, Toast.LENGTH_LONG).show()
                        }
                    }
                }
                .setNegativeButton(R.string.no) { dialog, _ ->
                    dialog.dismiss()
                }

            val alert = builder.create()
            alert.show()
        }
    }
    
    private fun navigateBack() {
        navController.navigateUp()
    }
    
    private fun showRoute() {
        navController.navigate(StatInfoFragmentDirections.actionStatInfoFragmentToNavHome(args.statUid))
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.getItem(0).isVisible = false
    }
}
