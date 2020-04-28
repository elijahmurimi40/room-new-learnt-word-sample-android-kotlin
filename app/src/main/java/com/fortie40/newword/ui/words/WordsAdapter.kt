package com.fortie40.newword.ui.words

import android.view.LayoutInflater
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


class WordsAdapter(): ListAdapter<WordModel, WordsAdapter.WordViewHolder>(WordDiffCallBack()),
    Filterable {

    private lateinit var wOriginalList: List<WordModel>
    private lateinit var wFilteredList: List<WordModel>
    private lateinit var clickHandler: IClickListener

    constructor(listener: IClickListener, wordList: List<WordModel>): this() {
        clickHandler = listener
        wOriginalList = wordList
        wFilteredList = wordList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val wordLayoutBinding: WordLayoutBinding = DataBindingUtil.inflate(layoutInflater,
            R.layout.word_layout, parent, false)
        return WordViewHolder(wordLayoutBinding)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        // val context = holder.binding.viewDetails.context
        holder.bind(getItem(position))

        holder.binding.iClickListener = clickHandler
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
        RecyclerView.ViewHolder(binding.root) {

        fun bind(wordModel: WordModel) {
            binding.wordM = wordModel
            binding.executePendingBindings()
        }
    }

    private val filter = object : Filter() {
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

    override fun getFilter(): Filter {
        return this.filter
    }
}