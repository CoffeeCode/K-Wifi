package com.example.andre.userinterface

import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import info.ap.pentax.Dir
import info.ap.pentax.PentaxCommunicator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import android.util.Log
import android.widget.TextView
import android.widget.Toast


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, SdPicturesFragment.OnListFragmentInteractionListener, GalleryFragment.OnListFragmentInteractionListener {

    override fun onListFragmentInteraction(item: String?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        val checkConnectionHandler = Handler()
        val handlerDelay : Long =  5000

        checkConnectionHandler.postDelayed(object : Runnable {
            override fun run() {
                val textView = findViewById(R.id.headerTextLevel1) as TextView
                Log.d("petnax communicator", "checking connection to camera")

                //do something
                val isConnected = checkConnectionHandler.postDelayed(this, handlerDelay)
                val pkConnector = PentaxConnector(this@MainActivity)

                /*if(pkConnector.isConnectionAlive()) {
                    textView.setText("Connected to Pentax K1")
                    Log.d("petnax communicator", "Connected")
                }
                else {
                    Log.d("petnax communicator", "NOT Connected")
                }*/
            }
        }, handlerDelay)

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        var fragment : Fragment

        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {

                /**
                 *  @todo 1. Check that Device is connected via WIFI, if not print error message
                 */

                /**
                 *  2. Get the Data from the PentaxCommunicator
                 */
                val pkCommunicator = PentaxCommunicator()
                var imageList : ArrayList<Dir>

                doAsync {

                    Snackbar.make(findViewById(R.id.content_main), "Reading image list. This could take a while.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()

                    imageList = ArrayList(pkCommunicator.getImageList())

                    var imageListForAdapter: ArrayList<String>
                    imageListForAdapter = arrayListOf<String>()

                    for(i in imageList.indices) {
                        imageListForAdapter.add(imageList[i].name)
                    }

                    uiThread {
                        /**
                         * 3. Start the new fragment and transmit the imageList in a bundle
                         */
                        var bundle = Bundle()
                        bundle.putStringArrayList("imageList", imageListForAdapter)
                        fragment = SdPicturesFragment()
                        fragment.arguments = bundle
                        val ft : FragmentTransaction = supportFragmentManager.beginTransaction()
                        ft.replace(R.id.content_main, fragment)
                        ft.commit()
                    }
                }

            }
            R.id.nav_gallery -> {

                //Handle the gallery action
                fragment = GalleryFragment()
                val ft : FragmentTransaction = supportFragmentManager.beginTransaction()
                ft.replace(R.id.content_main, fragment)
                ft.commit()
            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_connect -> {
                val pkConnector = PentaxConnector(this)
                pkConnector.connectToWPAWiFi("PENTAX_67A107", "A467A107")

            }
            R.id.nav_send -> {
                doAsync {
                    val pkCommunicator = PentaxCommunicator()
                    val text = pkCommunicator.checkConnection()

                    uiThread {
                        Toast.makeText(applicationContext, "Verbindung: " + text, Toast.LENGTH_LONG).show()
                    }
                }

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

}
