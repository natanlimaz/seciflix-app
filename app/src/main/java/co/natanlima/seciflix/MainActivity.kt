package co.natanlima.seciflix

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.natanlima.seciflix.model.Category
import co.natanlima.seciflix.model.Movie

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i("Teste", "onCreate")

        val categories = mutableListOf<Category>()
        for(j in 0 until 5) {

            val movies = mutableListOf<Movie>()
            for(i in 0 until 15) {
                val movie = Movie(R.drawable.movie)
                movies.add(movie)
            }

            val category = Category("cat $j", movies)
            categories.add(category)
        }


        val adapter = CategoryAdapter(categories)
        val rvMain: RecyclerView = findViewById(R.id.rv_main)
        rvMain.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvMain.adapter = adapter
    }

}