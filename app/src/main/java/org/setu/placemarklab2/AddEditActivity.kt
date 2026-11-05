package org.setu.placemarklab2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.setu.placemarklab2.models.PlacemarkModel
import timber.log.Timber
import android.net.Uri
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts


class AddEditActivity : AppCompatActivity() {

    private lateinit var titleInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var xInput: EditText
    private lateinit var yInput: EditText

    private var editingId: Long? = null

    private lateinit var imagePreview: ImageView
    private var selectedImageUri: Uri? = null

    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            if (uri != null) {
                // Keep access to the document URI for later use, when supported.
                try {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: SecurityException) {
                    // Some providers may not grant persistable access.
                }

                selectedImageUri = uri
                imagePreview.setImageURI(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_edit)

        imagePreview = findViewById(R.id.imagePreview)

        findViewById<Button>(R.id.imagePickButton).setOnClickListener {
            imagePicker.launch(arrayOf("image/*"))
        }

        titleInput =
            findViewById(R.id.titleInput)

        descriptionInput =
            findViewById(R.id.descriptionInput)

        xInput =
            findViewById(R.id.xInput)

        yInput =
            findViewById(R.id.yInput)

        val saveButton =
            findViewById<Button>(R.id.saveButton)

        val cancelButton =
            findViewById<Button>(R.id.cancelButton)

        editingId =
            intent.getLongExtra("id", -1L)
                .takeIf { it != -1L }

        if (editingId != null) {
            Timber.i("Form opened in EDIT mode for ID: $editingId")
            findViewById<TextView>(R.id.formTitle)
                .text = "Edit Mark"
            loadExistingMark(editingId!!)
        } else {
            Timber.i("Form opened in CREATE mode")
        }

        saveButton.setOnClickListener {
            saveMark()
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun loadExistingMark(id: Long) {

        val mark =
            AppData.placedMarks.findOne(id)

        if (mark == null) {

            Toast.makeText(
                this,
                "Mark not found",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        titleInput.setText(mark.title)
        descriptionInput.setText(mark.description)
        xInput.setText(mark.x.toString())
        yInput.setText(mark.y.toString())

        selectedImageUri = mark.imageUri?.let { Uri.parse(it) }

        if (selectedImageUri != null) {
            imagePreview.setImageURI(selectedImageUri)
        } else {
            imagePreview.setImageResource(android.R.drawable.ic_menu_gallery)
        }
    }

    private fun saveMark() {

        val title =
            titleInput.text.toString().trim()

        val description =
            descriptionInput.text.toString().trim()

        if (title.isEmpty()) {
            titleInput.error = "Title is required"
            return
        }

        val x =
            xInput.text.toString().toDoubleOrNull()

        if (x == null) {
            xInput.error = "Enter a valid number"
            return
        }

        val y =
            yInput.text.toString().toDoubleOrNull()

        if (y == null) {
            yInput.error = "Enter a valid number"
            return
        }

        try {
            if (editingId == null) {

                val mark = PlacemarkModel(
                    title = title,
                    description = description,
                    x = x,
                    y = y,
                    imageUri = selectedImageUri?.toString()
                )

                mark.validate()
                AppData.placedMarks.create(mark)
                Timber.i("Created new mark: $mark")

                Toast.makeText(
                    this,
                    "Mark created",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                val mark = PlacemarkModel(
                    id = editingId!!,
                    title = title,
                    description = description,
                    x = x,
                    y = y,
                    imageUri = selectedImageUri?.toString()
                )

                mark.validate()
                AppData.placedMarks.update(mark)
                Timber.i("Updated mark: $mark")
                Toast.makeText(
                    this,
                    "Mark updated",
                    Toast.LENGTH_SHORT
                ).show()
            }

            finish()
        } catch (e: IllegalArgumentException) {
            Toast.makeText(
                this,
                e.message ?: "Please check the entered values.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
