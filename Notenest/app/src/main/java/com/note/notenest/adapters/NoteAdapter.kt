package com.note.notenest.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.note.notenest.R

import com.note.notenest.data.models.NoteModel
import com.note.notenest.databinding.RecNoteItemBinding

class NoteAdapter() :
    ListAdapter<NoteModel, NoteAdapter.MyViewHolder>(NoteItemDiffCallback()) {

    inner class MyViewHolder(private val binding: RecNoteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(note: NoteModel) {
            binding.setVariable(BR.noteData, note)
            binding.executePendingBindings()
            binding.noteCard.setOnClickListener {
                onItemClickListener?.let { click ->
                    click(note)
                }

            }
        }


    }


    class NoteItemDiffCallback : DiffUtil.ItemCallback<NoteModel>() {
        override fun areItemsTheSame(oldItem: NoteModel, newItem: NoteModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: NoteModel, newItem: NoteModel): Boolean {
            return oldItem.id == newItem.id
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding: RecNoteItemBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.rec_note_item,
            parent,
            false
        )

        return MyViewHolder(binding)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        holder.bind(getItem(position))


    }


    private var onItemClickListener: ((NoteModel) -> Unit)? = null

    fun setOnItemClickListener(listener: (NoteModel) -> Unit) {
        onItemClickListener = listener
    }


}