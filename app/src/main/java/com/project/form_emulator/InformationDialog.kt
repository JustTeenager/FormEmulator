package com.project.form_emulator

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import androidx.fragment.app.DialogFragment

class InformationDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder=AlertDialog.Builder(context)
        val v=LayoutInflater.from(context).inflate(R.layout.dialog_information,null)
        v.findViewById<Button>(R.id.back_btn).setOnClickListener { dismiss() }
        return builder.setView(v).create()
    }
}