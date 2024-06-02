package com.elhady.superstore.ui.common

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.elhady.superstore.R


class ProgressDialog {
    companion object {
        fun createProgressDialog(context: Context): Dialog {
            val dialog = Dialog(context)
            val inflate =
                LayoutInflater.from(context).inflate(R.layout.progress_dialog_layout, null)
            dialog.setContentView(inflate)
            dialog.setCancelable(false)
            dialog.window?.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )
            return dialog
        }
    }
}