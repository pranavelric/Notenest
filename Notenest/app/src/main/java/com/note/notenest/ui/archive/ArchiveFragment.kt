package com.note.notenest.ui.archive

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.note.notenest.R
import com.note.notenest.adapters.ArchiveAdapter
import com.note.notenest.data.models.NoteModel
import com.note.notenest.databinding.FragmentArchiveBinding
import com.note.notenest.ui.main.MainActivity
import com.note.notenest.utils.Constants
import com.note.notenest.utils.MySharedPrefrences
import com.note.notenest.utils.hideSoftKeyboard
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ArchiveFragment : Fragment() {


    lateinit var binding: FragmentArchiveBinding

    @Inject
    lateinit var archiveAdapter: ArchiveAdapter


    @Inject
    lateinit var sharedPref: MySharedPrefrences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentArchiveBinding.inflate(inflater, container, false)

        setHasOptionsMenu(true)
        requireActivity().hideSoftKeyboard()
        setupRecyclerView()
        getData()
        setClickListeneres()
        return binding.root

    }

    private fun setClickListeneres() {

        archiveAdapter.setOnItemClickListener { archiveModel ->
        //val bundle = Bundle()

          findNavController().navigate(R.id.action_archive_to_updateArchiveFragment)

        }
    }

//    private fun setRecyclerView() {
//
//        binding.archiveRec.apply {
//            adapter = archiveAdapter
//          //  layoutManager=LinearLayoutManager(context)
//        }
//
//    }

    private fun getData() {
        (activity as MainActivity).noteViewModel.getArchiveItem().observe(
            viewLifecycleOwner, {
                it?.let { data ->
                    archiveAdapter.submitList(data)
                }
            }
        )

    }




    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.home_menu, menu)
        menu.findItem(R.id.menu_main_search).setEnabled(true).isVisible = true
        menu.findItem(R.id.menu_main_list).setEnabled(true).isVisible = true
        menu.findItem(R.id.menu_main_grid).setEnabled(true).isVisible = true
        menu.findItem(R.id.menu_main_overflow).setEnabled(true).isVisible = true
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
        when (sharedPref.getArchiveLayout()) {
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

            R.id.menu_main_delete_all -> {
                context?.let {   (activity as MainActivity).noteViewModel.emptyDatabase(it, Constants.ARCHIVE_EMPTY) }
            }

        }

        return super.onOptionsItemSelected(item)
    }
    private fun changeLayoutView(change: Boolean) {
        sharedPref.setArchiveLayout(change)
       archiveAdapter.notifyDataSetChanged()

        setupRecyclerView()
        requireActivity().invalidateOptionsMenu()

    }
    private fun setupRecyclerView() {
        when (sharedPref.getArchiveLayout()) {
            true ->   binding.archiveRec.layoutManager = LinearLayoutManager(context)
            false ->   binding.archiveRec.layoutManager =  StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
        binding.archiveRec.apply {
            adapter = archiveAdapter
         //   layoutManager=LinearLayoutManager(context)
        }
        binding.archiveRec.scheduleLayoutAnimation()




    }
    private fun searchTroughDatabase(query: String) {
        (activity as MainActivity).noteViewModel.searchArchiveDatabase(query).observe(this, { list ->
            list?.let { archiveAdapter.submitList(it) }
        })

    }













}