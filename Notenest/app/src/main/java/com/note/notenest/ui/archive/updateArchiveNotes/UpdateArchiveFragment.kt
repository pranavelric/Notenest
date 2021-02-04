package com.note.notenest.ui.archive.updateArchiveNotes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.note.notenest.R
import com.note.notenest.adapters.ArchiveAdapter
import com.note.notenest.databinding.FragmentUpdateArchiveBinding
import com.note.notenest.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UpdateArchiveFragment : Fragment() {


    lateinit var binding: FragmentUpdateArchiveBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentUpdateArchiveBinding.inflate(inflater, container, false)

        return binding.root
    }



}