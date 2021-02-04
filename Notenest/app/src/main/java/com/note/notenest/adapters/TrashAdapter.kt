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
import com.note.notenest.data.models.TrashModel
import com.note.notenest.databinding.RecArchiveItemBinding
import com.note.notenest.databinding.RecTrashItemBinding

class TrashAdapter() :
    ListAdapter<TrashModel, TrashAdapter.MyViewHolder>(NoteItemDiffCallback()) {

    inner class MyViewHolder(private val binding:RecTrashItemBinding ) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(note: TrashModel) {
            binding.setVariable(BR.trashNoteData, note)
            binding.executePendingBindings()
            binding.noteCard.setOnClickListener {
                onItemClickListener?.let { click ->
                    click(note)
                }

            }
        }


    }


    class NoteItemDiffCallback : DiffUtil.ItemCallback<TrashModel>() {
        override fun areItemsTheSame(oldItem: TrashModel, newItem: TrashModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: TrashModel, newItem: TrashModel): Boolean {
            return oldItem.id == newItem.id
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding: RecTrashItemBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.rec_trash_item,
            parent,
            false
        )

        return MyViewHolder(binding)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        holder.bind(getItem(position))


    }


    private var onItemClickListener: ((TrashModel) -> Unit)? = null

    fun setOnItemClickListener(listener: (TrashModel) -> Unit) {
        onItemClickListener = listener
    }


}