package com.fortie40.newword.ui.words

import android.graphics.Color
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fortie40.newword.R
import com.fortie40.newword.databinding.WordLayoutBinding
import com.fortie40.newword.helperfunctions.HelperFunctions
import com.fortie40.newword.roomdatabase.WordModel


class WordAdapter(): ListAdapter<WordModel, WordAdapter.WordViewHolder>(WordDiffCallBack()),
    Filterable {

    private lateinit var wOriginalList: List<WordModel>
    private lateinit var wFilteredList: List<WordModel>
    private lateinit var clickHandler: WordItemClickListener
    private lateinit var selectedItems: SparseBooleanArray

    constructor(listener: WordItemClickListener, wordList: List<WordModel>): this() {
        clickHandler = listener
        wOriginalList = wordList
        wFilteredList = wordList
        selectedItems = SparseBooleanArray()
    }

    interface WordItemClickListener {
        fun onWordClicked(clickedItemIndex: Int)
        fun onWordLongClicked(clickedItemIndex: Int)
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

    inner class WordViewHolder(private val binding: WordLayoutBinding):
        RecyclerView.ViewHolder(binding.root), View.OnClickListener, View.OnLongClickListener {

        val viewDetails: RelativeLayout = binding.viewDetails

        fun bind(wordModel: WordModel) {
            binding.wordM = wordModel
            binding.executePendingBindings()
        }

        private fun wordAdapterPosition(): Int {
            var position = adapterPosition
            val word = wFilteredList[position].wordLearned
            for (i in wOriginalList.indices) {
                if (word == wOriginalList[i].wordLearned) {
                    position = i
                    break
                }
            }
            return position
        }

        override fun onClick(p0: View?) {
            val aPosition = wordAdapterPosition()

            when(p0) {
                viewDetails -> clickHandler.onWordClicked(aPosition)
            }
        }

        override fun onLongClick(p0: View?): Boolean {
            val aPosition = wordAdapterPosition()

            when(p0) {
                viewDetails -> clickHandler.onWordLongClicked(aPosition)
            }
            return true
        }

        init {
            val viewDetails = binding.viewDetails
            viewDetails.setOnClickListener(this)
            viewDetails.setOnLongClickListener(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val wordLayoutBinding: WordLayoutBinding = DataBindingUtil.inflate(layoutInflater,
            R.layout.word_layout, parent, false)
        return WordViewHolder(wordLayoutBinding)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.bind(getItem(position))
        when(isSelected(position)) {
            true -> holder.viewDetails.setBackgroundColor(Color.LTGRAY)
            else -> holder.viewDetails.setBackgroundColor(Color.WHITE)
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