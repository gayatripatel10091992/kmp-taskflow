package com.gayatripatel.taskflow

import android.os.Build

actual fun platformName(): String = "Android ${Build.VERSION.RELEASE}"
