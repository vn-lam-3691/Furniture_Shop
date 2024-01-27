package com.vanlam.furnitureadder

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.icu.number.IntegerWidth
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.vanlam.furnitureadder.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.UUID

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var selectedImages = mutableListOf<Uri>()
    private var selectedColors = mutableListOf<Int>()
    private val productStorage = Firebase.storage.reference
    private val firestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonColorPicker.setOnClickListener {
            ColorPickerDialog.Builder(this)
                .setTitle("Product Color")
                .setPositiveButton("Select", object : ColorEnvelopeListener {
                    override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                        envelope?.let {
                            selectedColors.add(it.color)
                            updateStringColors()
                        }
                    }
                })
                .setNegativeButton("Cancel") { colorPicker, _ ->
                    colorPicker.dismiss()
                }.show()
        }

        val selectImagesActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val intent = result.data

                if (intent?.clipData != null) {  // Multiple images selected
                    val count = intent.clipData?.itemCount ?: 0
                    (0 until count).forEach {
                        val imageUri = intent.clipData?.getItemAt(it)?.uri
                        imageUri?.let {
                            selectedImages.add(it)
                        }
                    }
                }
                else {  // Single image selected
                    val imageUri = intent?.data
                    imageUri?.let { selectedImages.add(it) }
                }

                updateStringImages()
            }
        }

        binding.buttonImagesPicker.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = "image/*"

            selectImagesActivityResult.launch(intent)
        }
    }

    private fun updateStringImages() {
        binding.tvSelectedImages.text = selectedImages.size.toString()
    }

    private fun updateStringColors() {
        var colorString = ""
        selectedColors.forEach {
            colorString = "$colorString ${Integer.toHexString(it)}"
        }
        binding.tvSelectedColors.text = colorString
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.saveProduct) {
            val dataValidation = validateInformation()
            if (!dataValidation) {
                Toast.makeText(this, "Please check your input information", Toast.LENGTH_SHORT).show()
                return false
            }
            saveProduct()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun saveProduct() {
        val name = binding.edName.text.trim().toString()
        val category = binding.edCategory.text.trim().toString()
        val description = binding.edDescription.text.trim().toString()
        val price = binding.edPrice.text.toString().toFloat()
        val offer = binding.offerPercentage.text.toString().toFloat()
        val sizes = getSizeList(binding.edSizes.text.trim().toString())
        val imagesByteArray = getImagesByteArray()
        val images = mutableListOf<String>()

        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                showLoading()
            }
            try {
                async {
                    imagesByteArray.forEach {
                        val id = UUID.randomUUID().toString()
                        launch {
                            val imageStorage = productStorage.child("products/images/$id")
                            val result = imageStorage.putBytes(it).await()
                            val downloadUrl = result.storage.downloadUrl.await().toString()
                            images.add(downloadUrl)
                        }
                    }
                }.await()
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    hideLoading()
                }
            }

            val product = Product(
                UUID.randomUUID().toString(),
                name, category, price,
                if (binding.offerPercentage.text.isEmpty()) null else offer,
                if (binding.edDescription.text.isEmpty()) null else description,
                if (selectedColors.isEmpty()) null else selectedColors,
                sizes, images
            )

            firestore.collection("products").add(product)
                .addOnSuccessListener {
                    hideLoading()
                }
                .addOnFailureListener {
                    hideLoading()
                    Log.e("MainActivity", it.message.toString())
                }
        }
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.INVISIBLE
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun getImagesByteArray(): List<ByteArray> {
        val imagesByteArray = mutableListOf<ByteArray>()
        selectedImages.forEach {
            val stream = ByteArrayOutputStream()
            val imageBmp = MediaStore.Images.Media.getBitmap(contentResolver, it)

            if (imageBmp.compress(Bitmap.CompressFormat.JPEG, 100, stream)) {
                imagesByteArray.add(stream.toByteArray())
            }
        }
        return imagesByteArray
    }

    private fun getSizeList(stringSize: String): List<String>? {
        if (stringSize.isEmpty())
            return null

        return stringSize.split(",")
    }

    private fun validateInformation(): Boolean {
        if (binding.edName.text.isEmpty())
            return false

        if (binding.edCategory.text.isEmpty())
            return false

        if (binding.edPrice.text.isEmpty())
            return false

        if (selectedImages.isEmpty())
            return false

        return true
    }
}