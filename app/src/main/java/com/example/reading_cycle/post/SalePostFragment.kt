package com.example.reading_cycle.post

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.databinding.FragmentSalePostBinding
import com.example.reading_cycle.post.repository.SalePostRepository
import com.example.reading_cycle.post.vm.SalePostViewModel

class SalePostFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var fragmentSalePostBinding: FragmentSalePostBinding
    private lateinit var viewModelFactory: SalePostViewModel.Factory
    private val salePostViewModel: SalePostViewModel by viewModels { viewModelFactory }
    private var documentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("SalePostFragment", "onCreate called")

        arguments?.let {
            documentId = it.getString("documentId")
            Log.d("SalePostFragment", "Received documentId: $documentId")
        }

        val repository = SalePostRepository()
        viewModelFactory = SalePostViewModel.Factory(repository)

        documentId?.let {
            salePostViewModel.fetchSaleBookData(it)
            Log.d("SalePostFragment", "Fetching data for documentId: $documentId")
        } ?: Log.e("SalePostFragment", "documentId is null")
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
            Log.d("SalePostFragment", "Received data: ${saleBookData?.saleBookTitle}")
            saleBookData?.let {
                fragmentSalePostBinding.apply {
                    textSalePostTitle.text = it.saleBookTitle
                    textSalePostAuthor.text = it.saleBookAuthor
                    btnSalePostType.text = it.saleBookType.toString()
                    btnSalePostPrice.text = it.saleBookPrice
                    textSalePostRegPrice.text = it.saleBookRegPrice
                    textSalePostState.text = it.saleBookState.toString()
                    textSalePostExplain.text = it.saleBookExplain

                    // 대표 이미지 설정 (Glide 사용)
                    Glide.with(this@SalePostFragment)
                        .load(it.saleBookPostImg)
                        .into(imgSalePostPoster)

                    // 나머지 이미지 설정 (Glide 사용)
                    imgSalePostBook1.visibility = View.GONE
                    imgSalePostBook2.visibility = View.GONE
                    imgSalePostBook3.visibility = View.GONE
                    imgSalePostBook4.visibility = View.GONE

                    it.saleBookImg.take(4).forEachIndexed { index, imageUrl ->
                        when (index) {
                            0 -> {
                                imgSalePostBook1.visibility = View.VISIBLE
                                Glide.with(this@SalePostFragment)
                                    .load(imageUrl)
                                    .into(imgSalePostBook1)
                            }

                            1 -> {
                                imgSalePostBook2.visibility = View.VISIBLE
                                Glide.with(this@SalePostFragment)
                                    .load(imageUrl)
                                    .into(imgSalePostBook2)
                            }

                            2 -> {
                                imgSalePostBook3.visibility = View.VISIBLE
                                Glide.with(this@SalePostFragment)
                                    .load(imageUrl)
                                    .into(imgSalePostBook3)
                            }

                            3 -> {
                                imgSalePostBook4.visibility = View.VISIBLE
                                Glide.with(this@SalePostFragment)
                                    .load(imageUrl)
                                    .into(imgSalePostBook4)
                            }
                        }
                    }
                }
            }
        }
                return fragmentSalePostBinding.root
            }
        }
