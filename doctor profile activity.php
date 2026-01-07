package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class DoctorProfileActivity : AppCompatActivity() {

    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private lateinit var profileImageView: ImageView
    private lateinit var uploadProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_doctor_profile)

        profileImageView = findViewById(R.id.iv_profile_photo)
        uploadProgressBar = findViewById(R.id.pb_profile_upload)

        // Load cached image immediately if available
        val sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE)
        val cachedImage = sharedPref.getString("profile_image", "") ?: ""
        if (cachedImage.isNotEmpty() && cachedImage != "null") {
            val imageUrl = "http://192.168.31.130/php_backend/uploads/profile_pics/$cachedImage"
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_doctor_profile_placeholder)
                .into(profileImageView)
        }

        // Initialize the image picker launcher
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                uploadImageToServer(it)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            finish()
        }

        findViewById<android.view.View>(R.id.profile_photo_container).setOnClickListener {
            // Launch the system image picker
            pickImageLauncher.launch("image/*")
        }

        findViewById<Button>(R.id.btn_logout).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        loadDoctorProfile()
    }

    private fun loadDoctorProfile() {
        val sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE)
        val username = sharedPref.getString("username", "") ?: ""

        if (username.isEmpty()) return

        RetrofitClient.apiService.getDoctorProfile(username).enqueue(object : retrofit2.Callback<ApiResponse> {
            override fun onResponse(call: retrofit2.Call<ApiResponse>, response: retrofit2.Response<ApiResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    val doctor = response.body()?.doctor
                    doctor?.let {
                        findViewById<TextView>(R.id.tv_profile_name).text = it["full_name"]?.toString() ?: "N/A"
                        findViewById<TextView>(R.id.tv_profile_specialty).text = it["specialist"]?.toString() ?: "N/A"
                        findViewById<TextView>(R.id.tv_profile_experience).text = "${it["experience_years"] ?: "0"} Years"
                        findViewById<TextView>(R.id.tv_profile_hospital).text = it["hospital_name"]?.toString() ?: "N/A"
                        findViewById<TextView>(R.id.tv_profile_email).text = it["email"]?.toString() ?: "N/A"

                        val profileImage = it["profile_image"]?.toString() ?: ""
                        if (profileImage.isNotEmpty() && profileImage != "null") {
                            sharedPref.edit().putString("profile_image", profileImage).apply()
                            val imageUrl = "http://192.168.31.130/php_backend/uploads/profile_pics/$profileImage"
                            Glide.with(this@DoctorProfileActivity)
                                .load(imageUrl)
                                .placeholder(R.drawable.ic_doctor_profile_placeholder)
                                .error(R.drawable.ic_doctor_profile_placeholder)
                                .into(profileImageView)
                        }
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<ApiResponse>, t: Throwable) {
                Toast.makeText(this@DoctorProfileActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun uploadImageToServer(uri: Uri) {
        val sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE)
        val username = sharedPref.getString("username", "") ?: ""

        if (username.isEmpty()) return

        val file = getFileFromUri(uri)
        if (file == null) {
            Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show()
            return
        }

        val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
        val body = MultipartBody.Part.createFormData("profile_image", file.name, requestFile)
        val usernameBody = RequestBody.create(MediaType.parse("text/plain"), username)

        uploadProgressBar.visibility = View.VISIBLE
        Toast.makeText(this, "Uploading photo...", Toast.LENGTH_SHORT).show()

        RetrofitClient.apiService.uploadProfileImage(body, usernameBody).enqueue(object : retrofit2.Callback<ApiResponse> {
            override fun onResponse(call: retrofit2.Call<ApiResponse>, response: retrofit2.Response<ApiResponse>) {
                uploadProgressBar.visibility = View.GONE
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(this@DoctorProfileActivity, "Profile photo updated", Toast.LENGTH_SHORT).show()
                    val newImage = response.body()?.image_url ?: ""
                    if (newImage.isNotEmpty()) {
                        sharedPref.edit().putString("profile_image", newImage).apply()
                        val imageUrl = "http://192.168.31.130/php_backend/uploads/profile_pics/$newImage"
                        Glide.with(this@DoctorProfileActivity)
                            .load(imageUrl)
                            .into(profileImageView)
                    }
                } else {
                    val msg = response.body()?.message ?: "Upload failed"
                    Toast.makeText(this@DoctorProfileActivity, msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: retrofit2.Call<ApiResponse>, t: Throwable) {
                uploadProgressBar.visibility = View.GONE
                Toast.makeText(this@DoctorProfileActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getFileFromUri(uri: Uri): File? {
        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val file = File(cacheDir, "profile_upload.jpg")
            val outputStream = FileOutputStream(file)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}
