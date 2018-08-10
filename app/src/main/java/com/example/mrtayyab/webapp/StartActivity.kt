package com.example.mrtayyab.webapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity() {



    lateinit var mWebView : WebView
    lateinit var mToolbar : Toolbar
    lateinit var mProgressBar : ProgressBar

    lateinit var listItems : Array<String>
    lateinit var categoryList : Array<String>

    val MY_PERMISSION_REQUEST_CODE = 123


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)


        mWebView= findViewById(R.id.myWebView)
        mToolbar= findViewById(R.id.myToolbar)
        mProgressBar= findViewById(R.id.myProgressBar)
        setSupportActionBar(mToolbar)
        mProgressBar.max = 100

        checkPermission()  // check permission

        listItems = resources.getStringArray(R.array.choice_item)


        val myUrl = "https://www.puretoons.com/index.xhtml"
        checkConnectivity()// internet checker
        optionLinkOpen(myUrl)


        mWebView.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->

            if(mWebView.url.contains(".3gp") || mWebView.url.contains(".mp4")){

                val videoPath = URLUtil.guessFileName(url, contentDisposition, mimetype)

                val mBuilder = AlertDialog.Builder(this)
                mBuilder.setTitle("Choose an item")

                mBuilder.setSingleChoiceItems(listItems, -1, DialogInterface.OnClickListener { dialogInterface, i ->
                    // mResult.setText(listItems[i])

                    if (listItems[i] == "Online") {
                        Toast.makeText(this, "Your Choice is Online", Toast.LENGTH_SHORT).show()

                        val i = Intent(Intent.ACTION_VIEW)
                        i.data = Uri.parse(url)
                        startActivity(i)

                    } else if (listItems[i] == "Download") {
                        Toast.makeText(this, "Your Choice is download", Toast.LENGTH_SHORT).show()


                        val fileName = URLUtil.guessFileName(url, contentDisposition, mimetype) //returns a string of the name of the file THE IMPORTANT PART

                        val myRequest = DownloadManager.Request(Uri.parse(url))
                        myRequest.allowScanningByMediaScanner()

                        myRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        myRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                        val myManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

                        myManager.enqueue(myRequest)
                        myDownloadingAlert(fileName)
                        Toast.makeText(this, "Your file is Downloading... ${fileName}", Toast.LENGTH_SHORT).show()

                    }
                    dialogInterface.dismiss()
                })
                val mDialog = mBuilder.create()
                mDialog.show()

            }
        }

    }

    fun linkPassing(){

        val CartoonSeriesHindiDubbed = " http://puretoons.com/site_cartoon_series_hindi_dubbed.xhtml"
        val  CartoonSeriesBanglaDubbed = "http://puretoons.com/site_4.xhtml"

        categoryList = resources.getStringArray(R.array.cartoonsCatergory)

        val mBuilder = AlertDialog.Builder(this)
        mBuilder.setTitle("Choose a Category")
        mBuilder.setSingleChoiceItems(categoryList, -1, DialogInterface.OnClickListener { dialogInterface, i ->
            // mResult.setText(listItems[i])

            if (categoryList[i] == "Cartoon Series Hindi Dubbed") {
//                Toast.makeText(applicationContext , "you select ${myHindi}" , Toast.LENGTH_SHORT).show()
                optionLinkOpen(CartoonSeriesHindiDubbed)

            } else if (categoryList[i] == "Cartoon Series Bangla Dubbed") {

//                Toast.makeText(applicationContext , "you select ${myAnime}" , Toast.LENGTH_SHORT).show()
                optionLinkOpen(CartoonSeriesBanglaDubbed)
            }

            dialogInterface.dismiss()
        })
        val mDialog = mBuilder.create()
        mDialog.show()
    }


    fun myDownloadingAlert(filename : String ){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.app_name)
        builder.setIcon(R.mipmap.ic_launcher)
        builder.setMessage("Your Downloading is start ${filename}" )
                .setCancelable(false)
                //.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id -> finish() })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        val alert = builder.create()
        alert.show()

    }


    @SuppressLint("SetJavaScriptEnabled")
    fun optionLinkOpen(my : String){

        mWebView.loadUrl(my)

        mWebView.settings.javaScriptEnabled = true

        mWebView.webViewClient = WebViewClient()
        this.mProgressBar.progress = 0

        mWebView.webChromeClient = object : WebChromeClient() {

            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                mProgressBar.progress = newProgress
            }

            override fun onReceivedTitle(view: WebView, title: String) {
                super.onReceivedTitle(view, title)
                supportActionBar!!.title = title
            }

            override fun onReceivedIcon(view: WebView, icon: Bitmap) {
                super.onReceivedIcon(view, icon)
            }

        }
    }

    private fun checkConnectivity(): Int {
        var enabled = true

        var internet =2

        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connectivityManager.activeNetworkInfo

        if (info == null || !info.isConnected || !info.isAvailable) {
            internet = 0//sin conexion
            Toast.makeText(applicationContext, "Internet connection is not available...", Toast.LENGTH_SHORT).show()

            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.app_name)
            builder.setIcon(R.mipmap.ic_launcher)
            builder.setMessage("Internet connection is not available " )
                    .setCancelable(false)
                    .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id -> finish() })
                    .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
            val alert = builder.create()
            alert.show()

            enabled = false
        } else {

            internet = 1//conexión
            Toast.makeText(applicationContext, "Internet is available...", Toast.LENGTH_SHORT).show()

        }

        return internet
    }

    override fun onBackPressed() {
        if(mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
                exitAlert()
        }
    }

    fun exitAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.app_name)
        builder.setIcon(R.mipmap.ic_launcher)
        builder.setMessage("Are you sure to exit? " )
                .setCancelable(false)
                .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id -> finish() })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        val alert = builder.create()
        alert.show()
    }


    fun onForwardPressed() {

        if (mWebView.canGoForward()) {

            mWebView.goForward()
        } else {
            Toast.makeText(this, "Can't go further !", Toast.LENGTH_SHORT).show()
        }
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {

                Toast.makeText(applicationContext , "Home " , Toast.LENGTH_SHORT).show()
                val homeUrl = "https://www.puretoons.com/index.xhtml"

                optionLinkOpen(homeUrl)

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_back -> {
                Toast.makeText(applicationContext , "Press Back " , Toast.LENGTH_SHORT).show()

                onBackPressed()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_forward -> {
                Toast.makeText(applicationContext , "Press Forward " , Toast.LENGTH_SHORT).show()
                onForwardPressed()
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_refresh -> {
                Toast.makeText(applicationContext , "Refresh " , Toast.LENGTH_SHORT).show()
                mWebView.reload()
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_menu -> {
                Toast.makeText(applicationContext , "Menu " , Toast.LENGTH_SHORT).show()
                linkPassing()
                return@OnNavigationItemSelectedListener true
            }

        }
        false
    }

     fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // show an alert dialog
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("Write external storage permission is required.")
                    builder.setTitle("Please grant permission")
                    builder.setPositiveButton("OK") { dialogInterface, i ->
                        ActivityCompat.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                MY_PERMISSION_REQUEST_CODE
                        )
                    }
                    builder.setNeutralButton("Cancel", null)
                    val dialog = builder.create()
                    dialog.show()
                } else {
                    // Request permission
                    ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            MY_PERMISSION_REQUEST_CODE
                    )
                }
            } else {
                // Permission already granted
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSION_REQUEST_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                } else {
                    // Permission denied
                }
            }
        }
    }

}
