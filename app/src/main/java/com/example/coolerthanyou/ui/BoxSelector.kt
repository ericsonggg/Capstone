package com.example.coolerthanyou.ui

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.coolerthanyou.R

/**
 * Box Selection Class
 *
 * This class creates an alert dialog given some information about what to display
 *
 * @param Array<CharSequence> the list of values to display
 * @param MainViewModel the main view model with which we access live data
 * @constructor Stores the current index and the values to display
 */
class BoxSelector(private var _boxValues: Array<CharSequence>, private val _mainViewModel: MainViewModel) {
    /**
     * Gets an alert dialog based on previously populated data
     * @param Context context for creation of dialogs and access of resources
     * @param TextView the field which represents the box value
     * @return An alert dialog builder.
     */
    fun getAlertDialog(currentContext: Context, boxValue : TextView) : AlertDialog.Builder{
        var currentCheckedValue = 0;
        _mainViewModel.getBoxValue().observe(currentContext as AppCompatActivity, Observer {
            currentCheckedValue = it
        })

        val builder = AlertDialog.Builder(currentContext)
        builder.setTitle(currentContext.getText(R.string.quick_access_drawer_box_selection_text))

        if (currentCheckedValue >= _boxValues.size) {
            currentCheckedValue = 0;
        }

        val selectionClick = { dialog : DialogInterface, which: Int ->
            boxValue.text = _boxValues[which]
            Toast.makeText(currentContext,
                currentContext.getText(R.string.quick_access_drawer_update_notification), Toast.LENGTH_SHORT).show()

            _mainViewModel.setBoxValue(MutableLiveData(which))
            dialog.dismiss()
        }

        builder.setSingleChoiceItems(_boxValues, currentCheckedValue, selectionClick)
        return builder
    }
}