package org.setu.placemarklab2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.setu.placemarklab2.models.PlacemarkModel
import androidx.appcompat.app.AlertDialog
import timber.log.Timber

class MarkListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PlacedMarkAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_mark_list)
        Timber.i("MarkListActivity started")
        recyclerView =
            findViewById(R.id.marksRecyclerView)


        adapter = PlacedMarkAdapter(

            marks = AppData.placedMarks.findAll(),

            listener = object : OnMarkListener {



                override fun onMarkClick(mark: PlacemarkModel) {
                    editMark(mark)
                }



                override fun onMarkLongClick(mark: PlacemarkModel): Boolean {
                    deleteMark(mark)
                    return true

                }
            }
        )



        recyclerView.layoutManager =
            LinearLayoutManager(this)

        recyclerView.adapter = adapter

        val returnButton =
            findViewById<Button>(R.id.returnButton)

        returnButton.setOnClickListener {

            startActivity(
                Intent(this, MainActivity::class.java)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (::adapter.isInitialized) {
            val marks = AppData.placedMarks.findAll()
            Timber.i("Refreshing mark list, found ${marks.size} items")
            adapter.updateMarks(marks)
        }
    }

    private fun editMark(mark: PlacemarkModel) {
        Timber.i("Edit requested for mark ID: ${mark.id} (${mark.title})")
        val intent =
            Intent(this, AddEditActivity::class.java)

        intent.putExtra("id", mark.id)

        startActivity(intent)
    }

    private fun deleteMark(mark: PlacemarkModel) {
        Timber.i("Delete requested for mark ID: ${mark.id} (${mark.title})")
        AlertDialog.Builder(this)
            .setTitle("Delete Mark")
            .setMessage("Are you sure you want to delete '${mark.title}'?")
            .setPositiveButton("Delete") { dialog, _ ->
                AppData.placedMarks.delete(mark.id)
                adapter.updateMarks(AppData.placedMarks.findAll())
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
