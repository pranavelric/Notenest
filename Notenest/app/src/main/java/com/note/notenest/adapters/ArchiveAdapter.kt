package com.note.notenest.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.note.notenest.R
import com.note.notenest.data.models.ArchiveModel
import com.note.notenest.databinding.RecArchiveItemBinding

class ArchiveAdapter() :
    ListAdapter<ArchiveModel, ArchiveAdapter.MyViewHolder>(NoteItemDiffCallback()) {

    inner class MyViewHolder(private val binding: RecArchiveItemBinding ) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(note: ArchiveModel) {
            binding.setVariable(BR.archiveNoteData, note)
            binding.executePendingBindings()
            binding.noteCard.setOnClickListener {
                onItemClickListener?.let { click ->
                    click(note)
                }

            }
        }


    }


    class NoteItemDiffCallback : DiffUtil.ItemCallback<ArchiveModel>() {
        override fun areItemsTheSame(oldItem: ArchiveModel, newItem: ArchiveModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ArchiveModel, newItem: ArchiveModel): Boolean {
            return oldItem.id == newItem.id
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding:RecArchiveItemBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.rec_archive_item,
            parent,
            false
        )

        return MyViewHolder(binding)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        holder.bind(getItem(position))


    }


    private var onItemClickListener: ((ArchiveModel) -> Unit)? = null

    fun setOnItemClickListener(listener: (ArchiveModel) -> Unit) {
        onItemClickListener = listener
    }


}