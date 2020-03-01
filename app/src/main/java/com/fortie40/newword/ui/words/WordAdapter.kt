package com.fortie40.newword.ui.words

import android.graphics.Color
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fortie40.newword.R
import com.fortie40.newword.databinding.WordLayoutBinding
import com.fortie40.newword.helperclasses.HelperFunctions
import com.fortie40.newword.interfaces.IClickListener
import com.fortie40.newword.roomdatabase.WordModel


class WordAdapter(): ListAdapter<WordModel, WordAdapter.WordViewHolder>(WordDiffCallBack()),
    Filterable {

    private lateinit var wOriginalList: List<WordModel>
    private lateinit var wFilteredList: List<WordModel>
    private lateinit var clickHandler: IClickListener
    private lateinit var selectedItems: SparseBooleanArray

    constructor(listener: IClickListener, wordList: List<WordModel>): this() {
        clickHandler = listener
        wOriginalList = wordList
        wFilteredList = wordList
        selectedItems = SparseBooleanArray()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val wordLayoutBinding: WordLayoutBinding = DataBindingUtil.inflate(layoutInflater,
            R.layout.word_layout, parent, false)
        return WordViewHolder(wordLayoutBinding)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.bind(getItem(position))
        val context = holder.binding.viewDetails.context
        holder.binding.iClickListener = clickHandler

        when(isSelected(position)) {
            true -> {
                holder.binding.viewDetails.setBackgroundColor(Color.LTGRAY)
                holder.binding.icon.text = ""
                holder.binding.icon.background = context.getDrawable(R.drawable.circle_icon)
            }
            else -> holder.binding.viewDetails.setBackgroundColor(Color.WHITE)
        }
    }

    class WordDiffCallBack: DiffUtil.ItemCallback<WordModel>() {
        override fun areItemsTheSame(oldItem: WordModel, newItem: WordModel): Boolean {
            return oldItem.wordId == newItem.wordId
        }

        override fun areContentsTheSame(oldItem: WordModel, newItem: WordModel): Boolean {
            return oldItem.wordLearned == newItem.wordLearned &&
                    oldItem.language == newItem.language &&
                    oldItem.meaning == newItem.meaning
        }
    }

    inner class WordViewHolder(val binding: WordLayoutBinding):
        RecyclerView.ViewHolder(binding.root), View.OnLongClickListener {

        fun bind(wordModel: WordModel) {
            binding.wordM = wordModel
            binding.executePendingBindings()
        }

        override fun onLongClick(p0: View?): Boolean {
            when(p0) {
                binding.viewDetails -> clickHandler.onWordLongClicked(adapterPosition)
            }
            return true
        }

        init {
            binding.viewDetails.setOnLongClickListener(this)
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val charString = p0.toString()

                wFilteredList = if (charString.isEmpty()) {
                    wOriginalList
                } else {
                    val filteredList = wOriginalList
                        .filter { HelperFunctions.toLowerCase(it.wordLearned).contains(charString) ||
                                HelperFunctions.toLowerCase(it.language).contains(charString) ||
                                HelperFunctions.toLowerCase(it.meaning).contains(charString) }
                        .toMutableList()
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = wFilteredList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                submitList(p1?.values as List<WordModel>)
            }
        }
    }

    private fun getSelectedItems(): ArrayList<Int> {
        val items: ArrayList<Int> = ArrayList(selectedItems.size())
        for (i in 0 until selectedItems.size()) {
            items.add(selectedItems.keyAt(i))
        }
        return items
    }

    fun getSelectedItemCount(): Int {
        return getSelectedItems().size
    }

    fun toggleSelection(position: Int) {
        if (selectedItems.get(position, false)) {
            selectedItems.delete(position)
        } else {
            selectedItems.put(position, true)
        }
        notifyItemChanged(position)
    }

    private fun isSelected(position: Int): Boolean {
        return getSelectedItems().contains(position)
    }

    fun clearSelection() {
        val selection:List<Int> = getSelectedItems()
        selectedItems.clear()
        for (i in selection) {
            notifyItemChanged(i)
        }
    }
}