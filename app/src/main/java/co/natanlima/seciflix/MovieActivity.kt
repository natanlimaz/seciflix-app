package co.natanlima.seciflix

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.natanlima.seciflix.model.Movie
import co.natanlima.seciflix.model.MovieDetail
import co.natanlima.seciflix.util.MovieTask
import com.squareup.picasso.Picasso

class MovieActivity : AppCompatActivity(), MovieTask.Callback {

    private lateinit var progressBar: ProgressBar
    private lateinit var txtTitle: TextView
    private lateinit var txtDescription: TextView
    private lateinit var txtCast: TextView
    private lateinit var adapter: MovieAdapter
    private val movies = mutableListOf<Movie>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        txtTitle = findViewById(R.id.movie_txt_title)
        txtDescription = findViewById(R.id.movie_txt_desc)
        txtCast = findViewById(R.id.movie_txt_cast)
        progressBar = findViewById(R.id.movie_progress)

        val rv: RecyclerView = findViewById(R.id.movie_rv_similar)

        val id = intent?.getIntExtra("id", 0) ?: throw IllegalStateException("ID não foi encontrado!")

        val url = "https://api.tiagoaguiar.co/netflixapp/movie/${id}?apiKey=2d7947b5-2b1c-497a-bbf2-0b5c6732b05d"

        MovieTask(this).execute(url)

        adapter = MovieAdapter(movies, R.layout.movie_item_similar)
        rv.layoutManager = GridLayoutManager(this, 3)
        rv.adapter = adapter

        /* Toolbar personalizada! */
        val toolbar: Toolbar = findViewById(R.id.movie_toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = null

    }

    override fun onPreExecute() {
        progressBar.visibility = View.VISIBLE
    }

    override fun onResult(movieDetail: MovieDetail) {
        progressBar.visibility = View.GONE

        txtTitle.text = movieDetail.movie.title
        txtDescription.text = movieDetail.movie.desc
        txtCast.text = getString(R.string.cast, movieDetail.movie.cast)

        movies.clear()
        movies.addAll(movieDetail.similars)
        adapter.notifyDataSetChanged()

        val coverImg: ImageView = findViewById(R.id.movie_img)

        Picasso.get().load(movieDetail.movie.coverUrl).into(object: com.squareup.picasso.Target {
            override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {

                Log.e("Error Bitmap", e?.message, e)

                val errorMessage: String = "Não foi possível carregar a imagem do filme!"
                Toast.makeText(this@MovieActivity, errorMessage, Toast.LENGTH_LONG).show()
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                val layerDrawable: LayerDrawable = ContextCompat.getDrawable(this@MovieActivity, R.drawable.shadows) as LayerDrawable
                val movieCover = BitmapDrawable(resources, bitmap)
                layerDrawable.setDrawableByLayerId(R.id.cover_drawable, movieCover)

                coverImg.setImageDrawable(layerDrawable)
            }
        })
    }

    override fun onFailure(message: String) {
        progressBar.visibility = View.GONE
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // CLIQUE BOTAO VOOLTAR
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.drawable.baseline_arrow_back_24 -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

}