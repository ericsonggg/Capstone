package com.example.coolerthanyou.ui

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.widget.TextView
import android.widget.Toast
import com.example.coolerthanyou.R

/**
 * Box Selection Class
 *
 * This class creates an alert dialog given some information about what to display
 *
 * @param Int the current Index which should be pre-selected on the list
 * @param Array<CharSequence> the list of values to display
 * @constructor Stores the current index and the values to display
 */
class BoxSelector(var _defaultCheckedValue: Int, var _boxValues: Array<CharSequence>) {
    fun getAlertDialog(currentContext: Context, boxValue : TextView) : AlertDialog.Builder{
        val builder = AlertDialog.Builder(currentContext)
        builder.setTitle(currentContext.getText(R.string.quick_access_drawer_box_selection_text))

        if (_defaultCheckedValue >= _boxValues.size) {
            _defaultCheckedValue = 0;
        }

        val selectionClick = { dialog : DialogInterface, which: Int ->
            boxValue.text = _boxValues[which]
            _defaultCheckedValue = which
            Toast.makeText(currentContext,
                currentContext.getText(R.string.quick_access_drawer_update_notification), Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        builder.setSingleChoiceItems(_boxValues, _defaultCheckedValue, selectionClick)
        return builder
    }
}