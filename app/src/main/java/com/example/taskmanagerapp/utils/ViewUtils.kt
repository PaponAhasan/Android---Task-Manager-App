package com.example.taskmanagerapp.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.example.taskmanagerapp.R

object ViewUtils {
    fun viewDialogResponse(mContext: Context, image: Drawable, message: String, listener: DialogListener){
        val dialog = Dialog(mContext)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.popup_dialog_success)
        val imageView = dialog.findViewById<ImageView>(R.id.imageResponse)
        val tvMessage = dialog.findViewById(R.id.tvMessage) as TextView
        val btnOk = dialog.findViewById<AppCompatButton>(R.id.btn_ok)
        val btnCancel = dialog.findViewById<AppCompatButton>(R.id.btn_cancel)
        tvMessage.text = message
        imageView.setImageDrawable(image)

        btnOk.setOnClickListener {
            listener.onConfirmed()
            dialog.dismiss()
        }
        btnCancel.setOnClickListener {
            listener.onCanceled()
            dialog.dismiss()
        }

        dialog.show()
        val window = dialog.window
        window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }
}