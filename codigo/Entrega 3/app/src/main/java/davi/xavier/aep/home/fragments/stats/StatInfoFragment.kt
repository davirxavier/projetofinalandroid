package davi.xavier.aep.home.fragments.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import davi.xavier.aep.databinding.FragmentStatInfoBinding

class StatInfoFragment : Fragment() {
    private lateinit var binding: FragmentStatInfoBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentStatInfoBinding.inflate(layoutInflater)
        
        
        
        return binding.root
    }
}
