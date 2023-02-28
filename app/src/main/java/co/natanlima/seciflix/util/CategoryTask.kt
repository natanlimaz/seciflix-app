package co.natanlima.seciflix.util

import android.os.Handler
import android.os.Looper
import android.util.Log
import co.natanlima.seciflix.model.Category
import co.natanlima.seciflix.model.Movie
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

class CategoryTask(private val callback: Callback) {

    private val handler = Handler(Looper.getMainLooper()) // variavel que vai entrar no meio do fluxo do loop da thread principal
    private val executor = Executors.newSingleThreadExecutor()

    interface Callback {
        fun onPreExecute()
        fun onResult(categories: List<Category>)
        fun onFailure(message: String)
    }

    fun execute(url: String) {
        callback.onPreExecute()
        // Neste momento, estamos utilizando a UI-thread (1)

        executor.execute {
            var urlConnection: HttpsURLConnection? = null
            var buffer: BufferedInputStream? = null
            var stream: InputStream? = null

            try {
                // Neste momento, estamos utilizando a NOVA-thread [processo paralelo] (2)
                val requestURL = URL(url) // abrir uma url
                urlConnection = requestURL.openConnection() as HttpsURLConnection // abrir a conexão
                urlConnection.readTimeout = 2000 //tempo leitura (2s)
                urlConnection.connectTimeout = 2000 //tempo conexão (2s)

                val statusCode: Int = urlConnection.responseCode
                if (statusCode > 400) {
                    throw IOException("Erro na comunicação com o servidor")
                }

                stream = urlConnection.inputStream // sequencia de bytes
                // forma 1: simples e rápida (forma mais pura e raiz) pouco utilizado atualmente
                //val jsonAsString = stream.bufferedReader().use { it.readText() } // bytes -> string

                // forma 2: ???
                buffer = BufferedInputStream(stream)
                val jsonAsString = toString(buffer)

                // o JSON está preparado para ser convertido em um DATA CLASS
                val categories = toCategories(jsonAsString)

                handler.post { // Aqui roda dentro da UI Thread
                    callback.onResult(categories)
                }

            } catch(error: IOException) {
                val message = error.message ?: "Erro desconhecido"
                Log.e("Teste", message, error)
                handler.post {
                    callback.onFailure(message)
                }
            }
            finally {
                urlConnection?.disconnect()
                stream?.close()
                buffer?.close()
            }
        }
    }

    private fun toCategories(jsonAsString: String) : List<Category> {
        val categories = mutableListOf<Category>()

        val jsonRoot = JSONObject(jsonAsString)
        val jsonCategories = jsonRoot.getJSONArray("category")

        for(i in 0 until jsonCategories.length()) {

            val jsonCategory = jsonCategories.getJSONObject(i)

            val title = jsonCategory.getString("title")
            val jsonMovies = jsonCategory.getJSONArray("movie")

            val movies = mutableListOf<Movie>()
            for(j in 0 until jsonMovies.length()) {
                val jsonMovie = jsonMovies.getJSONObject(j)
                val id = jsonMovie.getInt("id")
                val coverUrl = jsonMovie.getString("cover_url")

                movies.add( Movie(id, coverUrl) )
            }

            categories.add( Category(title, movies) )
        }

        return categories
    }

    private fun toString(stream: InputStream): String {
        val bytes = ByteArray(1024)
        val baos = ByteArrayOutputStream()
        var read: Int
        while(true) {
            read = stream.read(bytes)
            if(read <= 0) {
                break
            }
            baos.write(bytes, 0, read)
        }

        return String(baos.toByteArray())
    }

}