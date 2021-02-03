package com.note.notenest.ui.home.createNote

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.note.notenest.R
import com.note.notenest.data.models.NoteModel
import com.note.notenest.databinding.FragmentCreateNoteBinding
import com.note.notenest.utils.*
import com.note.notenest.viewModels.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*



@AndroidEntryPoint
class CreateNoteFragment : Fragment() {

    private lateinit var currentItem: NoteModel

    private val noteViewModel: NoteViewModel by lazy {
        ViewModelProvider(this)[NoteViewModel::class.java]
    }

    var selectImagePath: String? = null

    lateinit var binding: FragmentCreateNoteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCreateNoteBinding.inflate(inflater, container, false)

        setClickListeners()
        setData()
        setHasOptionsMenu(true)

        handleBackPressed()

        return binding.root

    }

    private fun handleBackPressed() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                    currentItem = NoteModel(
                        0,
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

    private fun setData() {
        binding.lastModified.text = "Last modified : " + SimpleDateFormat(
            "dd MMMM yyyy",
            Locale.getDefault()
        ).format(Date())


    }


    private fun setClickListeners() {

        binding.addMoreFab.setOnClickListener {

            //create cool dialog here

            createDialog()


        }


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


        if (requestCode === Constants.REQUEST_SELECT && resultCode === RESULT_OK) {
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
        menu.findItem(R.id.share).setEnabled(true).isVisible = true
        menu.findItem(R.id.color).setEnabled(true).isVisible = true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.menu_main_save -> {

                currentItem = NoteModel(
                    0, binding.editTextTitle.text.toString(), binding.editTextBody.text.toString(),
                    (binding.root.background as ColorDrawable).color,
                    null,
                    selectImagePath,
                    binding.lastModified.text.toString()
                )

                if(currentItem.title==""&&currentItem.description==""){
                    context?.toast("Please fill details to save note")
                }else{

                    noteViewModel.addNoteItem(currentItem)
                    context?.toast("Note saved")
                    findNavController().navigateUp()


                }

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
              //  context?.toast("delete")
            }

        }

        return super.onOptionsItemSelected(item)
    }


}