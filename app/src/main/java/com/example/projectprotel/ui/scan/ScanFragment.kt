package com.example.projectprotel.ui.scan

import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.projectprotel.databinding.FragmentScanBinding
import com.example.projectprotel.ui.info.InfoActivity
import java.io.File


private const val FILE_NAME = "photo.jpg"
private const val REQUEST_CODE = 42
private const val GALLERY_REQUEST_CODE = 2
private lateinit var photoFile : File
private lateinit var galleryphotoFile : File
private lateinit var FilePhoto : String
private lateinit var currentPhotoPath : String
lateinit var bitmap: Bitmap


class ScanFragment : Fragment() {
    //ImageView mImageview
    //var imagePicker: ImageView? = null
    //private var resolver: ContentResolver? = requireActivity().contentResolver
    lateinit var imageView: ImageView
    private lateinit var scanViewModel: ScanViewModel
    private var _binding: FragmentScanBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        scanViewModel =
            ViewModelProvider(this).get(ScanViewModel::class.java)

        _binding = FragmentScanBinding.inflate(inflater, container, false)
        val root: View = binding.root


        binding.gallery.setOnClickListener {

            //Create an Intent with action as ACTION_PICK
            //Create an Intent with action as ACTION_PICK
            val intent = Intent(Intent.ACTION_PICK)
            // Sets the type as image/*. This ensures only components of type image are selected
            // Sets the type as image/*. This ensures only components of type image are selected
            intent.type = "image/*"
            //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
            //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
            val mimeTypes = arrayOf("image/jpeg", "image/png")
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            // Launching the Intent
            // Launching the Intent
            startActivityForResult(intent, GALLERY_REQUEST_CODE)
        }

        binding.cancelbtn.setOnClickListener {
            binding.imagecam.setImageResource(R.mipmap.sym_def_app_icon)
        }

        binding.camera.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photoFile = getPhotoFile(FILE_NAME)

            val fileProvider = context?.let { it1 ->
                FileProvider.getUriForFile(
                    it1, "com.example.projectprotel.fileprovider",
                    photoFile
                )
            }
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            if (context?.let { it1 -> takePictureIntent.resolveActivity(it1.packageManager) } != null) {
                startActivityForResult(takePictureIntent, REQUEST_CODE)
            } else {
                Toast.makeText(context, "unable to open camera", Toast.LENGTH_SHORT).show()
            }

        }

        return root
    }

    private fun getPhotoFile(fileName: String): File {
        val storageDirectory = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getPath(context: Context, uri: Uri?): String? {
        var result: String? = null
        val proj = arrayOf(MediaStore.Images.Media._ID)
        val cursor: Cursor? =
            uri?.let { context.contentResolver.query(it, proj, null, null, null) }
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val column_index: Int = cursor.getColumnIndexOrThrow(proj[0])
                result = cursor.getString(column_index)
            }
            cursor.close()
        }
        if (result == null) {
            result = "Not found"
        }
        return result
    }

    @SuppressLint("Range")
    fun getImageFilePath(uri1: Context, uri: Uri): String? {
        val file = File(uri.path)
        val filePath = file.path.split(":").toTypedArray()
        val image_id = filePath[filePath.size - 1]
        val cursor: Cursor? = context?.contentResolver?.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            MediaStore.Images.Media._ID + " = ? ",
            arrayOf(image_id),
            null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            val imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            cursor.close()
            return imagePath
        }
        return null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK)

            when (requestCode) {

                REQUEST_CODE -> {
                    val takenImage = BitmapFactory.decodeFile(photoFile.absolutePath)
                    binding.imagecam.setImageBitmap(takenImage)

                    binding.nextbtn.setOnClickListener {
                        val nextIntent = Intent(context, InfoActivity::class.java)
                        nextIntent.putExtra("picture", photoFile.absolutePath)
                        Log.e(photoFile.absolutePath, photoFile.absolutePath)
                        startActivity(nextIntent)
                    }

                }

                GALLERY_REQUEST_CODE -> {


                    val selectedImage: Uri = data?.data ?: return
                    val mBitmap: Bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, selectedImage)
                    binding.imagecam.setImageBitmap(mBitmap)

                    val picturePath = context?.let { getPath(it, selectedImage) }

//                    val galleryimage = BitmapFactory.decodeFile(galleryphotoFile.absolutePath)
//                    binding.imagecam.setImageBitmap(galleryimage)

//                    val returnUri: Uri? = data?.data
//                    val contentResolver = requireActivity().contentResolver
//                    val bitmapImage = MediaStore.Images.Media.getBitmap(contentResolver, returnUri)
                    //bitmapImage.scale(1,1)

//                    binding.imagecam.setImageBitmap(galleryimage)
                    //val takenImageGal = BitmapFactory.decodeFile(galleryphotoFile.absolutePath)
                    //binding.imagecam.setImageBitmap(takenImageGal)


                    //btngallery()

                    binding.nextbtn.setOnClickListener {
                        val nextIntentGallery = Intent(context, InfoActivity::class.java)
                        nextIntentGallery.putExtra("picturegal", selectedImage)
                        nextIntentGallery.putExtra("imageUri", selectedImage)
                        if (picturePath != null) {
                            Log.e(picturePath,picturePath)
                        }
                        startActivity(nextIntentGallery)
                    }
                }

            }
    }
}