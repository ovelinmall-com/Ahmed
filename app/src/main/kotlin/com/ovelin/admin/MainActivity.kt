package com.ovelin.admin

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import android.widget.LinearLayout
import android.view.Gravity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging

/**
 * شاشة واحدة بسيطة جداً:
 * 1) تطلب إذن الإشعارات (أندرويد 13+)
 * 2) تسجّل FCM token عند السيرفر تلقائياً
 * 3) بعد كده التطبيق ما محتاج يتفتح تاني — الإشعارات بتوصل مباشرة عبر الخدمة في الخلفية
 */
class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                registerFcmToken()
            } else {
                statusText.text = "لازم تسمح بالإشعارات عشان التطبيق يشتغل.\nافتح إعدادات التطبيق وفعّلها يدوياً."
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // واجهة بسيطة جداً بالكود مباشرة، بدون XML layout معقد
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(48, 48, 48, 48)
        }
        statusText = TextView(this).apply {
            textSize = 16f
            gravity = Gravity.CENTER
            text = "جاري التجهيز..."
        }
        layout.addView(statusText)
        setContentView(layout)

        ensureNotificationPermission()
    }

    private fun ensureNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (granted) {
                registerFcmToken()
            } else {
                statusText.text = "اسمح بالإشعارات عشان تستلم تنبيهات لوحة الأدمن"
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            // أندرويد أقدم من 13: الإذن ممنوح تلقائياً
            registerFcmToken()
        }
    }

    private fun registerFcmToken() {
        statusText.text = "جاري الربط مع السيرفر..."
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                AdminApi.registerToken(
                    token = token,
                    onSuccess = {
                        runOnUiThread {
                            statusText.text = "تم الربط بنجاح ✅\nالتطبيق شغال في الخلفية.\nممكن تقفل الشاشة دي عادي."
                        }
                    },
                    onError = { msg ->
                        runOnUiThread {
                            statusText.text = "فشل الربط بالسيرفر:\n$msg\n\nتأكد من اتصال الإنترنت وأعد فتح التطبيق."
                        }
                    }
                )
            }
            .addOnFailureListener {
                statusText.text = "فشل الحصول على توكن الإشعارات. تأكد إن google-services.json صحيح."
            }
    }
}
