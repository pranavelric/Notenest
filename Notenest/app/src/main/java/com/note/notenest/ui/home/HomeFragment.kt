package com.note.notenest.ui.home

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.note.notenest.R
import com.note.notenest.databinding.FragmentHomeBinding
import com.note.notenest.utils.MySharedPrefrences
import com.note.notenest.utils.hideSoftKeyboard
import com.note.notenest.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

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

        setClickListeneres()

        return binding!!.root
    }

    private fun setClickListeneres() {
        binding?.fab?.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_createNoteFragment)
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
                context?.toast("sorty by title")
            }
            R.id.menu_main_sort_creation -> {
                context?.toast("sorty by creation")
            }
            R.id.menu_main_sort_color -> {
                context?.toast("sorty by color")
            }
            R.id.menu_main_delete_all -> {
                context?.toast("sorty by delete all")
            }

        }






        return super.onOptionsItemSelected(item)
    }


    private fun changeLayoutView(change: Boolean) {
        sharedPref.setNotesLayout(change)
        //  adapter.notifyDataSetChanged()
        // setupRecyclerView()
        requireActivity().invalidateOptionsMenu()
        //   invalidateOptionsMenu(requireActivity())
    }

    private fun searchTroughDatabase(query: String) {
//        noteViewModel.searchDatabase("%$query%").observe(this, { list ->
//            list?.let { adapter.setData(it) }
//        })

        context?.toast("Search function")
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }


}