package com.example.reading_cycle.post

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.R
import com.example.reading_cycle.databinding.FragmentAddSalePostBinding

class AddSalePostFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var fragmentAddSalePostBinding: FragmentAddSalePostBinding

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1001
        const val REQUEST_PICK_IMAGE = 1002
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentAddSalePostBinding = FragmentAddSalePostBinding.inflate(inflater)
        mainActivity.hideBottomNavigation()

        // 뒤로 가기 버튼 클릭 리스너
        fragmentAddSalePostBinding.toolbarLayoutAddSalePost.setNavigationOnClickListener {
            mainActivity.removeFragment(MainActivity.ADD_SALE_POST_FRAGMENT)
        }

        // 카드뷰 클릭 이벤트 처리
        fragmentAddSalePostBinding.cardViewAddSalePostImg.setOnClickListener {
            showImageSourceDialog()
        }

        return fragmentAddSalePostBinding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_PICK_IMAGE -> {
                if (resultCode == RESULT_OK) {
                    val selectedImageUri = data?.data
                    selectedImageUri?.let { uri ->
                        val imageBitmap = uriToBitmap(uri)
                        imageBitmap?.let { bitmap ->
                            val resizedBitmap = resizeBitmap(bitmap)
                            fragmentAddSalePostBinding.imgAddSalePost1.setImageBitmap(resizedBitmap)
                        }
                    }
                }
            }

            REQUEST_IMAGE_CAPTURE -> {
                if (resultCode == RESULT_OK) {
                    // 이미지 캡처의 경우 URI가 아닌 비트맵을 반환하므로 바로 사용 가능
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    val resizedBitmap = resizeBitmap(imageBitmap)
                    fragmentAddSalePostBinding.imgAddSalePost1.setImageBitmap(resizedBitmap)
                }
            }
        }
    }
    private fun resizeBitmap(bitmap: Bitmap): Bitmap {
        val targetWidth = fragmentAddSalePostBinding.imgAddSalePost1.width
        val targetHeight = fragmentAddSalePostBinding.imgAddSalePost1.height

        val width = bitmap.width
        val height = bitmap.height

        val scaleX = targetWidth.toFloat() / width
        val scaleY = targetHeight.toFloat() / height
        val scaleFactor = Math.max(scaleX, scaleY)

        val scaledWidth = scaleFactor * width
        val scaledHeight = scaleFactor * height

        return Bitmap.createScaledBitmap(bitmap, scaledWidth.toInt(), scaledHeight.toInt(), true)
    }

    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun showImageSourceDialog() {
        val items = arrayOf("갤러리", "카메라")
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setItems(items) { dialog, which ->
            when (which) {
                0 -> openGallery()
                1 -> openCamera()
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_PICK_IMAGE)
    }

    private fun openCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.CAMERA),
                REQUEST_IMAGE_CAPTURE
            )
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun showBookTypeMenu(view: View) {
        // XML에서 정의한 팝업 메뉴를 인플레이트
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.popup_menu_add_post_book_type, popupMenu.menu)

        // 팝업 메뉴 아이템 클릭 리스너 설정
        popupMenu.setOnMenuItemClickListener { menuItem ->
            // 각 메뉴 아이템에 대한 처리 추가
            when (menuItem.itemId) {
                R.id.menuNovel -> {
                    updateButtonText("소설") }
                R.id.menuPoetry -> {
                    updateButtonText("시") }
                R.id.menuEssay -> {
                    updateButtonText("에세이") }
                R.id.menuClassic -> {
                    updateButtonText("고전") }
                R.id.menuComic -> {
                    updateButtonText("만화") }
                R.id.menuChildren -> {
                    updateButtonText("어린이") }
                R.id.menuToddler -> {
                    updateButtonText("유아") }
                R.id.menuSelfDevelopment -> {
                    updateButtonText("자기계발") }
                R.id.menuReference -> {
                    updateButtonText("학습/참고서") }
                R.id.menuMajor -> {
                    updateButtonText("전공서") }
                R.id.menuCooking -> {
                    updateButtonText("요리/제빵") }
                R.id.menuLanguage -> {
                    updateButtonText("외국어") }
                R.id.menuSocialScience -> {
                    updateButtonText("사회/과학") }
                R.id.menuArt -> {
                    updateButtonText("예술") }
                R.id.menuReligion -> {
                    updateButtonText("종교") }
                R.id.menuEconomics -> {
                    updateButtonText("경제/경영") }
                R.id.menuHealthTravel -> {
                    updateButtonText("건강/여행") }
                R.id.menuHistory -> {
                    updateButtonText("역사") }
                R.id.menuPhilosophy -> {
                    updateButtonText("철학") }
                R.id.menuOther -> {
                    updateButtonText("기타") }
            }
            true
        }
        // 팝업 메뉴 표시
        popupMenu.show()
    }

    private fun updateButtonText(text: String) {
        fragmentAddSalePostBinding.btnAddSalePostType1.text = text
    }
}