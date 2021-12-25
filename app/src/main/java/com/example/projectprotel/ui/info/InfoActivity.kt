package com.example.projectprotel.ui.info

import android.app.SearchManager
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.projectprotel.Data.PlantsEntity
import com.example.projectprotel.databinding.InfoBinding
import com.example.projectprotel.ml.Model
import com.example.projectprotel.ui.search.SearchFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.ObjectDetector


class InfoActivity : AppCompatActivity(){
    private lateinit var binding: InfoBinding

    companion object {
        const val kategori = "kategori"
        const val kategoridua = "kategoridua"
        const val kategoritiga = "kategoritiga"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = InfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[InfoViewModel::class.java]

        BottomSheetBehavior.from(binding.infoframe).apply {
            peekHeight=200
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }



        val picture = intent.getStringExtra("picture")
        val pictureGal = intent.getStringExtra("picturegal")

        val uri: Uri? = intent.getParcelableExtra("imageUri")

        // just display image in imageview
        // just display image in imageview
        //imageView.setImageBitmap(BitmapFactory.decodeStream(ims))

        val apa = BitmapFactory.decodeFile(picture)
        val gapa = BitmapFactory.decodeFile(pictureGal)
        val ims = uri?.let { contentResolver.openInputStream(it) }
        val ppp = BitmapFactory.decodeStream(ims)


        if (apa == apa) {
            binding.gambarinfo.setImageBitmap(apa)
        }
        if (ppp == ppp){
            binding.gambarinfo.setImageBitmap(ppp)
        }



        if (apa == null){

            val extras = intent.extras
            val plantId = extras?.getString(kategori)
            val plantId2 = extras?.getString(kategoridua)
            val plantId3 = extras?.getString(kategoritiga)

            if (plantId != null) {
                if (plantId2 != null) {
                    if (plantId3 != null) {
                        viewModel.setSelectedplants(plantId, plantId2, plantId3)
                    }
                }
                viewModel.getPlants()?.let { populateplant(it) }
                binding.searchbtn.setOnClickListener{
                    val intent = Intent(Intent.ACTION_WEB_SEARCH)
                    intent.putExtra(SearchManager.QUERY, "Tanaman " + plantId +" yang berhabitat di " + plantId2 + " dan " + plantId3)
                    startActivity(intent)

                }
            }
        }
        else{
            //imageInBitMapAfterResize=resizePhoto(Apa)
            //imageInBitMapAfterResizeGal=resizePhoto(Gapa)
            //binding.searchbtn.text = ""
            scans(apa)
            search()

            //scans(gapa)
        }

        if (ppp != null){

            //binding.searchbtn.text = ""
            scans(ppp)
            search()

        }

    }

    private fun search() {
        binding.tombolsearch.setOnClickListener {

//            val nextintent = Intent(this, SearchFragment::class.java)
//            startActivity(nextintent)
//            tes
            binding.gambarinfo.visibility = View.GONE
            binding.searchbtn.visibility = View.GONE
            binding.infoframe.visibility = View.GONE

            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(com.example.projectprotel.R.id.maincontainer, SearchFragment()).commit()
        }
    }

    private fun scans(bitmap: Bitmap){
        val viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[InfoViewModel::class.java]
        val model = Model.newInstance(this)

// Creates inputs for reference.
        val image = TensorImage.fromBitmap(bitmap)

// Runs model inference and gets result.
        val outputs = model.process(image)
        val probability = outputs.probabilityAsCategoryList
        val hasil = probability.maxByOrNull { it.score }?.label ?: "NO_PLANTS"


        viewModel.setSelectedplant(hasil)
        viewModel.getPlant()?.let { populateplant(it) }

// Releases model resources if no longer used.
        model.close()


    }

    private fun scan(bitmap: Bitmap) {
        val image = TensorImage.fromBitmap(bitmap)

        val options = ObjectDetector.ObjectDetectorOptions.builder()
            .setMaxResults(5)
            .setScoreThreshold(0.3f)
            .build()
        val detector = ObjectDetector.createFromFileAndOptions(
            this,
            "model.tflite",
            options
        )
        val results = detector.detect(image)

        val resultToDisplay = results.map {
            val category = it.categories.first()
            val text = "${category.label}, ${category.score.times(100).toInt()}%"
            //DetectionResult(it.boundingBox, text)
        }
        val textWithResult = resultToDisplay.toString()
        //val imgWithResult = image
        runOnUiThread {
            binding.judul.text = textWithResult
            //binding.gambarinfo.setImageBitmap(imgWithResult)
        }
    }

    private fun populateplant(plantsEntity: PlantsEntity) {
        with(binding){
            judul.text = plantsEntity.name
            deskripsi.text = plantsEntity.description
            classification.text = plantsEntity.scientificClassification
            characteristics.text = plantsEntity.characteristics
            distribution.text = plantsEntity.distribution
            cycle.text = plantsEntity.lifecycle
            category.text = plantsEntity.kategorisatu + "\n" + plantsEntity.kategoridua + "\n" + plantsEntity.kategoritiga
        }
        Glide.with(this)
            .load(plantsEntity.gambar)
            .apply(RequestOptions().override(3500, 2000))
            .centerCrop()
            .into(binding.gambarinfo)
    }


    fun resizePhoto(bitmap: Bitmap): Bitmap {


        val w = bitmap.width
        val h = bitmap.height
        val aspRat = w / h
        val W = 224
        val H = W * aspRat
        val b = Bitmap.createScaledBitmap(bitmap, W, H, false)

        return b


    }



}
