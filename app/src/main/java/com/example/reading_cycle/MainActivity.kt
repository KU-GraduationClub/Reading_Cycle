package com.example.reading_cycle

import android.os.Bundle
import android.os.SystemClock
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.reading_cycle.chat.ChatListFragment
import com.example.reading_cycle.databinding.ActivityMainBinding
import com.example.reading_cycle.friend.FriendMainFragment
import com.example.reading_cycle.library.LibraryMainFragment
import com.example.reading_cycle.library.LibraryMyFragment
import com.example.reading_cycle.location.LocSetFragment
import com.example.reading_cycle.location.LocReSetFragment
import com.example.reading_cycle.login.LoginMainFragment
import com.example.reading_cycle.login.PasswordResetFragment
import com.example.reading_cycle.login.UserRegisterFragment
import com.example.reading_cycle.notify.NotifyFragment
import com.example.reading_cycle.post.AddSalePostFragment
import com.example.reading_cycle.post.AddSwapPostFragment
import com.example.reading_cycle.post.PostMainFragment
import com.example.reading_cycle.post.SalePostFragment
import com.example.reading_cycle.post.SwapPostFragment

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    private var newFragment: Fragment? = null
    private var oldFragment: Fragment? = null

    companion object{
        const val POST_MAIN_FRAGMENT = "PostMainFragment"
        const val ADD_SALE_POST_FRAGMENT = "AddSalePostFragment"
        const val ADD_SWAP_POST_FRAGMENT = "AddSwapPostFragment"
        const val SALE_POST_FRAGMENT = "SalePostFragment"
        const val SWAP_POST_FRAGMENT = "SwapPostFragment"
        const val LOC_SET_FRAGMENT = "LocSetFragment"
        const val LOC_RESET_FRAGMENT = "LocResetFragment"
        const val LOGIN_MAIN_FRAGMENT = "LoginMainFragment"
        const val PASSWORD_RESET_FRAGMENT = "PasswordResetFragment"
        const val USER_REGISTER_FRAGMENT = "UserRegisterFragment"
        const val CHAT_LIST_FRAGMENT = "ChatListFragment"
        const val LIBRARY_MAIN_FRAGMENT = "LibraryMainFragment"
        const val LIBRARY_MY_FRAGMENT = "LibraryMyFragment"
        const val FRIEND_MAIN_FRAGMENT = "FriendMainFragment"
        const val NOTIFY_FRAGMENT = "NotifyFragment"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mainBinding.root
        setContentView(view)

        // 기본 ActionBar를 숨깁니다.
        supportActionBar?.hide()

        replaceFragment(LOGIN_MAIN_FRAGMENT, false, null)

        // 네비게이션 바 아이템 클릭 이벤트 처리
        mainBinding.bottomNavigation.setOnNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.bottom_main -> replaceFragment(POST_MAIN_FRAGMENT, true)
                R.id.bottom_chat -> replaceFragment(CHAT_LIST_FRAGMENT, true)
                R.id.bottom_frd -> replaceFragment(FRIEND_MAIN_FRAGMENT, true)
                R.id.bottom_lib -> replaceFragment(LIBRARY_MY_FRAGMENT, true)
            }
            true
        }
    }

    fun replaceFragment(name: String, addToBackStack: Boolean, bundle: Bundle? = null) {

        SystemClock.sleep(200)

        // Fragment 교체 상태로 설정한다.
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        
            // 새로운 Fragment를 담을 변수
            newFragment = when(name){
                POST_MAIN_FRAGMENT -> PostMainFragment()
                ADD_SALE_POST_FRAGMENT -> AddSalePostFragment()
                ADD_SWAP_POST_FRAGMENT -> AddSwapPostFragment()
                SALE_POST_FRAGMENT -> SalePostFragment()
                SWAP_POST_FRAGMENT -> SwapPostFragment()
                LOC_SET_FRAGMENT -> LocSetFragment()
                LOC_RESET_FRAGMENT -> LocReSetFragment()
                LOGIN_MAIN_FRAGMENT -> LoginMainFragment()
                USER_REGISTER_FRAGMENT -> UserRegisterFragment()
                PASSWORD_RESET_FRAGMENT -> PasswordResetFragment()
                CHAT_LIST_FRAGMENT -> ChatListFragment()
                LIBRARY_MAIN_FRAGMENT -> LibraryMainFragment()
                LIBRARY_MY_FRAGMENT -> LibraryMyFragment()
                FRIEND_MAIN_FRAGMENT -> FriendMainFragment()
                NOTIFY_FRAGMENT -> NotifyFragment()
                else -> Fragment()
            }

        // newFragment 에 Fragment가 들어있으면 oldFragment에 넣어준다.
        if (newFragment != null) {
            oldFragment = newFragment
        }

        // Fragment를 교체한다.
        fragmentTransaction.replace(R.id.hostFragmentMain, newFragment!!)

        if (addToBackStack) {
            // Fragment를 Backstack에 넣어 이전으로 돌아가는 기능이 동작할 수 있도록 한다.
            fragmentTransaction.addToBackStack(name)
        }

        // 교체 명령 동작.
        fragmentTransaction.commit()
    }

    // Fragment를 BackStack에서 제거.
    fun removeFragment(name: String) {
        supportFragmentManager.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    // BottomNavigationView Visible 메서드
    fun showBottomNavigation() {
        mainBinding.bottomNavigation.visibility = View.VISIBLE
    }
    fun hideBottomNavigation() {
        mainBinding.bottomNavigation.visibility = View.GONE
    }

    fun navigateToNotifyFragment() {
        replaceFragment(NOTIFY_FRAGMENT, true)
    }

    fun navigateToLocSetFragment() {
        replaceFragment(LOC_RESET_FRAGMENT, true)
    }
}
