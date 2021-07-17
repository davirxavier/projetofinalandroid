package davi.xavier.aep.home.ui.stats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import davi.xavier.aep.R
import davi.xavier.aep.databinding.StatsItemBinding
import java.time.format.DateTimeFormatter

class StatsListAdapter : RecyclerView.Adapter<StatsListAdapter.StatsViewHolder>() {
    
    class StatsViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = StatsItemBinding.bind(view)
        
        val periodoText = binding.periodoText
        val dataText = binding.dataText
        val distanciaText = binding.distanciaText
        val caloriasText = binding.caloriasText
    }

    private var items: MutableList<StatsViewObject> = mutableListOf()

    fun add(item: StatsViewObject) {
        items.add(item)
        notifyItemInserted(items.size)
    }

    fun delete(i: Int) {
        if (i < items.size && i >= 0)
        {
            items.removeAt(i)
            notifyItemRemoved(i)
            notifyItemRangeChanged(i, items.size)
        }
    }

    fun setAll(new: List<StatsViewObject>) {
//        val result = DiffUtil.calculateDiff(StringDiffChecker(this.items, new))
        items.clear()
        items.addAll(new)

//        result.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.stats_item, parent, false)

        return StatsViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatsViewHolder, position: Int) {
        val item = items[position]
        
        val context = holder.itemView.context
        holder.periodoText.text = context.getString(R.string.periodo, item.periodStartHour, item.periodEndHour)
        holder.dataText.text = item.date.format(DateTimeFormatter.ofPattern(context.getString(R.string.date_format)))
        holder.distanciaText.text = context.getString(R.string.distancia_curta, item.distance)
        holder.caloriasText.text = context.getString(R.string.calorias_curta, item.calories)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
