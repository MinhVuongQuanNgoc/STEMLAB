package utils

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.material.R
import com.google.android.material.snackbar.Snackbar

object UIHelper {
    fun showNotification(view: View, message: String) {
        val snackbar = Snackbar.make(view, "", Snackbar.LENGTH_SHORT)
        val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout

        // Hide default Snackbar text
        val defaultTextView = snackbarLayout.findViewById<TextView>(R.id.snackbar_text)
        defaultTextView.visibility = View.INVISIBLE

        // Inflate custom pill view
        val customView = LayoutInflater.from(view.context).inflate(com.example.stemlab.R.layout.custom_toast, null)
        val tvMessage = customView.findViewById<TextView>(com.example.stemlab.R.id.tvToastText)
        tvMessage.text = message

        // Make the snackbar container transparent and center it
        snackbarLayout.setBackgroundColor(Color.TRANSPARENT)
        snackbarLayout.setPadding(0, 0, 0, 0)
        snackbarLayout.elevation = 0f

        val params = snackbarLayout.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        params.width = FrameLayout.LayoutParams.WRAP_CONTENT
        params.bottomMargin = 200 // Position it like a Toast
        snackbarLayout.layoutParams = params

        snackbarLayout.addView(customView, 0)
        snackbar.show()
    }
}