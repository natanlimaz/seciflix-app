package co.natanlima.seciflix

import android.graphics.drawable.LayerDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.natanlima.seciflix.model.Movie

class MovieActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        val txtTitle: TextView = findViewById(R.id.movie_txt_title)
        val txtDescription: TextView = findViewById(R.id.movie_txt_desc)
        val txtCast: TextView = findViewById(R.id.movie_txt_cast)
        val rv: RecyclerView = findViewById(R.id.movie_rv_similar)

        txtTitle.text = "Batman Begins"
        txtDescription.text = "O jovem Bruce Wayne viaja para o Oriente e recebe treinamento em artes marciais do mestre Henri Ducard, um membro da misteriosa Liga das Sombras. Quando Ducard revela que a verdadeira proposta da Liga é a destruição completa de Gotham City, Wayne retorna à sua cidade natal com o intuito de livrá-la de criminosos e assassinos. Bruce assume a persona de Batman, o Cavaleiro das Trevas, e conta com a ajuda do mordomo Alfred e do expert Lucius Fox."
        txtCast.text = getString(R.string.cast, "Elenco: Christian Bale. Personagem : Bruce Wayne/Batman ; Michael Caine. Personagem : Alfred Pennyworth ; Liam Neeson. Personagem : Ra's Al Ghul / Henri Ducard.")

        val movies = mutableListOf<Movie>()
        for(i in 0 until 15) {
            val movie = Movie(R.drawable.movie)
            movies.add(movie)
        }

        rv.layoutManager = GridLayoutManager(this, 3)
        rv.adapter = MovieAdapter(movies, R.layout.movie_item_similar)

        /* Toolbar personalizada! */
        val toolbar: Toolbar = findViewById(R.id.movie_toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = null

        // busquei o desenhavel (layer-list)
        val layerDrawable: LayerDrawable = ContextCompat.getDrawable(this, R.drawable.shadows) as LayerDrawable

        // busquei o filme(img) que eu quero
        val movieCover = ContextCompat.getDrawable(this, R.drawable.movie_4)

        // atribui a esse layer-list o novo filme(img)
        layerDrawable.setDrawableByLayerId(R.id.cover_drawable, movieCover)

        // setei o desenhavel no image view
        val coverImg: ImageView = findViewById(R.id.movie_img)
        coverImg.setImageDrawable(layerDrawable)

    }

    // CLIQUE BOTAO VOOLTAR
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.drawable.baseline_arrow_back_24 -> finishAffinity()
        }
        return super.onOptionsItemSelected(item)
    }

}