package com.example.stemlab

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast

object ToastHelper {
    fun showCustomToast(context: Context, message: String) {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.custom_toast, null)

        val text: TextView = layout.findViewById(R.id.tvToastText)
        text.text = message

        with(Toast(context)) {
            duration = Toast.LENGTH_SHORT
            view = layout
            show()
        }
    }
}