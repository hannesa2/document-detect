package info.hannes.edgedetection.demo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar
import info.hannes.github.AppUpdateHelper
import info.hannes.edgedetection.*
import info.hannes.edgedetection.activity.ScanActivity
import info.hannes.edgedetection.utils.*
import kotlinx.android.synthetic.main.activity_preview.*
import timber.log.Timber
import java.io.File

class PreviewActivity : AppCompatActivity() {
    private var filePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)

        startScan()

        AppUpdateHelper.checkForNewVersion(
                this,
                BuildConfig.GIT_REPOSITORY,
                BuildConfig.VERSION_NAME
        )
    }

    private fun startScan() {
        val intent = Intent(this, ScanActivity::class.java)
        // optional, otherwise it's stored internal
        intent.putExtra(ScanConstants.IMAGE_PATH, getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString())
        intent.putExtra(ScanConstants.TIME_HOLD_STILL, 700L)
        startActivityForResult(intent, REQUEST_CODE)
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                data?.extras?.let { bundle ->
                    filePath = bundle.getString(ScanConstants.SCANNED_RESULT)
                    filePath?.let {
                        val baseBitmap = it.decodeBitmapFromFile()
                        scanned_image.setImageBitmap(baseBitmap)
                        scanned_image.scaleType = ImageView.ScaleType.FIT_CENTER

                        textDensity.text = "Density ${baseBitmap.density}"
                        textDimension.text = "${baseBitmap.width} / ${baseBitmap.height}"

                        showSnackbar(it)
                        Timber.i(it)
                    }

                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                finish()
            }
        }

        buttonOpenExtern.setOnClickListener {
            externalCacheDir?.let { path ->
                filePath?.let { file ->
                    PreviewActivity@ this.viewPdf(File(file).createPdf(path))
                }
            }
        }

        buttonRotate.setOnClickListener {
            filePath?.let {
                var baseBitmap = it.decodeBitmapFromFile()
                baseBitmap = baseBitmap.rotate(90f)
                scanned_image.setImageBitmap(baseBitmap)
                this.storeBitmap(baseBitmap, File(it))
            }
        }
    }

    private fun showSnackbar(text: String) {
        var viewPos: View? = findViewById(R.id.coordinatorLayout)
        if (viewPos == null) {
            viewPos = findViewById(android.R.id.content)
        }
        val snackbar = Snackbar.make(viewPos!!, text, Snackbar.LENGTH_INDEFINITE)
        val view = snackbar.view
        when (val params = view.layoutParams) {
            is CoordinatorLayout.LayoutParams -> {
                val paramsC = view.layoutParams as CoordinatorLayout.LayoutParams
                paramsC.gravity = Gravity.CENTER_VERTICAL
                view.layoutParams = paramsC
                snackbar.show()
            }
            is FrameLayout.LayoutParams -> {
                val paramsC = view.layoutParams as FrameLayout.LayoutParams
                paramsC.gravity = Gravity.BOTTOM
                view.layoutParams = paramsC
                snackbar.show()
            }
            else -> {
                Toast.makeText(this, text + " " + params.javaClass.simpleName, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE = 101
    }
}