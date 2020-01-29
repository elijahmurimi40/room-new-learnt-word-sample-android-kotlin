package com.fortie40.newword.ui.words

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fortie40.newword.R
import com.fortie40.newword.databinding.WordLayoutBinding
import com.fortie40.newword.roomdatabase.WordModel

class WordAdapter: ListAdapter<WordModel, WordAdapter.WordViewHolder>(WordDiffCallBack()) {

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

    class WordViewHolder(private val binding: WordLayoutBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(wordModel: WordModel) {
            binding.wordM = wordModel
            binding.executePendingBindings()
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
}