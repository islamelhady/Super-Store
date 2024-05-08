package com.elhady.superstore.ui.home

import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.elhady.superstore.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar


fun View.showSnakeBarError(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_INDEFINITE)
        .setAction(this.context.resources.getString(R.string.ok)) {}.setActionTextColor(
            ContextCompat.getColor(this.context, R.color.white)
        ).show()
}

fun View.showRetrySnakeBarError(message: String, retry: () -> Unit) {
    Snackbar.make(this, message, Snackbar.LENGTH_INDEFINITE)
        .setAction(this.context.resources.getString(R.string.retry)) { retry.invoke() }
        .setActionTextColor(
            ContextCompat.getColor(this.context, R.color.white)
        ).show()
}

fun View.showLoginSuccessDialog() {
    MaterialAlertDialogBuilder(this.context).setTitle("Register Success")
        .setMessage("We have sent you an email verification link. Please verify your email to login.")
        .setPositiveButton(
            "OK"
        ) { dialog, which ->
            dialog?.dismiss()
            this.findNavController().popBackStack()
        }.create().show()
}

fun View.showSentEmailSuccessDialog() {
    MaterialAlertDialogBuilder(this.context).setTitle("Reset Password")
        .setMessage("We have sent you an email to reset your password. Please check your email.")
        .setPositiveButton(
            "OK"
        ) { dialog, which ->
            dialog?.dismiss()
            this.findNavController().popBackStack()
        }.create().show()
}
