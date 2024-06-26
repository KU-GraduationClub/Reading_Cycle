package com.example.reading_cycle.post

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.R
import com.example.reading_cycle.databinding.FragmentAddSwapPostBinding
import com.example.reading_cycle.post.model.BookState
import com.example.reading_cycle.post.model.BookType
import com.example.reading_cycle.post.model.SwapBookData
import com.example.reading_cycle.post.repository.AddSwapPostRepository
import com.example.reading_cycle.post.vm.AddSwapPostViewModel
import com.example.reading_cycle.post.vm.AddSwapPostViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.text.NumberFormat
import java.util.Locale

class AddSwapPostFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var fragmentAddSwapPostBinding: FragmentAddSwapPostBinding
    private lateinit var viewModel: AddSwapPostViewModel
    private var selectedCardIndex: Int? = null
    private var selectedFrameId: Int? = null
    private var selectedBookType1: BookType? = null
    private var selectedBookType2: BookType? = null
    private var selectedBookState: BookState? = null
    private val selectedImages = mutableListOf<Bitmap>()

    private val cardViewIds = listOf(
        R.id.cardViewAddSwapPostImg1,
        R.id.cardViewAddSwapPostImg2,
        R.id.cardViewAddSwapPostImg3,
        R.id.cardViewAddSwapPostImg4,
        R.id.cardViewAddSwapPostImg5
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
        fragmentAddSwapPostBinding = FragmentAddSwapPostBinding.inflate(inflater)
        mainActivity.hideBottomNavigation()

        val factory = AddSwapPostViewModelFactory(AddSwapPostRepository())
        viewModel = ViewModelProvider(this, factory)[AddSwapPostViewModel::class.java]

        // 뒤로 가기 버튼 클릭 리스너
        fragmentAddSwapPostBinding.toolbarLayoutAddSwapPost.setNavigationOnClickListener {
            mainActivity.removeFragment(MainActivity.ADD_SWAP_POST_FRAGMENT)
        }

        // 각 카드뷰에 대한 클릭 이벤트 처리
        for (i in cardViewIds.indices) {
            fragmentAddSwapPostBinding.root.findViewById<CardView>(cardViewIds[i]).setOnClickListener {
                showImageSourceDialog(i)
            }
        }

        setupPriceEditText(fragmentAddSwapPostBinding.edtAddSwapPostRegPrice)

        // 책 종류 선택 버튼 클릭 리스너 설정
        fragmentAddSwapPostBinding.FrameAddSwapPost1.setOnClickListener {
            getBookTypeMenu(it,R.id.btnAddSwapPostType1)
        }
        fragmentAddSwapPostBinding.FrameAddSwapPost2.setOnClickListener {
            getBookTypeMenu(it,R.id.btnAddSwapPostType2)
        }

        // 각 프레임 레이아웃에 대한 클릭 이벤트 처리
        fragmentAddSwapPostBinding.FrameAddSwapPostVeryBad.setOnClickListener {
            selectFrame(R.id.FrameAddSwapPostVeryBad)
        }

        fragmentAddSwapPostBinding.FrameAddSwapPostBad.setOnClickListener {
            selectFrame(R.id.FrameAddSwapPostBad)
        }

        fragmentAddSwapPostBinding.FrameAddSwapPostCommon.setOnClickListener {
            selectFrame(R.id.FrameAddSwapPostCommon)
        }

        fragmentAddSwapPostBinding.FrameAddSwapPostGood.setOnClickListener {
            selectFrame(R.id.FrameAddSwapPostGood)
        }

        fragmentAddSwapPostBinding.FrameAddSwapPostVeryGood.setOnClickListener {
            selectFrame(R.id.FrameAddSwapPostVeryGood)
        }

        // 버튼 클릭 이벤트 리스너 설정
        fragmentAddSwapPostBinding.btnAddSwapPostComplete.setOnClickListener {
            lifecycleScope.launch {
                try {
                    // 완료 버튼 클릭 시 버튼 비활성화
                    fragmentAddSwapPostBinding.btnAddSwapPostComplete.isEnabled = false
                    val colorStateList = ColorStateList.valueOf(Color.GRAY)
                    fragmentAddSwapPostBinding.btnAddSwapPostComplete.backgroundTintList = colorStateList

                    val swapData = collectInputData()
                    viewModel.uploadSwapPost(swapData)
                } catch (e: IllegalStateException) {
                    showSnackbar(e.message ?: "빈 칸 없이 작성해주세요.")
                } catch (e: Exception) {
                    showSnackbar("게시글 등록에 실패했습니다. 다시 시도해주세요.")
                } finally {
                    fragmentAddSwapPostBinding.btnAddSwapPostComplete.isEnabled = true
                    fragmentAddSwapPostBinding.btnAddSwapPostComplete.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#CF8127"))
                }
            }
        }

        // 뷰모델에서 uploadResult 결과에 따른 동작 수행
        viewModel.uploadResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                showSnackbar("게시글이 성공적으로 등록되었습니다.")
                // PostMainFragment로 이동, RecyclerView 갱신
                mainActivity.removeFragment(MainActivity.ADD_SWAP_POST_FRAGMENT)
                mainActivity.navigateToPostMainFragment()
            } else {
                showSnackbar("게시글 등록에 실패했습니다. 다시 시도해주세요")
            }
        }

        return fragmentAddSwapPostBinding.root
    }

    private suspend fun collectInputData(): SwapBookData {
        val title =  fragmentAddSwapPostBinding.edtAddSwapPostTitle.text.toString()
        val author =  fragmentAddSwapPostBinding.edtAddSwapPostAuthor.text.toString()
        val bookType = selectedBookType1 ?: throw IllegalStateException("판매 도서 종류를 선택해주세요")
        val bookSwapType = selectedBookType2 ?: throw IllegalStateException("교환할 도서 종류를 선택해주세요")
        val regPrice = fragmentAddSwapPostBinding.edtAddSwapPostRegPrice.text.toString()
        val bookState = selectedBookState ?: throw IllegalStateException("도서 상태를 선택해주세요")
        val description =  fragmentAddSwapPostBinding.edtAddSwapPostExplain.text.toString()

        if (title.isBlank()) throw IllegalStateException("제목을 입력하세요.")
        if (author.isBlank()) throw IllegalStateException("작가를 입력하세요.")
        if (regPrice.isBlank()) throw IllegalStateException("가격을 입력하세요.")
        if (description.isBlank()) throw IllegalStateException("설명을 입력하세요.")
        if (selectedImages.isEmpty()) throw IllegalStateException("최소 한 장의 이미지를 등록하세요.")

        val imageUrls = withContext(Dispatchers.IO) {
            uploadImagesAndGetUrls(selectedImages)
        }

        return SwapBookData(
            swapIdx = System.currentTimeMillis(), // 또는 서버에서 생성한 ID 사용
            swapBookPostImg =imageUrls.firstOrNull() ?: "",
            swapBookImg = imageUrls,
            swapBookTitle = title,
            swapBookAuthor = author,
            swapBookType = bookType,
            bookSwapType = bookSwapType,
            swapBookRegPrice = regPrice,
            swapBookState = bookState,
            swapBookExplain = description
        )
    }

    private suspend fun uploadImagesAndGetUrls(images: List<Bitmap>): List<String> = withContext(Dispatchers.IO) {
        val urls = mutableListOf<String>()
        val storage = FirebaseStorage.getInstance().reference

        images.forEachIndexed { index, bitmap ->
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val data = byteArrayOutputStream.toByteArray()
            val filePath = "images/${System.currentTimeMillis()}_$index.jpg"
            val ref = storage.child(filePath)

            try {
                val uploadTask = ref.putBytes(data).await()
                val downloadUrl = uploadTask.storage.downloadUrl.await().toString()
                urls.add(downloadUrl)

                // 이미지 업로드 확인을 위해 URL 로그로 출력, Storage 규칙 적용 후 제거
                Log.d("ImageUpload", "Image $index uploaded successfully. URL: $downloadUrl")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("ImageUpload", "Image $index upload failed: ${e.message}")
            }
        }

        return@withContext urls
    }

    // 도서 상태 선택 데이터 처리
    private fun determineBookState(): BookState {
        return selectedBookState ?: throw IllegalStateException("도서 상태 선택 필요")
    }

    private fun selectFrame(frameId: Int) {
        // 이전에 선택된 프레임 레이아웃의 선택 표시 해제
        selectedFrameId?.let { previousFrameId ->
            val previousFrameLayout = fragmentAddSwapPostBinding.root.findViewById<FrameLayout>(previousFrameId)
            previousFrameLayout.background = ContextCompat.getDrawable(requireContext(), R.drawable.border_add_post_edit_text)
        }

        // 새로운 프레임 레이아웃에 선택 표시 추가
        val selectedFrameLayout = fragmentAddSwapPostBinding.root.findViewById<FrameLayout>(frameId)
        selectedFrameLayout.setBackgroundColor(Color.GRAY)

        // 선택된 프레임 레이아웃의 ID 저장
        selectedFrameId = frameId

        // 프레임 ID에 따라 BookState 설정
        selectedBookState = when (frameId) {
            R.id.FrameAddSwapPostVeryBad -> BookState.VERY_BAD
            R.id.FrameAddSwapPostBad -> BookState.BAD
            R.id.FrameAddSwapPostCommon -> BookState.COMMON
            R.id.FrameAddSwapPostGood -> BookState.GOOD
            R.id.FrameAddSwapPostVeryGood -> BookState.VERY_GOOD
            else -> null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_PICK_IMAGE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val selectedImageUris = data?.clipData
                    selectedImageUris?.let { clipData ->
                        for (i in 0 until minOf(clipData.itemCount, cardViewIds.size)) { // 최대 5개까지만 처리
                            val imageUri = clipData.getItemAt(i).uri
                            val imageBitmap = uriToBitmap(imageUri)
                            imageBitmap?.let { bitmap ->
                                val resizedBitmap = resizeBitmap(bitmap)
                                selectedImages.add(resizedBitmap)
                                val imageViewId = fragmentAddSwapPostBinding.root.findViewById<CardView>(
                                    cardViewIds[i])
                                    .getChildAt(0) // 각 카드뷰 안에 있는 ImageView를 가져옴
                                    .id
                                fragmentAddSwapPostBinding.root.findViewById<ImageView>(imageViewId).setImageBitmap(resizedBitmap)
                                // 다음 번호의 카드뷰를 보여줌
                                if (i < cardViewIds.size - 1) {
                                    val nextCardViewId = cardViewIds[i + 1]
                                    fragmentAddSwapPostBinding.root.findViewById<CardView>(
                                        nextCardViewId
                                    ).visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                }
            }
            REQUEST_IMAGE_CAPTURE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    val resizedBitmap = resizeBitmap(imageBitmap)
                    selectedImages.add(resizedBitmap)
                    selectedCardIndex?.let { index ->
                        val imageViewId = fragmentAddSwapPostBinding.root.findViewById<CardView>(cardViewIds[index])
                            .getChildAt(0) // 각 카드뷰 안에 있는 ImageView를 가져옴
                            .id
                        fragmentAddSwapPostBinding.root.findViewById<ImageView>(imageViewId).setImageBitmap(resizedBitmap)
                        // 다음 번호의 카드뷰를 보여줌
                        if (index < cardViewIds.size - 1) {
                            val nextCardViewId = cardViewIds[index + 1]
                            fragmentAddSwapPostBinding.root.findViewById<CardView>(nextCardViewId).visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    // 촬영 / 갤러리 선택 이미지 크기 조정
    private fun resizeBitmap(bitmap: Bitmap): Bitmap {
        val targetWidth = fragmentAddSwapPostBinding.imgAddSwapPost1.width
        val targetHeight = fragmentAddSwapPostBinding.imgAddSwapPost1.height

        val width = bitmap.width
        val height = bitmap.height

        val scaleX = targetWidth.toFloat() / width
        val scaleY = targetHeight.toFloat() / height
        val scaleFactor = scaleX.coerceAtLeast(scaleY)

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
        builder.setItems(items) { _, which ->
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
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),REQUEST_PICK_IMAGE
        )
    }

    private fun openCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.CAMERA), REQUEST_IMAGE_CAPTURE
            )
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, AddSwapPostFragment.REQUEST_IMAGE_CAPTURE)
        }
    }

    // 도서 종류 선택용 다이얼로그
    private fun getBookTypeMenu(view: View, buttonId: Int) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.popup_menu_add_post_book_type, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            val selectedBookType = when (menuItem.itemId) {
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
                when (buttonId) {
                    R.id.btnAddSwapPostType1 -> {
                        selectedBookType1 = it // 판매 도서 타입 저장
                        updateButtonText(showBookTypeText(it), buttonId)
                    }
                    R.id.btnAddSwapPostType2 -> {
                        selectedBookType2 = it // 교환 도서 타입 저장
                        updateButtonText(showBookTypeText(it), buttonId)
                    }
                }
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
            BookType.CHILDREN -> "어린이"
            BookType.TODDLER -> "유아"
            BookType.OTHER -> "기타"
        }
    }

    private fun updateButtonText(text: String, buttonId: Int) {
        when (buttonId) {
            R.id.btnAddSwapPostType1 -> {
                fragmentAddSwapPostBinding.btnAddSwapPostType1.text = text
            }

            R.id.btnAddSwapPostType2 -> {
                fragmentAddSwapPostBinding.btnAddSwapPostType2.text = text
            }
        }
    }

    private fun setupPriceEditText(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            private var current = ""

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != current) {
                    editText.removeTextChangedListener(this)

                    val cleanString = s.toString().replace("""[,.원]""".toRegex(), "")
                    if (cleanString.isNotEmpty()) {
                        val parsed = cleanString.toDouble()
                        val formatted = NumberFormat.getNumberInstance(Locale.KOREA).format(parsed) + "원"

                        current = formatted
                        editText.setText(formatted)
                        editText.setSelection(formatted.length - 1)
                    } else {
                        current = ""
                        editText.setText("")
                    }

                    editText.addTextChangedListener(this)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }
}