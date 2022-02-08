package com.nb.myway.utils

import android.app.Dialog
import android.content.Context
import android.widget.Toast


fun Context.toast(message: String) =
    Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();