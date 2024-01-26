package com.vanlam.furnitureshop.dialog

import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.vanlam.furnitureshop.R

fun Fragment.setupBottomSheetDialog(
    onSendClick: (String) -> Unit
) {
    val dialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
    val view = layoutInflater.inflate(R.layout.reset_password_dialog, null)
    dialog.setContentView(view)
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    dialog.show()

    val edResetPassword = view.findViewById<EditText>(R.id.edResetPassword)
    val btnCancelResetPassword = view.findViewById<Button>(R.id.btnCancelResetPass)
    val btnSendResetPassword = view.findViewById<Button>(R.id.btnSendResetPass)

    btnSendResetPassword.setOnClickListener {
        val email = edResetPassword.text.trim().toString()
        onSendClick(email)
        dialog.dismiss()
    }

    btnCancelResetPassword.setOnClickListener {
        dialog.dismiss()
    }
}