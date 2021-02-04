package com.note.notenest.ui.home

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.note.notenest.R
import com.note.notenest.adapters.NoteAdapter
import com.note.notenest.data.models.ArchiveModel
import com.note.notenest.data.models.NoteModel
import com.note.notenest.data.models.TrashModel
import com.note.notenest.databinding.FragmentHomeBinding
import com.note.notenest.utils.*
import com.note.notenest.utils.Constants.NOTE_TO_ARCHIVE
import com.note.notenest.utils.Constants.NOTE_TO_TRASH
import com.note.notenest.utils.Constants.SWIPE_ARCHIVE
import com.note.notenest.utils.Constants.SWIPE_DELETE
import com.note.notenest.viewModels.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {


    private val noteViewModel: NoteViewModel by lazy {
        ViewModelProvider(this)[NoteViewModel::class.java]
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @Inject
    lateinit var noteAdapter: NoteAdapter


    @Inject
    lateinit var sharedPref: MySharedPrefrences


    var binding: FragmentHomeBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding!!.lifecycleOwner = this

        setHasOptionsMenu(true)
        requireActivity().hideSoftKeyboard()
        getNoteData()
        setupRecyclerView()
        setClickListeneres()

        return binding!!.root
    }

    private fun getNoteData() {

        noteViewModel.getNoteList().observe(viewLifecycleOwner, {
            it?.let { data ->


                if (data.isEmpty()) {
                    binding?.emptyListBg?.visible()

                } else {
                    binding?.emptyListBg?.gone()

                }


                noteAdapter.submitList(data)

            }


        })


    }

    private fun setClickListeneres() {
        binding?.fab?.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_createNoteFragment)
        }

        noteAdapter.setOnItemClickListener { note ->
            val bundle = Bundle()
            bundle.putParcelable(Constants.NOTE, note)
            findNavController().navigate(R.id.action_home_to_updateNoteFragment, bundle)
        }

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.home_menu, menu)
        menu.findItem(R.id.menu_main_search).setEnabled(true).isVisible = true
        menu.findItem(R.id.menu_main_list).setEnabled(true).isVisible = true
        menu.findItem(R.id.menu_main_grid).setEnabled(true).isVisible = true
        menu.findItem(R.id.menu_main_overflow).setEnabled(true).isVisible = true
        menu.findItem(R.id.menu_main_sort_by).setEnabled(true).isVisible = true
        menu.findItem(R.id.menu_main_delete_all).setEnabled(true).isVisible = true

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val search = menu.findItem(R.id.menu_main_search).actionView as? SearchView
        val list = menu.findItem(R.id.menu_main_list)
        val grid = menu.findItem(R.id.menu_main_grid)

        // Set search function.
        search?.isSubmitButtonEnabled = true
        search?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) searchTroughDatabase(query)

                requireActivity().hideSoftKeyboard()

                return true

            }

            override fun onQueryTextChange(query: String?): Boolean {
                if (query != null) searchTroughDatabase(query)
                return true
            }

        })

        // Change option when change recyclerview layout.
        when (sharedPref.getNotesLayout()) {
            true -> {
                list.setEnabled(false).isVisible = false
                grid.setEnabled(true).isVisible = true
            }
            false -> {
                list.setEnabled(true).isVisible = true
                grid.setEnabled(false).isVisible = false
            }
        }

        return super.onPrepareOptionsMenu(menu)


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.menu_main_list -> changeLayoutView(true)
            R.id.menu_main_grid -> changeLayoutView(false)
            R.id.menu_main_sort_title -> {
                sortRecyclerView(Constants.SORT_BY_TITLE)
            }
            R.id.menu_main_sort_creation -> {
                sortRecyclerView(Constants.SORT_BY_CREATION)
            }
            R.id.menu_main_sort_color -> {
                sortRecyclerView(Constants.SORT_BY_COLOR)
            }
            R.id.menu_main_delete_all -> {
                //   noteViewModel.deleteAllNotesItem()

                context?.let { noteViewModel.emptyDatabase(it, Constants.NOTE_EMPTY) }

            }

        }

        return super.onOptionsItemSelected(item)
    }

    private fun sortRecyclerView(sortBy: String) {


        binding?.notesRec?.scheduleLayoutAnimation()

        getSortingMehod(sortBy).observe(viewLifecycleOwner, {
            it?.let { data ->
                noteAdapter.submitList(data)
            }

        })


    }


    fun getSortingMehod(sortBy: String): LiveData<List<NoteModel>> {
        return when (sortBy) {
            Constants.SORT_BY_TITLE -> noteViewModel.getNoteListSortedByTitle()
            Constants.SORT_BY_COLOR -> noteViewModel.getNoteListSortedByColor()
            Constants.SORT_BY_CREATION -> noteViewModel.getNoteListSortedByCreation()
            else -> noteViewModel.getNoteListSortedByTitle()
        }

    }

    private fun changeLayoutView(change: Boolean) {
        sharedPref.setNotesLayout(change)
        noteAdapter.notifyDataSetChanged()

        setupRecyclerView()
        requireActivity().invalidateOptionsMenu()

    }

    private fun setupRecyclerView() {
        when (sharedPref.getNotesLayout()) {
            true -> binding?.notesRec?.layoutManager = LinearLayoutManager(context)
            false -> binding?.notesRec?.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
        binding?.notesRec?.apply {
            adapter = noteAdapter

        }
        binding?.notesRec?.scheduleLayoutAnimation()


        swipeToDeleteOrArchive(Constants.SWIPE_DELETE)
        swipeToDeleteOrArchive(Constants.SWIPE_ARCHIVE)


    }

    private fun swipeToDeleteOrArchive(action: Int) {
        lateinit var background: Drawable
        lateinit var icon: Drawable

        when (action) {
            SWIPE_DELETE -> {
                background =
                    ResourcesCompat.getDrawable(resources, R.drawable.bg_swipe_delete, null)!!
                icon =
                    ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_delete_24, null)!!
            }
            SWIPE_ARCHIVE -> {
                background =
                    ResourcesCompat.getDrawable(resources, R.drawable.bg_swipe_archive, null)!!
                icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_archive, null)!!
            }
        }

        val swipeToDeleteCallBack = object : SwipeItem(action, background, icon, requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val noteItem = noteAdapter.currentList[viewHolder.adapterPosition]
                val archiveItem = ArchiveModel(
                    noteAdapter.currentList[viewHolder.adapterPosition].id,
                    noteAdapter.currentList[viewHolder.adapterPosition].title,
                    noteAdapter.currentList[viewHolder.adapterPosition].description,

                    noteAdapter.currentList[viewHolder.adapterPosition].color,

                    noteAdapter.currentList[viewHolder.adapterPosition].sketches,
                    noteAdapter.currentList[viewHolder.adapterPosition].files,
                    noteAdapter.currentList[viewHolder.adapterPosition].lastModified


                )
                val trashItem = TrashModel(
                    noteAdapter.currentList[viewHolder.adapterPosition].id,
                    noteAdapter.currentList[viewHolder.adapterPosition].title,
                    noteAdapter.currentList[viewHolder.adapterPosition].description,
                    noteAdapter.currentList[viewHolder.adapterPosition].color,
                    noteAdapter.currentList[viewHolder.adapterPosition].sketches,
                    noteAdapter.currentList[viewHolder.adapterPosition].files,
                    noteAdapter.currentList[viewHolder.adapterPosition].lastModified
                )


                when (action) {
                    SWIPE_ARCHIVE -> noteViewModel.moveItem(
                        NOTE_TO_ARCHIVE,
                        noteItem,
                        archiveItem,
                        trashItem,
                        requireView()
                    )
                    SWIPE_DELETE -> noteViewModel.moveItem(
                        NOTE_TO_TRASH,
                        noteItem,
                        archiveItem,
                        trashItem,
                        requireView()
                    )
                }


                val temp = viewHolder.adapterPosition
                val list = noteAdapter.currentList
                val tempList = list.toMutableList()
                noteAdapter.submitList(tempList)
                tempList.removeAt(temp)
            }


        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallBack)
        itemTouchHelper.attachToRecyclerView(binding?.notesRec)


    }

    private fun searchTroughDatabase(query: String) {
        noteViewModel.searchDatabase(query).observe(this, { list ->
            list?.let { noteAdapter.submitList(it) }
        })

    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }


}