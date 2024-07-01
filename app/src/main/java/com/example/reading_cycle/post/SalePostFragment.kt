package com.example.reading_cycle.post

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.databinding.DialogPostDetailsTextBinding
import com.example.reading_cycle.databinding.FragmentSalePostBinding
import com.example.reading_cycle.post.repository.SalePostRepository
import com.example.reading_cycle.post.vm.SalePostViewModel

class SalePostFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var fragmentSalePostBinding: FragmentSalePostBinding
    private lateinit var viewModelFactory: SalePostViewModel.Factory
    private lateinit var dialogPostDetailsTextBinding: DialogPostDetailsTextBinding
    private val salePostViewModel: SalePostViewModel by viewModels { viewModelFactory }
    private var documentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            documentId = it.getString("documentId")
            Log.d("SalePostFragment", "전달받은 문서 ID: $documentId")
        }

        val repository = SalePostRepository()
        viewModelFactory = SalePostViewModel.Factory(repository)

        documentId?.let {
            salePostViewModel.fetchSaleBookData(it)
            Log.d("SalePostFragment", "문서 ID에 대한 데이터 가져오기: $documentId")
        } ?: Log.e("SalePostFragment", "문서 ID가 null입니다.")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentSalePostBinding = FragmentSalePostBinding.inflate(inflater)
        mainActivity.hideBottomNavigation()

        // 뒤로 가기 버튼 클릭 리스너 설정
        fragmentSalePostBinding.toolbarLayoutSalePost.setNavigationOnClickListener {
            mainActivity.removeFragment(MainActivity.SALE_POST_FRAGMENT)
        }

        salePostViewModel.saleBookData.observe(viewLifecycleOwner) { saleBookData ->
            saleBookData?.let { data ->
                fragmentSalePostBinding.apply {
                    textSalePostTitle.text = data.saleBookTitle
                    textSalePostAuthor.text = data.saleBookAuthor
                    btnSalePostType.text = data.saleBookType.displayName
                    btnSalePostPrice.text = data.saleBookPrice
                    textSalePostRegPrice.text = data.saleBookRegPrice
                    textSalePostState.text = data.saleBookState.displayName
                    textSalePostExplain.text = data.saleBookExplain

                    // 대표 이미지 설정 (첫 번째 이미지)
                    Glide.with(this@SalePostFragment)
                        .load(data.saleBookImg.firstOrNull())
                        .fitCenter()
                        .into(imgSalePostPoster)

                    // 추가 이미지 설정 (나머지 이미지)
                    val imageViews = listOf(
                        imgSalePostBook1,
                        imgSalePostBook2,
                        imgSalePostBook3,
                        imgSalePostBook4
                    )
                    data.saleBookImg.drop(1).take(imageViews.size).forEachIndexed { index, imageUrl ->
                        imageViews[index].visibility = View.VISIBLE
                        Glide.with(this@SalePostFragment)
                            .load(imageUrl)
                            .centerCrop()
                            .into(imageViews[index])
                    }

                    // 클릭 리스너 설정
                    val onClickListener = View.OnClickListener {
                        showSalePostDetailsDialog(
                            data.saleBookTitle,   // title로 전달
                            data.saleBookAuthor,  // author로 전달
                            data.saleBookExplain  // description으로 전달
                        )
                    }

                    // 클릭 리스너를 텍스트 뷰에 설정
                    textSalePostTitle.setOnClickListener(onClickListener)
                    textSalePostAuthor.setOnClickListener(onClickListener)
                    textSalePostExplain.setOnClickListener(onClickListener)
                }
            }
        }

        return fragmentSalePostBinding.root
    }

    private fun showSalePostDetailsDialog(title: String, author: String, description: String) {
        dialogPostDetailsTextBinding = DialogPostDetailsTextBinding.inflate(layoutInflater)
        dialogPostDetailsTextBinding.dialogSalePostTitle.text = title
        dialogPostDetailsTextBinding.dialogSalePostAuthor.text = author
        dialogPostDetailsTextBinding.dialogSalePostDescription.text = description

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogPostDetailsTextBinding.root)
            .setPositiveButton("확인") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()

        dialog.show()
    }

}