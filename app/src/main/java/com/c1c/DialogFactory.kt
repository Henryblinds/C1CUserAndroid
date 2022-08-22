package com.c1c

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatDialog
import com.google.android.material.textview.MaterialTextView

class DialogFactory {
    companion object {
        @JvmStatic
        fun showLoadingDialog(context: Context): AppCompatDialog {
            val v: View = LayoutInflater.from(context).inflate(R.layout.loading, null)
            val dialog = AppCompatDialog(context)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            dialog.setContentView(v)
            return dialog
        }

        @JvmStatic
        fun showActionSelect(
            context: Context,
            msg: String,
            okMsg: String,
            cancelMsg: String,
            ls: ConfirmInterface
        ): AppCompatDialog {
            val v: View = LayoutInflater.from(context).inflate(R.layout.dialog_confirm_delete, null)
            val dialogData = AppCompatDialog(context)
            dialogData.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val llcancel = v.findViewById<View>(R.id.llno) as LinearLayout
            val llcomplete = v.findViewById<View>(R.id.llyes) as LinearLayout
            val tvpoperror = v.findViewById<View>(R.id.msginfo) as MaterialTextView
            val tvoktext = v.findViewById<View>(R.id.ok_text) as MaterialTextView
            val tvdeltext = v.findViewById<View>(R.id.del_text) as MaterialTextView

            tvpoperror.text = msg
            tvoktext.text = okMsg
            tvdeltext.text = cancelMsg

            llcancel.setOnClickListener {
                dialogData.dismiss()
                ls.clickCancel()
            }
            llcomplete.setOnClickListener {
                dialogData.dismiss()
                ls.clickOk()
            }
            dialogData.setContentView(v)

            return dialogData
        }
    }
}