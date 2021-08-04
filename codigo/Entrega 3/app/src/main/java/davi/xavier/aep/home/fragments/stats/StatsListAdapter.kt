package davi.xavier.aep.home.fragments.stats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import davi.xavier.aep.R
import davi.xavier.aep.data.entities.StatEntry
import davi.xavier.aep.databinding.StatsItemBinding
import java.time.format.DateTimeFormatter

class StatsListAdapter : ListAdapter<StatEntry, StatsListAdapter.StatsViewHolder>(callback) {
    
    var itemTouchedCallback: ((item: StatEntry) -> Unit)? = null
    
    companion object {
        private val callback = object : DiffUtil.ItemCallback<StatEntry>() {
            override fun areItemsTheSame(oldItem: StatEntry, newItem: StatEntry): Boolean {
                return oldItem.uid == newItem.uid
            }

            override fun areContentsTheSame(oldItem: StatEntry, newItem: StatEntry): Boolean {
                return oldItem.uid == newItem.uid
                        && oldItem.startTime == newItem.startTime
                        && oldItem.endTime == newItem.endTime
                        && oldItem.distance == newItem.distance
                        && oldItem.calories == newItem.calories
            }
        }
    }
    
    class StatsViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = StatsItemBinding.bind(view)
        
        val periodoText = binding.periodoText
        val dataText = binding.dataText
        val distanciaText = binding.distanciaText
        val caloriasText = binding.caloriasText
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.stats_item, parent, false)

        return StatsViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatsViewHolder, position: Int) {
        val item = getItem(position)
        
        val context = holder.itemView.context
        
        val start = item.startTime?.format(DateTimeFormatter.ofPattern(context.getString(R.string.hour_format)))
        val end = item.endTime?.format(DateTimeFormatter.ofPattern(context.getString(R.string.hour_format)))
        
        if (end != null) {
            holder.periodoText.text = context.getString(R.string.periodo, start, end)
        } else {
            holder.periodoText.text = context.getString(R.string.periodo_short, start)
        }
        
        holder.dataText.text = item.startTime?.format(DateTimeFormatter.ofPattern(context.getString(R.string.date_format)))
        holder.distanciaText.text = context.getString(R.string.distancia_curta, String.format("%.2f", item.distance?.toFloat() ?: 0f))
        holder.caloriasText.text = context.getString(R.string.calorias_curta, if (item.calories != null) item.calories else 0)
        
        holder.itemView.setOnClickListener {
            itemTouchedCallback?.let { it(item) }
        }
    }
}
