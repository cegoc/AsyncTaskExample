package com.paradox.asynctaskexample

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import java.net.URL
import java.security.AccessControlContext

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val asyncTask = MyAsyncTask(this,content, progressDownload, textInfo)

        btnStar.setOnClickListener{
            asyncTask.execute("https://image.ibb.co/m0bLqJ/16205604.png",
                    "https://www.pikpng.com/pngl/m/375-3750661_baby-sticker-imagenes-de-ositos-animados-bonitos-clipart.png")
            it.isEnabled=false
            btnCancel.isEnabled=true
        }
        btnCancel.setOnClickListener{
            asyncTask.cancel(true)
            it.isEnabled=false
        }
    }

    private inner class MyAsyncTask(val contex: Context, val content:LinearLayout, //Recibimos la vista que vamos a usar en el AsyncTask
                                    val progress:ProgressBar, val txtInfo:TextView): AsyncTask<String,Int,MutableList<Bitmap>>(){

        override fun onPreExecute() {
            super.onPreExecute()
            progressDownload.progress = 0
            txtInfo.text = "Descarga iniciada"
        }
        override fun doInBackground(vararg params: String?): MutableList<Bitmap> {
            val list = ArrayList<Bitmap>()
            for (i in 0 until params.size){
                val urlImage = URL(params[i])
                try {
                    val input = urlImage.openStream()
                    list.add(BitmapFactory.decodeStream(input))
                }catch (e:Exception){
                    Log.println(Log.WARN, "doInBackground", e.message)
                }
                //4 = 100
                //2? = 50
                publishProgress((((i+1)*100)/params.size.toFloat()).toInt())
                if (isCancelled)break
            }
            return list
        }

        override fun onProgressUpdate(vararg values: Int?) {
            progressDownload.progress = values[0]!!
            txtInfo.text = "Descarga${values[0]}%"
            super.onProgressUpdate(*values)
        }

        override fun onPostExecute(result: MutableList<Bitmap>?) {
            txtInfo.text="Descarga Finalizada\n${result?.size} Archivos descargados"
            for (image in result!!){
                content.addView(addImage(image))

            }
            super.onPostExecute(result)
        }

        override fun onCancelled(result:MutableList<Bitmap>?) {
            super.onCancelled(result)
            txtInfo.text="Descarga Cancelada\n${result?.size} Archivos descargados"
            for (image in result!!){
                content.addView(addImage(image))

            }
        }
        @SuppressLint("ResourceAsColor")
        private fun addImage(Bitmap:Bitmap):ImageView{
            val params= LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 400)
            params.bottomMargin=20
            val imageView=ImageView(contex)
            imageView.layoutParams = params

            imageView.run {
                setImageBitmap(Bitmap)
                setBackgroundColor(R.color.start)

            }
            return imageView
        }
    }
}