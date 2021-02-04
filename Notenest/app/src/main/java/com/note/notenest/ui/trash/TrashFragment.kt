package com.note.notenest.ui.trash

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.note.notenest.R
import com.note.notenest.adapters.TrashAdapter
import com.note.notenest.databinding.FragmentTrashBinding
import com.note.notenest.ui.main.MainActivity
import com.note.notenest.utils.Constants
import com.note.notenest.utils.gone
import com.note.notenest.utils.hideSoftKeyboard
import com.note.notenest.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TrashFragment : Fragment() {


    lateinit var binding: FragmentTrashBinding

    @Inject
    lateinit var trashAdapter: TrashAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTrashBinding.inflate(inflater, container, false)

        setHasOptionsMenu(true)
        requireActivity().hideSoftKeyboard()
        setupRecyclerView()
        getData()
        setClickListeneres()


        return binding.root
    }


    private fun setClickListeneres() {

        trashAdapter.setOnItemClickListener { trashModel ->
            val bundle = Bundle()
            bundle.putParcelable(Constants.TRASH, trashModel)
            findNavController().navigate(R.id.action_trash_to_updateTrashFragment, bundle)

        }
    }


    private fun getData() {
        (activity as MainActivity).noteViewModel.getTrashData().observe(
            viewLifecycleOwner, {
                it?.let { data ->

                    if (data.isEmpty()) {
                        binding.emptyListBg.visible()

                    } else {
                        binding.emptyListBg.gone()

                    }

                    trashAdapter.submitList(data)


                }
            }
        )

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.home_menu, menu)
        menu.findItem(R.id.menu_main_overflow).setEnabled(true).isVisible = true
        menu.findItem(R.id.menu_main_delete_all).setEnabled(true).isVisible = true

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.menu_main_delete_all -> {
                context?.let {
                    (activity as MainActivity).noteViewModel.emptyDatabase(
                        it,
                        Constants.TRASH_EMPTY
                    )
                }
            }

        }

        return super.onOptionsItemSelected(item)
    }

    private fun setupRecyclerView() {

        binding.trashRec.apply {
            adapter = trashAdapter
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
        binding.trashRec.scheduleLayoutAnimation()


    }


}