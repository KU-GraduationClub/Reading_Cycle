package com.example.reading_cycle.post

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.R
import com.example.reading_cycle.databinding.FragmentAddSalePostBinding
import com.example.reading_cycle.post.model.BookState
import com.example.reading_cycle.post.model.BookType
import com.example.reading_cycle.post.model.SaleBookData
import com.example.reading_cycle.post.repository.AddSalePostRepository
import com.example.reading_cycle.post.vm.AddSalePostViewModel
import com.example.reading_cycle.post.vm.AddSalePostViewModelFactory

class AddSalePostFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var fragmentAddSalePostBinding: FragmentAddSalePostBinding
    private lateinit var viewModel: AddSalePostViewModel
    private var selectedCardIndex: Int? = null
    private var selectedFrameId: Int? = null
    private var selectedBookType: BookType? = null
    private var selectedBookState: BookState? = null

    private val cardViewIds = listOf(
        R.id.cardViewAddSalePostImg1,
        R.id.cardViewAddSalePostImg2,
        R.id.cardViewAddSalePostImg3,
        R.id.cardViewAddSalePostImg4,
        R.id.cardViewAddSalePostImg5
    )

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
        // ViewModelFactory 초기화
        val factory = AddSalePostViewModelFactory(AddSalePostRepository())
        // ViewModelProvider를 통해 ViewModel 인스턴스를 가져옴
        viewModel = ViewModelProvider(this, factory)[AddSalePostViewModel::class.java]

        // 뒤로 가기 버튼 클릭 리스너
        fragmentAddSalePostBinding.toolbarLayoutAddSalePost.setNavigationOnClickListener {
            mainActivity.removeFragment(MainActivity.ADD_SALE_POST_FRAGMENT)
        }

        // 각 카드뷰에 대한 클릭 이벤트 처리
        for (i in cardViewIds.indices) {
            fragmentAddSalePostBinding.root.findViewById<CardView>(cardViewIds[i]).setOnClickListener {
                showImageSourceDialog(i)
            }
        }

        // 책 종류 선택 버튼 클릭 리스너 설정
        fragmentAddSalePostBinding.btnAddSalePostType1.setOnClickListener {
            getBookTypeMenu(it)
        }

        // 각 프레임 레이아웃에 대한 클릭 이벤트 처리
        fragmentAddSalePostBinding.FrameAddSalePostVeryBad.setOnClickListener {
            selectFrame(R.id.FrameAddSalePostVeryBad)
        }

        fragmentAddSalePostBinding.FrameAddSalePostBad.setOnClickListener {
            selectFrame(R.id.FrameAddSalePostBad)
        }

        fragmentAddSalePostBinding.FrameAddSalePostCommon.setOnClickListener {
            selectFrame(R.id.FrameAddSalePostCommon)
        }

        fragmentAddSalePostBinding.FrameAddSalePostGood.setOnClickListener {
            selectFrame(R.id.FrameAddSalePostGood)
        }

        fragmentAddSalePostBinding.FrameAddSalePostVeryGood.setOnClickListener {
            selectFrame(R.id.FrameAddSalePostVeryGood)
        }

        // 버튼 클릭 이벤트 리스너 설정
        fragmentAddSalePostBinding.btnAddSalePostComplete.setOnClickListener {
            val saleData = collectInputData()
            viewModel.uploadSalePost(saleData) // ViewModel 인스턴스를 통해 메서드 호출
        }

        return fragmentAddSalePostBinding.root
    }

    private fun collectInputData(): SaleBookData {
        val title =  fragmentAddSalePostBinding.edtAddSalePostTitle.text.toString()
        val author =  fragmentAddSalePostBinding.edtAddSalePostAuthor.text.toString()
        val bookType = selectedBookType ?: throw IllegalStateException("Book type must be selected")
        val price =  fragmentAddSalePostBinding.edtAddSalePostPrice.text.toString().toLong()
        val regPrice =  fragmentAddSalePostBinding.edtAddSalePostRegPrice.text.toString().toLong()
        val bookState = determineBookState()
        val description =  fragmentAddSalePostBinding.edtAddSalePostExplain.text.toString()

        return SaleBookData(
            saleIdx = System.currentTimeMillis(), // 또는 서버에서 생성한 ID 사용
            saleBookPostImg = "", // 이미지 업로드 후 URL 설정 필요
            saleBookImg = listOf(), // 이미지 URL 리스트
            saleBookTitle = title,
            saleBookAuthor = author,
            saleBookType = bookType,
            saleBookPrice = price,
            saleBookRegPrice = regPrice,
            saleBookState = bookState,
            saleBookExplain = description
        )
    }

    // 도서 상태 선택 데이터 처리
    private fun determineBookState(): BookState {
        return selectedBookState ?: throw IllegalStateException("도서 상태 선택 필요")
    }

    private fun selectFrame(frameId: Int) {
        // 이전에 선택된 프레임 레이아웃의 선택 표시 해제
        selectedFrameId?.let { previousFrameId ->
            val previousFrameLayout = fragmentAddSalePostBinding.root.findViewById<FrameLayout>(previousFrameId)
            previousFrameLayout.background = ContextCompat.getDrawable(requireContext(), R.drawable.border_add_post_edit_text)
        }

        // 새로운 프레임 레이아웃에 선택 표시 추가
        val selectedFrameLayout = fragmentAddSalePostBinding.root.findViewById<FrameLayout>(frameId)
        selectedFrameLayout.setBackgroundColor(Color.GRAY)

        // 선택된 프레임 레이아웃의 ID 저장
        selectedFrameId = frameId

        // 프레임 ID에 따라 BookState 설정
        selectedBookState = when (frameId) {
            R.id.FrameAddSalePostVeryBad -> BookState.VERY_BAD
            R.id.FrameAddSalePostBad -> BookState.BAD
            R.id.FrameAddSalePostCommon -> BookState.COMMON
            R.id.FrameAddSalePostGood -> BookState.GOOD
            R.id.FrameAddSalePostVeryGood -> BookState.VERY_GOOD
            else -> null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_PICK_IMAGE -> {
                if (resultCode == RESULT_OK) {
                    val selectedImageUris = data?.clipData
                    selectedImageUris?.let { clipData ->
                        for (i in 0 until minOf(clipData.itemCount, cardViewIds.size)) { // 최대 5개까지만 처리
                            val imageUri = clipData.getItemAt(i).uri
                            val imageBitmap = uriToBitmap(imageUri)
                            imageBitmap?.let { bitmap ->
                                val resizedBitmap = resizeBitmap(bitmap)
                                val index = i
                                val imageViewId = fragmentAddSalePostBinding.root.findViewById<CardView>(cardViewIds[index])
                                    .getChildAt(0) // 각 카드뷰 안에 있는 ImageView를 가져옴
                                    .id
                                fragmentAddSalePostBinding.root.findViewById<ImageView>(imageViewId).setImageBitmap(resizedBitmap)
                                // 다음 번호의 카드뷰를 보여줌
                                if (index < cardViewIds.size - 1) {
                                    val nextCardViewId = cardViewIds[index + 1]
                                    fragmentAddSalePostBinding.root.findViewById<CardView>(nextCardViewId).visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                }
            }
            REQUEST_IMAGE_CAPTURE -> {
                if (resultCode == RESULT_OK) {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    val resizedBitmap = resizeBitmap(imageBitmap)
                    selectedCardIndex?.let { index ->
                        val imageViewId = fragmentAddSalePostBinding.root.findViewById<CardView>(cardViewIds[index])
                            .getChildAt(0) // 각 카드뷰 안에 있는 ImageView를 가져옴
                            .id
                        fragmentAddSalePostBinding.root.findViewById<ImageView>(imageViewId).setImageBitmap(resizedBitmap)
                        // 다음 번호의 카드뷰를 보여줌
                        if (index < cardViewIds.size - 1) {
                            val nextCardViewId = cardViewIds[index + 1]
                            fragmentAddSalePostBinding.root.findViewById<CardView>(nextCardViewId).visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    // 촬영 / 갤러리 선택 이미지 크기 조정
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

    // 촬영용 uri -> bitmap 변환
    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // 카드뷰 내부 이미지 추가용 다이얼로그
    private fun showImageSourceDialog(cardIndex: Int) {
        selectedCardIndex = cardIndex // 선택된 카드뷰의 인덱스 저장
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
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) // 다중 선택 허용
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PICK_IMAGE)
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

    // 도서 종류 선택용 다이얼로그
    private fun getBookTypeMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.popup_menu_add_post_book_type, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            selectedBookType = when (menuItem.itemId) {
                R.id.menuNovel -> BookType.NOVEL
                R.id.menuPoetry -> BookType.POETRY
                R.id.menuEssay -> BookType.ESSAY
                R.id.menuClassic -> BookType.CLASSIC
                R.id.menuComic -> BookType.COMIC
                R.id.menuChildren -> BookType.CHILDREN
                R.id.menuToddler -> BookType.TODDLER
                R.id.menuSelfDevelopment -> BookType.SELF_DEVELOPMENT
                R.id.menuReference -> BookType.REFERENCE
                R.id.menuMajor -> BookType.MAJOR
                R.id.menuCooking -> BookType.COOKING
                R.id.menuLanguage -> BookType.LANGUAGE
                R.id.menuSocialScience -> BookType.SOCIAL_SCIENCE
                R.id.menuArt -> BookType.ART
                R.id.menuReligion -> BookType.RELIGION
                R.id.menuEconomics -> BookType.ECONOMICS
                R.id.menuHealthTravel -> BookType.HEALTH_TRAVEL
                R.id.menuHistory -> BookType.HISTORY
                R.id.menuPhilosophy -> BookType.PHILOSOPHY
                R.id.menuOther -> BookType.OTHER
                else -> null
            }
            selectedBookType?.let {
                updateButtonText(showBookTypeText(it))
            }
            true
        }
        popupMenu.show()
    }

    private fun showBookTypeText(bookType: BookType): String {
        return when (bookType) {
            BookType.NOVEL -> "소설"
            BookType.POETRY -> "시"
            BookType.ESSAY -> "에세이"
            BookType.CLASSIC -> "고전"
            BookType.COMIC -> "만화"
            BookType.CHILDREN -> "어린이"
            BookType.TODDLER -> "유아"
            BookType.SELF_DEVELOPMENT -> "자기계발"
            BookType.REFERENCE -> "학습/참고서"
            BookType.MAJOR -> "전공서"
            BookType.COOKING -> "요리/제빵"
            BookType.LANGUAGE -> "외국어"
            BookType.SOCIAL_SCIENCE -> "사회/과학"
            BookType.ART -> "예술"
            BookType.RELIGION -> "종교"
            BookType.ECONOMICS -> "경제/경영"
            BookType.HEALTH_TRAVEL -> "건강/여행"
            BookType.HISTORY -> "역사"
            BookType.PHILOSOPHY -> "철학"
            BookType.OTHER -> "기타"
        }
    }

    private fun updateButtonText(text: String) {
        fragmentAddSalePostBinding.btnAddSalePostType1.text = text
    }
}