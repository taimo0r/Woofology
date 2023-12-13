package com.example.woofology

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ShareDogData {
    @Throws(IOException::class)
    fun shareDogInfo(activity: Activity) {
        val view = activity.findViewById<View>(R.id.rootView)
        val bitmap = Bitmap.createBitmap(view.width, (view.height * 1.3).toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        view.layout(view.left, view.top, view.right, view.bottom)
        view.draw(canvas)

        // Save Bitmap as JPG
        val imageFileName = "Woofology_Dog_Info.jpg"
        val imageDogShare = File(activity.externalCacheDir, imageFileName)
        //Convert bitmap to byte array
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        val bitmapdata = bos.toByteArray()
        //Write the bytes in file
        val fos = FileOutputStream(imageDogShare)
        fos.write(bitmapdata)
        fos.flush()
        fos.close()
        val shareImageURI = FileProvider.getUriForFile(
            activity,
            "com.example.woofology.fileprovider",
            imageDogShare
        )
        val intent = Intent(Intent.ACTION_SEND)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(Intent.EXTRA_STREAM, shareImageURI)
        intent.type = "image/jpg"
        activity.startActivity(Intent.createChooser(intent, "Share image via"))
    }
}