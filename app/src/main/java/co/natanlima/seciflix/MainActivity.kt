package co.natanlima.seciflix

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.natanlima.seciflix.model.Category
import co.natanlima.seciflix.model.Movie
import co.natanlima.seciflix.util.CategoryTask

class MainActivity : AppCompatActivity(), CategoryTask.Callback {

    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: CategoryAdapter
    private val categories = mutableListOf<Category>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressBar = findViewById(R.id.progress_main)

        adapter = CategoryAdapter(categories) {id ->
            val intent = Intent(this@MainActivity, MovieActivity::class.java)
            intent.putExtra("id", id)
            startActivity(intent)
        }
        val rvMain: RecyclerView = findViewById(R.id.rv_main)
        rvMain.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvMain.adapter = adapter

        CategoryTask(this).execute("https://api.tiagoaguiar.co/netflixapp/home?apiKey=2d7947b5-2b1c-497a-bbf2-0b5c6732b05d")
    }

    override fun onPreExecute() {
        progressBar.visibility = View.VISIBLE
    }
    override fun onResult(categories: List<Category>) {
        // Aqui será quando o CategoryTask chamar de volta
        // ( Callback ) - listener
        this.categories.clear()
        this.categories.addAll(categories)

        adapter.notifyDataSetChanged() // Força o adapter a redesenhar a tela, chamar novamente o onBindViewHolder, etc...

        progressBar.visibility = View.GONE
    }

    override fun onFailure(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        progressBar.visibility = View.GONE
    }

}