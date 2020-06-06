package com.example.coolerthanyou.ui

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.widget.TextView
import android.widget.Toast
import com.example.coolerthanyou.R

class BoxSelector (defaultCheckedValue: Int, boxValues: Array<CharSequence>){
    var currentBoxValue = 0;
    var boxValueList = arrayOf<CharSequence>()

    init{
        currentBoxValue = defaultCheckedValue
        boxValueList = boxValues
    }

    fun getAlertDialog(currentContext: Context, boxValue : TextView) : AlertDialog.Builder{
        val builder = AlertDialog.Builder(currentContext)
        builder.setTitle("Select Box")

        val selectionClick = { dialog : DialogInterface, which: Int ->
            boxValue.text = boxValueList[which]
            currentBoxValue = which
            Toast.makeText(currentContext,
                currentContext.getText(R.string.quick_access_drawer_update_notification), Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        builder.setSingleChoiceItems(boxValueList,currentBoxValue, selectionClick)
        return builder
    }
}