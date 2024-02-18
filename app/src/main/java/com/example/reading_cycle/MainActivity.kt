package com.example.reading_cycle

import android.os.Bundle
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.reading_cycle.databinding.ActivityMainBinding
import com.example.reading_cycle.location.model.LocSetFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.reading_cycle.chat.ChatListFragment


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

   private var mMap: GoogleMap? = null
    var newFragment: Fragment? = null
    var oldFragment: Fragment? = null
    private lateinit var mainBinding: ActivityMainBinding
  
    companion object{
        val POST_MAIN_FRAGMENT = "PostMainFragment"
        val ADD_SALE_POST_FRAGMENT = "AddSalePostFragment"
        val ADD_SWAP_POST_FRAGMENT = "AddSwapPostFragment"
        val SALE_POST_FRAGMENT = "SalePostFragment"
        val SWAP_POST_FRAGMENT = "SwapPostFragment"
        val CHAT_LIST_FRAGMENT = "ChatListFragment"
        val LOC_SET_FRAGMENT = "LocSetFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)

        // 기본 ActionBar를 숨깁니다.
        supportActionBar?.hide()

        replaceFragment(POST_MAIN_FRAGMENT, false, null)
    }

        fun replaceFragment(name:String, addToBackStack:Boolean, bundle:Bundle? = null) {

            SystemClock.sleep(200)

            // Fragment 교체 상태로 설정한다.
            val fragmentTransaction = supportFragmentManager.beginTransaction()

            // newFragment 에 Fragment가 들어있으면 oldFragment에 넣어준다.
            if(newFragment != null){
                oldFragment = newFragment
            }

            // 새로운 Fragment를 담을 변수
            newFragment = when(name){
                POST_MAIN_FRAGMENT -> PostMainFragment()
                ADD_SALE_POST_FRAGMENT -> AddSalePostFragment()
                ADD_SWAP_POST_FRAGMENT -> AddSwapPostFragment()
                SALE_POST_FRAGMENT -> SalePostFragment()
                SWAP_POST_FRAGMENT -> SwapPostFragment()
                LOC_SET_FRAGMENT -> LocSetFragment()
                else -> Fragment()
            }

            // Fragment를 교체한다.
            fragmentTransaction.replace(R.id.hostFragmentMain, newFragment!!)

            if (addToBackStack == true) {
                // Fragment를 Backstack에 넣어 이전으로 돌아가는 기능이 동작할 수 있도록 한다.
                fragmentTransaction.addToBackStack(name)
            }

            // 교체 명령 동작.
            fragmentTransaction.commit()
        }
        
         // Handle the Google Map when it's ready
        override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val SEOUL = LatLng(37.556, 126.97)
        val markerOptions = MarkerOptions()
        markerOptions.position(SEOUL)
        markerOptions.title("서울")
        markerOptions.snippet("한국 수도")
        mMap?.addMarker(markerOptions)
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 10f))
    }
 }

