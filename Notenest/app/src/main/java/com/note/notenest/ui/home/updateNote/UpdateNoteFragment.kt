package com.note.notenest.ui.home.updateNote

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat

import androidx.navigation.fragment.findNavController
import com.note.notenest.R
import com.note.notenest.data.models.ArchiveModel
import com.note.notenest.data.models.NoteModel
import com.note.notenest.data.models.TrashModel
import com.note.notenest.databinding.FragmentUpdateNoteBinding
import com.note.notenest.ui.main.MainActivity
import com.note.notenest.utils.*
import com.note.notenest.utils.Constants.NOTE_TO_ARCHIVE
import com.note.notenest.utils.Constants.NOTE_TO_TRASH
import dagger.hilt.android.AndroidEntryPoint
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class UpdateNoteFragment : Fragment() {


    lateinit var note: NoteModel
    lateinit var binding: FragmentUpdateNoteBinding
    private lateinit var currentItem: NoteModel
    private lateinit var noteItem: NoteModel
    private lateinit var archiveItem: ArchiveModel
    private lateinit var trashItem: TrashModel
    var selectImagePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            note = it.getParcelable(Constants.NOTE)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUpdateNoteBinding.inflate(inflater, container, false)
        binding.noteUpdate = note
        setHasOptionsMenu(true)
        setClickListeners()
        handleBackPressed()
        setData()
        setItems()
        return binding.root
    }

    private fun setClickListeners() {
        binding.addMoreFab.setOnClickListener {

            createDialog()
        }


    }

    private fun setItems() {
        noteItem = NoteModel(
            note.id,
            note.title,
            note.description,
            note.color,
            note.sketches,
            note.files,
            note.lastModified
        )
        archiveItem = ArchiveModel(
            note.id,
            note.title,
            note.description,
            note.color,
            note.sketches,
            note.files,
            note.lastModified
        )
        trashItem = TrashModel(
            note.id,
            note.title,
            note.description,
            note.color,
            note.sketches,
            note.files,
            note.lastModified
        )
    }


    private fun setData() {
        binding.lastModified.text = "Last modified : " + SimpleDateFormat(
            "dd MMMM yyyy",
            Locale.getDefault()
        ).format(Date())


        if (note != null && note.files != null) {
            selectImagePath = note.files
        }


    }


    private fun handleBackPressed() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                    currentItem = NoteModel(
                        note.id,
                        binding.editTextTitle.text.toString(),
                        binding.editTextBody.text.toString(),
                        (binding.root.background as ColorDrawable).color,
                        null,
                        selectImagePath,
                        binding.lastModified.text.toString()
                    )


                    if (currentItem.title != "" || currentItem.description != "") {

                        context?.discardDialog(Constants.NOTE_ADD_DISCARD, binding.root)

                    } else {
                        findNavController().navigateUp()
                    }


                }

            })
    }

    private fun createDialog() {


        val builder = context?.let { AlertDialog.Builder(it) }
        val viewGroup: ViewGroup = activity?.findViewById(android.R.id.content)!!
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.add_more_dialog_layout, viewGroup, false)


        builder?.setView(dialogView)
        val dialog = builder?.create()
        dialog?.show()


        dialogView.findViewById<TextView>(R.id.files).setOnClickListener {

            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.REQUEST_PERMISSION
                )
            } else {
                selectImage();
            }


            dialog?.dismiss()

        }

        dialogView.findViewById<TextView>(R.id.sketch).setOnClickListener {
            context?.toast("Sketch")
            dialog?.dismiss()

        }


    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (context?.packageManager?.let { intent.resolveActivity(it) } != null) {
            startActivityForResult(intent, Constants.REQUEST_SELECT)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {


        if (requestCode == Constants.REQUEST_PERMISSION && grantResults.size > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {

                context?.toast("Permission Denied, Please allow permission to add image")
            }
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode === Constants.REQUEST_SELECT && resultCode === Activity.RESULT_OK) {
            if (data != null) {
                val selectImgUri: Uri? = data.data
                if (selectImgUri != null) {
                    try {
                        val inputStream: InputStream =
                            context?.contentResolver?.openInputStream(selectImgUri)!!
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        binding.imageNote.setImageBitmap(bitmap)
                        binding.imageNote.setVisibility(View.VISIBLE)
                        //     findViewById(R.id.fabdeleteimg).setVisibility(View.VISIBLE)
                        selectImagePath = getPathFromUri(selectImgUri)


                    } catch (e: Exception) {
                        e.printStackTrace()
                        //    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                        e.message?.let { context?.toast(it) }

                    }
                }
            }
        }


    }

    private fun getPathFromUri(selectImgUri: Uri): String {

        val filePath: String
        val cursor: Cursor = context?.contentResolver?.query(selectImgUri, null, null, null, null)!!
        if (cursor == null) {
            filePath = selectImgUri.path!!
        } else {
            cursor.moveToFirst()
            val index: Int = cursor.getColumnIndex("_data")
            filePath = cursor.getString(index)
            cursor.close()
        }
        return filePath


    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.create_menu, menu)
        menu.findItem(R.id.menu_main_save).setEnabled(true).isVisible = true

        menu.findItem(R.id.menu_to_archive).setEnabled(true).isVisible = true
        menu.findItem(R.id.share).setEnabled(true).isVisible = true
        menu.findItem(R.id.color).setEnabled(true).isVisible = true
        menu.findItem(R.id.delete).setEnabled(true).isVisible = true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.menu_main_save -> {

                currentItem = NoteModel(
                    note.id,
                    binding.editTextTitle.text.toString(),
                    binding.editTextBody.text.toString(),
                    (binding.root.background as ColorDrawable).color,
                    null,
                    selectImagePath,
                    binding.lastModified.text.toString()
                )

                if (currentItem.title == "" && currentItem.description == "") {
                    context?.toast("Please fill details to save note")
                } else {

                    (activity as MainActivity).noteViewModel.UpdateNote(currentItem)
                    context?.toast("Note saved")
                    findNavController().navigateUp()


                }

            }
            R.id.menu_to_archive -> {


                (activity as MainActivity).noteViewModel.moveItem(
                    NOTE_TO_ARCHIVE,
                    noteItem,
                    archiveItem,
                    trashItem,
                    requireView()
                )
                findNavController().navigateUp()


            }
            R.id.share -> {
                context?.shareNotes(
                    binding.editTextTitle.text.toString(),
                    binding.editTextBody.text.toString()
                )
            }
            R.id.color -> {
                context?.changeNoteBackgroundColor(binding.root)
            }
            R.id.delete -> {

                (activity as MainActivity).noteViewModel.moveItem(
                    NOTE_TO_TRASH,
                    noteItem,
                    archiveItem,
                    trashItem,
                    requireView()
                )
                findNavController().navigateUp()

            }

        }

        return super.onOptionsItemSelected(item)
    }


}