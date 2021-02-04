package com.note.notenest.ui.trash.updateTrash

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
import androidx.navigation.fragment.findNavController
import com.note.notenest.R
import com.note.notenest.data.models.ArchiveModel
import com.note.notenest.data.models.NoteModel
import com.note.notenest.data.models.TrashModel
import com.note.notenest.databinding.FragmentUpdateTrashBinding
import com.note.notenest.ui.main.MainActivity
import com.note.notenest.utils.Constants
import com.note.notenest.utils.changeNoteBackgroundColor
import com.note.notenest.utils.shareNotes
import com.note.notenest.utils.toast
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


class UpdateTrashFragment : Fragment() {


    lateinit var note: TrashModel
    lateinit var binding: FragmentUpdateTrashBinding
    private lateinit var currentItem: TrashModel
    private lateinit var noteItem: NoteModel
    private lateinit var archiveItem: ArchiveModel
    private lateinit var trashItem: TrashModel
    var selectImagePath: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            note = it.getParcelable(Constants.TRASH)!!
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = FragmentUpdateTrashBinding.inflate(inflater, container, false)
        binding.trashUpdate = note

        setHasOptionsMenu(true)
        setClickListeners()

        setData()
        setItems()


        return binding.root
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


    private fun setClickListeners() {

        binding.addMoreFab.setOnClickListener {

            (activity as MainActivity).noteViewModel.moveItem(
                Constants.TRASH_TO_NOTE,
                noteItem,
                archiveItem,
                trashItem,
                requireView()
            )


        }

        binding.bodyCard.setOnClickListener {

            (activity as MainActivity).noteViewModel.moveItem(
                Constants.TRASH_TO_NOTE_EDIT,
                noteItem,
                archiveItem,
                trashItem,
                requireView()
            )



        }
        binding.titleCard.setOnClickListener {
            (activity as MainActivity).noteViewModel.moveItem(
                Constants.TRASH_TO_NOTE_EDIT,
                noteItem,
                archiveItem,
                trashItem,
                requireView()
            )

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
        menu.findItem(R.id.delete).setEnabled(true).isVisible = true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.delete -> {

                (activity as MainActivity).noteViewModel.deleteTrashItem(trashItem)
                findNavController().navigateUp()

            }

        }

        return super.onOptionsItemSelected(item)
    }


}