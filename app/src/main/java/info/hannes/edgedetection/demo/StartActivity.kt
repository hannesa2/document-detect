package info.hannes.edgedetection.demo

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import info.hannes.logcat.BothLogActivity
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        buttonScan.setOnClickListener { startActivity(Intent(this, PreviewActivity::class.java)) }
        buttonLog.setOnClickListener { startActivity(Intent(this, BothLogActivity::class.java)) }
        buttonCrash.setOnClickListener {
            Toast.makeText(this, "force crash ${info.hannes.logcat.BuildConfig.VERSIONNAME}", Toast.LENGTH_SHORT).show()
            Handler(Looper.getMainLooper()).postDelayed({ throw RuntimeException("Test Crash ${info.hannes.logcat.BuildConfig.VERSIONNAME}") }, 3000)
        }

        textBuildType.text = "BuildType     : ${BuildConfig.BUILD_TYPE}"
        textAppVersion.text = "App version   : ${BuildConfig.VERSION_NAME}"
        textOpenCVVersion.text = "OpenCV version: ${org.opencv.BuildConfig.VERSION_NAME}"
    }

}
