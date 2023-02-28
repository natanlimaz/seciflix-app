package co.natanlima.seciflix.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

class DownloadImageTask(private val callback: Callback){

    private val handler = Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()

    interface Callback {
        fun onResult(bitmap: Bitmap)
    }

    fun execute(url: String) {
        executor.execute {

            var urlConnection: HttpsURLConnection? = null
            var stream: InputStream? = null
            try {
                val requestURL = URL(url)
                urlConnection= requestURL.openConnection() as HttpsURLConnection
                urlConnection.readTimeout = 2000
                urlConnection.connectTimeout = 2000

                val statusCode: Int = urlConnection.responseCode
                if(statusCode > 400) {
                    throw IOException("Erro na comunicação como servidor!")
                }

                stream = urlConnection.inputStream
                val bitmap = BitmapFactory.decodeStream(stream)

                handler.post {
                    callback.onResult(bitmap)
                }
            }
            catch(error: IOException) {
                val message = error.message ?: "Erro desconhecido!"
                Log.e("Teste", message, error)
            }
            finally {
                urlConnection?.disconnect()
                stream?.close()
            }

        }
    }
}