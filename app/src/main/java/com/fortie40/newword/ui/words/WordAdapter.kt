package com.fortie40.newword.ui.words

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

    constructor(listener: WordItemClickListener, wordList: List<WordModel>): this() {
        clickHandler = listener
        wOriginalList = wordList
        wFilteredList = wordList
    }

    interface WordItemClickListener {
        fun viewDetails(clickedItemIndex: Int)
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
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(wordModel: WordModel) {
            binding.wordM = wordModel
            binding.executePendingBindings()
        }

        override fun onClick(p0: View?) {
            var aPosition = adapterPosition
            val word = wFilteredList[aPosition].wordLearned
            for (i in wOriginalList.indices) {
                if (word == wOriginalList[i].wordLearned) {
                    aPosition = i
                    break
                }
            }

            val viewDetails = p0?.findViewById<RelativeLayout>(R.id.view_details)
            when(p0) {
                viewDetails -> clickHandler.viewDetails(aPosition)
            }
        }

        init {
            val viewDetails = binding.viewDetails
            viewDetails.setOnClickListener(this)
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
}