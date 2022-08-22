package com.c1c

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast

inline fun <reified T : Activity> Activity.launchActivity(extras: Bundle? = null, noinline block: Intent.() -> Unit = {}) {
    val intent = Intent(this, T::class.java)
    if (extras != null) intent.putExtras(extras)
    intent.block()
    startActivity(intent)
}

inline fun <reified T : Context> Context.launchActivity(extras: Bundle? = null, noinline block: Intent.() -> Unit = {}) {
    val intent = Intent(this, T::class.java)
    if (extras != null) intent.putExtras(extras)
    intent.block()
    startActivity(intent)
}

fun Activity.toastValidation(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}