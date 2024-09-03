package com.example.reading_cycle.post

import android.app.Activity
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
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.R
import com.example.reading_cycle.UserViewModel
import com.example.reading_cycle.databinding.FragmentAddSalePostBinding
import com.example.reading_cycle.post.model.BookState
import com.example.reading_cycle.post.model.BookType
import com.example.reading_cycle.post.model.SaleBookData
import com.example.reading_cycle.post.repository.AddSalePostRepository
import com.example.reading_cycle.post.vm.AddSalePostViewModel
import com.example.reading_cycle.post.vm.AddSalePostViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.NumberFormat
import java.util.Locale

class AddSalePostFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var fragmentAddSalePostBinding: FragmentAddSalePostBinding
    private lateinit var viewModel: AddSalePostViewModel
    private var selectedCardIndex: Int? = null
    private var selectedFrameId: Int? = null
    private var selectedBookType: BookType? = null
    private var selectedBookState: BookState? = null
    private val selectedImages = mutableListOf<Bitmap>()
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var barcodeScannerLauncher: ActivityResultLauncher<Intent>

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
        const val REQUEST_BARCODE_SCAN = 1003
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentAddSalePostBinding = FragmentAddSalePostBinding.inflate(inflater)
        mainActivity.hideBottomNavigation()

        val userIdx = userViewModel.userIdx
        Log.d("PostMainFragment", "User Index: $userIdx")

        // ViewModelFactory 초기화
        val factory = AddSalePostViewModelFactory(AddSalePostRepository())
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

        // 정가, 판매가 입력 형식 설정
        setupPriceEditText(fragmentAddSalePostBinding.edtAddSalePostPrice)
        setupPriceEditText(fragmentAddSalePostBinding.edtAddSalePostRegPrice)

        // 책 종류 선택 버튼 클릭 리스너 설정
        fragmentAddSalePostBinding.FrameAddSalePost1.setOnClickListener {
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

        fragmentAddSalePostBinding.btnAddSalePostComplete.setOnClickListener { handleCompleteButtonClick() }

        // 뷰모델에서 uploadResult 결과에 따른 동작 수행
        viewModel.uploadResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                showSnackbar("게시글이 성공적으로 등록되었습니다.")
                mainActivity.removeFragment(MainActivity.ADD_SALE_POST_FRAGMENT)
            } else {
                showSnackbar("게시글 등록에 실패했습니다.")
            }
        }

        // 메뉴 아이템 클릭 리스너 설정
        fragmentAddSalePostBinding.toolbarLayoutAddSalePost.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.postMenuItemNotify -> {
                    // 알림 메뉴 아이템 클릭 시 동작
                    true
                }
                R.id.postMenuItemBarcodeScan -> {
                    // 바코드 스캔 메뉴 아이템 클릭 시 동작
                    openBarcodeScanner()
                    true
                }
                else -> false
            }
        }

        barcodeScannerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.d("BarcodeScanner", "Result code: ${result.resultCode}")
            Log.d("BarcodeScanner", "Result data: ${result.data}")

            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                try {
                    // IntentIntegrator를 사용하여 스캔 결과를 파싱
                    val scanResult = IntentIntegrator.parseActivityResult(
                        Activity.RESULT_OK,
                        result.resultCode,
                        data
                    )

                    // scanResult 객체의 상태를 확인
                    if (scanResult != null) {
                        Log.d("BarcodeScanner", "ScanResult: $scanResult")
                        Log.d("BarcodeScanner", "ScanResult contents: ${scanResult.contents}")

                        // scanResult.contents가 null이 아닌지 확인
                        if (scanResult.contents != null) {
                            handleBarcodeResult(scanResult.contents)
                        } else {
                            showSnackbar("바코드 스캔 결과를 가져오는 데 실패했습니다.")
                            Log.d("BarcodeScanner", "Scan result contents is null.")
                        }
                    } else {
                        // Intent의 extras를 직접 확인
                        val extras = data?.extras
                        Log.d("BarcodeScanner", "Intent Extras: $extras")

                        val scanContent = extras?.getString("SCAN_RESULT")
                        if (scanContent != null) {
                            handleBarcodeResult(scanContent)
                        } else {
                            showSnackbar("바코드 스캔 결과를 가져오는 데 실패했습니다.")
                            Log.d("BarcodeScanner", "No scan result found in extras.")
                        }
                    }
                } catch (e: Exception) {
                    showSnackbar("바코드 스캔 결과를 처리하는 데 문제가 발생했습니다.")
                    Log.e("BarcodeScanner", "Error processing scan result: ${e.message}")
                }
            } else {
                showSnackbar("바코드 스캔이 취소되었습니다.")
                Log.d("BarcodeScanner", "Scan result not OK. Result code: ${result.resultCode}")
            }
        }
        return fragmentAddSalePostBinding.root
    }

    // 바코드 스캐너 열기
    private fun openBarcodeScanner() {
        try {
            Log.d("BarcodeScanner", "Opening barcode scanner")
            val integrator = IntentIntegrator(requireActivity())
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
            integrator.setPrompt("Scan a barcode")
            integrator.setCameraId(0)  // Use a specific camera of the device
            integrator.setBeepEnabled(true)
            integrator.setBarcodeImageEnabled(true)

            val intent = integrator.createScanIntent()
            barcodeScannerLauncher.launch(intent)
        } catch (e: Exception) {
            Log.e("BarcodeScanner", "Error opening barcode scanner: ${e.message}", e)
            showSnackbar("바코드 스캐너를 열 수 없습니다.")
        }
    }

    private fun handleCompleteButtonClick() {
        // 완료 버튼을 비활성화하여 중복 클릭을 방지
        fragmentAddSalePostBinding.btnAddSalePostComplete.isEnabled = false
        lifecycleScope.launch {
            try {
                val saleBookData = collectInputData()
                viewModel.uploadSalePost(saleBookData)
            } catch (e: IllegalStateException) {
                showSnackbar(e.message ?: "오류가 발생했습니다.")
                // 오류가 발생한 경우 버튼을 다시 활성화
                fragmentAddSalePostBinding.btnAddSalePostComplete.isEnabled = true
            }
        }
    }

    private suspend fun collectInputData(): SaleBookData {
        val title = fragmentAddSalePostBinding.edtAddSalePostTitle.text.toString()
        val author = fragmentAddSalePostBinding.edtAddSalePostAuthor.text.toString()
        val bookType = selectedBookType?: throw IllegalStateException("판매 도서 종류를 선택해주세요")
        val price = fragmentAddSalePostBinding.edtAddSalePostPrice.text.toString()
        val regPrice = fragmentAddSalePostBinding.edtAddSalePostRegPrice.text.toString()
        val bookState = selectedBookState ?: throw IllegalStateException("도서 상태를 선택해주세요")
        val description = fragmentAddSalePostBinding.edtAddSalePostExplain.text.toString()

        if (title.isBlank()) throw IllegalStateException("제목을 입력하세요.")
        if (author.isBlank()) throw IllegalStateException("작가를 입력하세요.")
        if (price.isBlank()) throw IllegalStateException("판매 가격을 입력하세요.")
        if (regPrice.isBlank()) throw IllegalStateException("정가를 입력하세요.")
        if (description.isBlank()) throw IllegalStateException("설명을 입력하세요.")
        if (selectedImages.isEmpty()) throw IllegalStateException("최소 한 장의 이미지를 등록하세요.")

        val imageUrls = withContext(Dispatchers.IO) {
            uploadImagesAndGetUrls(selectedImages)
        }

        val userId = userViewModel.userIdx ?: throw IllegalStateException("유저 ID를 가져올 수 없습니다.")

        return SaleBookData(
            userId = userId,
            saleBookPostImg = imageUrls.firstOrNull() ?: "",
            saleBookImg = imageUrls,
            saleBookTitle = title,
            saleBookAuthor = author,
            saleBookType = bookType,
            saleBookPrice = price,
            saleBookRegPrice = regPrice,
            saleBookState = bookState,
            saleBookExplain = description
        )
    }

    // 이미지 업로드 후 URL을 반환하는 함수
    private suspend fun uploadImagesAndGetUrls(images: List<Bitmap>): List<String> = withContext(Dispatchers.IO) {
        val urls = mutableListOf<String>()
        val storage = FirebaseStorage.getInstance().reference

        images.forEachIndexed { index, bitmap ->
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
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
        Log.d("BarcodeScanner", "onActivityResult called with requestCode: $requestCode, resultCode: $resultCode")
        when (requestCode) {
            REQUEST_PICK_IMAGE -> {
                if (resultCode == RESULT_OK) {
                    val imageUri = data?.data
                    imageUri?.let { uri ->
                        val imageBitmap = uriToBitmap(uri)
                        imageBitmap?.let { bitmap ->
                            val resizedBitmap = resizeBitmap(bitmap)
                            selectedCardIndex?.let { index ->
                                // 이미지를 대체하거나 추가하는 경우
                                if (index < selectedImages.size) {
                                    // 이미지를 대체하는 경우, 기존 이미지를 삭제하고 새 이미지를 추가함
                                    selectedImages.removeAt(index)
                                    selectedImages.add(index, resizedBitmap)
                                } else {
                                    // 선택한 인덱스가 리스트의 범위를 넘어가는 경우 새로운 이미지를 추가함
                                    selectedImages.add(resizedBitmap)
                                }
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
                        } ?: run {
                            showSnackbar("이미지를 가져오는 데 문제가 발생했습니다.")
                        }
                    } ?: run {
                        showSnackbar("이미지를 가져오는 데 문제가 발생했습니다.")
                    }
                }
            }
            REQUEST_IMAGE_CAPTURE -> {
                if (resultCode == RESULT_OK) {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    val resizedBitmap = resizeBitmap(imageBitmap)
                    selectedCardIndex?.let { index ->
                        // 이미지를 대체하거나 추가하는 경우
                        if (index < selectedImages.size) {
                            // 이미지를 대체하는 경우, 기존 이미지를 삭제하고 새 이미지를 추가함
                            selectedImages.removeAt(index)
                            selectedImages.add(index, resizedBitmap)
                        } else {
                            // 선택한 인덱스가 리스트의 범위를 넘어가는 경우 새로운 이미지를 추가함
                            selectedImages.add(resizedBitmap)
                        }
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

    private fun handleBarcodeResult(barcode: String) {
        Log.d("BarcodeScan", "handleBarcodeResult called with barcode: $barcode")

        // API 호출을 코루틴 내에서 실행
        CoroutineScope(Dispatchers.Main).launch {
            val bookInfo = fetchBookInfoFromBarcode(barcode)

            if (bookInfo != null) {
                Log.d("BarcodeScan", "Book info fetched successfully: $bookInfo")
                // UI 업데이트 예: Snackbar 표시 및 입력 필드 업데이트
                updateInputFields(bookInfo)
            } else {
                Log.d("BarcodeScan", "Failed to fetch book info")
                showSnackbar("바코드 스캔 결과를 가져오는 데 실패했습니다.")
            }
        }
    }

    private fun updateInputFields(bookInfo: BookInfo) {
        // UI를 업데이트하기 위해 메인 스레드에서 실행합니다.

        // 로그: BookInfo 객체의 정보
        Log.d("UpdateInputFields", "Updating input fields with BookInfo: $bookInfo")

        // 제목 설정
        fragmentAddSalePostBinding.edtAddSalePostTitle.setText(bookInfo.title)
        Log.d("UpdateInputFields", "Title set to: ${bookInfo.title}")

        // 저자 설정
        val authorsText = bookInfo.authors?.joinToString(", ")
        fragmentAddSalePostBinding.edtAddSalePostAuthor.setText(authorsText)
        Log.d("UpdateInputFields", "Authors set to: $authorsText")

        // 설명 설정
        fragmentAddSalePostBinding.edtAddSalePostExplain.setText(bookInfo.description)
        Log.d("UpdateInputFields", "Description set to: ${bookInfo.description}")

        // coverUrl이 URL 문자열이라면 비트맵으로 변환 후 설정
        bookInfo.coverUrl?.let { coverUrl ->
            Log.d("UpdateInputFields", "Cover URL: $coverUrl")

            lifecycleScope.launch {
                val bitmap = fetchBitmapFromUrl(coverUrl)
                if (bitmap != null) {
                    fragmentAddSalePostBinding.imgAddSalePost1.setImageBitmap(bitmap)
                    Log.d("UpdateInputFields", "Cover image set successfully")
                } else {
                    showSnackbar("책 표지 이미지를 로드하는 데 실패했습니다.")
                    Log.e("UpdateInputFields", "Failed to load cover image from URL: $coverUrl")
                }
            }
        } ?: run {
            Log.d("UpdateInputFields", "No cover URL provided")
        }
    }

    private suspend fun fetchBitmapFromUrl(urlString: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            Log.e("FetchBitmap", "Error fetching bitmap from URL: ${e.message}")
            null
        }
    }

    private suspend fun fetchBookInfoFromBarcode(barcode: String): BookInfo? {
        return withContext(Dispatchers.IO) {
            try {
                val url = "https://openlibrary.org/api/books?bibkeys=ISBN:$barcode&format=json&jscmd=data"
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()

                val responseCode = connection.responseCode
                Log.d("APIResponse", "Response Code: $responseCode")

                val response = connection.inputStream.bufferedReader().use { it.readText() }
                Log.d("APIResponse", "Response Body: $response")

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // JSON 파싱 및 BookInfo 객체 생성
                    val bookInfo = parseBookInfo(response)
                    bookInfo
                } else {
                    Log.e("APIResponse", "Failed with response code: $responseCode")
                    null
                }
            } catch (e: Exception) {
                Log.e("FetchBookInfo", "Error fetching book info: ${e.message}")
                null
            }
        }
    }

    private fun parseBookInfo(jsonString: String): BookInfo? {
        return try {
            val jsonObject = JSONObject(jsonString)
            val bookData = jsonObject.keys().asSequence().firstOrNull()?.let { jsonObject.getJSONObject(it) }

            val title = bookData?.optString("title")
            val authors = bookData?.optJSONArray("authors")?.let { jsonArray ->
                List(jsonArray.length()) { i ->
                    jsonArray.getJSONObject(i).optString("name")
                }
            }
            val coverObject = bookData?.optJSONObject("cover")
            val coverUrl = coverObject?.optString("large")
            val description = when {
                bookData?.has("description") == true -> {
                    bookData.optString("description") // 단순 문자열
                        ?: bookData.optJSONObject("description")?.optString("value") // 객체의 value 필드
                }
                else -> null
            }
            Log.d("ParseBookInfo", "Description: $description")

            BookInfo(title, authors, coverUrl, description)
        } catch (e: Exception) {
            Log.e("JSONParse", "Error parsing JSON: ${e.message}")
            null
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_BARCODE_SCAN -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("BarcodeScan", "Camera permission granted")
                    openBarcodeScanner() // 권한이 허용되면 스캐너 열기
                } else {
                    Log.w("BarcodeScan", "Camera permission denied")
                    Toast.makeText(requireContext(), "Camera permission is required for scanning", Toast.LENGTH_SHORT).show()
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
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
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

    private fun updateButtonText(text: String) {
        fragmentAddSalePostBinding.btnAddSalePostType1.text = text
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

    // Retrofit 인터페이스
    interface BookApiService {
        @GET("api/books")
        suspend fun getBookInfo(
            @Query("bibkeys") bibkeys: String,
            @Query("format") format: String = "json",
            @Query("jscmd") jscmd: String = "data"
        ): Map<String, BookInfoResponse>
    }

    data class BookInfoResponse(
        val title: String,
        val authors: List<Author>,
        val number_of_pages: Int?,
        val cover: Cover?
    ) {
        data class Author(val name: String)
        data class Cover(val large: String?)
    }

    // 도서 정보 데이터 클래스
    data class BookInfo(
        val title: String?,
        val authors: List<String>?,
        val coverUrl: String?,
        val description: String?
    )
}