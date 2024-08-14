package com.example.reading_cycle.post

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.R
import com.example.reading_cycle.UserViewModel
import com.example.reading_cycle.databinding.DialogPostDetailsTextBinding
import com.example.reading_cycle.databinding.FragmentSalePostBinding
import com.example.reading_cycle.post.model.SaleBookData
import com.example.reading_cycle.post.repository.SalePostRepository
import com.example.reading_cycle.post.vm.SalePostViewModel

class SalePostFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var fragmentSalePostBinding: FragmentSalePostBinding
    private lateinit var viewModelFactory: SalePostViewModel.Factory
    private lateinit var dialogPostDetailsTextBinding: DialogPostDetailsTextBinding
    private val salePostViewModel: SalePostViewModel by viewModels { viewModelFactory }
    private val userViewModel: UserViewModel by activityViewModels()
    private var documentId: String? = null
    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: SalePostPagerAdapter
    private lateinit var closeButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            documentId = it.getString("documentId")
            Log.d("SalePostFragment", "전달받은 문서 ID: $documentId")
        }

        userViewModel.userIdx?.let { userIdx ->
            val repository = SalePostRepository(userIdx)
            viewModelFactory = SalePostViewModel.Factory(repository)
        }

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

        viewPager = fragmentSalePostBinding.viewPagerSalePostImages
        closeButton = fragmentSalePostBinding.root.findViewById(R.id.btnClose)
        adapter = SalePostPagerAdapter(emptyList())
        viewPager.adapter = adapter

        // 닫기 버튼 클릭 리스너 설정
        closeButton.setOnClickListener {
            hideViewPager()
        }

        salePostViewModel.saleBookData.observe(viewLifecycleOwner) { saleBookData ->
            saleBookData?.let { data ->
                bindSalePostData(data)
                // SaleBookData에서 userId를 추출하고 fetchUserData 호출
                data.userId?.let { userId ->
                    salePostViewModel.fetchUserData(userId)
                }
            }
        }

        salePostViewModel.userData.observe(viewLifecycleOwner) { userData ->
            userData?.let { data ->
                fragmentSalePostBinding.textSalePostUser.text = data.userNickname
                if (data.userProfileImage.isNotEmpty()) {
                    Glide.with(fragmentSalePostBinding.root.context)
                        .load(data.userProfileImage)
                        .apply(
                            RequestOptions()
                                .centerCrop()
                                .circleCrop()
                                .override(100, 100)
                        )
                        .into(fragmentSalePostBinding.imgSalePostUser)
                }
            }
        }

        return fragmentSalePostBinding.root
    }

    private fun bindSalePostData(data: SaleBookData) {
        fragmentSalePostBinding.apply {
            textSalePostTitle.text = data.saleBookTitle
            textSalePostAuthor.text = data.saleBookAuthor
            btnSalePostType.text = data.saleBookType.displayName
            btnSalePostPrice.text = data.saleBookPrice
            textSalePostRegPrice.text = data.saleBookRegPrice
            textSalePostState.text = data.saleBookState.name
            textSalePostExplain.text = data.saleBookExplain

            // 대표 이미지 설정 (첫 번째 이미지)
            loadImageView(imgSalePostPoster, data.saleBookImg.firstOrNull())

            imgSalePostPoster.setOnClickListener {
                showViewPager(0, data.saleBookImg)  // 첫 번째 이미지를 클릭했으므로 startPosition은 0
            }

            // 추가 이미지 설정 (나머지 이미지)
            val imageViews = listOf(
                imgSalePostBook1,
                imgSalePostBook2,
                imgSalePostBook3,
                imgSalePostBook4
            )
            data.saleBookImg.drop(1).take(imageViews.size).forEachIndexed { index, imageUrl ->
                imageViews[index].visibility = View.VISIBLE
                loadImageView(imageViews[index], imageUrl)

                imageViews[index].setOnClickListener {
                    showViewPager(index + 1, data.saleBookImg)  // 추가 이미지의 인덱스에 따라 조정
                }
            }

            // ViewPager2 클릭 리스너 설정
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    // 이미지를 클릭하면 전체 화면으로 보기
                    viewPager.setOnClickListener {
                        showViewPager(position, data.saleBookImg)
                    }
                }
            })

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

    private fun loadImageView(imageView: ImageView, imageUrl: String?) {
        Glide.with(this@SalePostFragment)
            .load(imageUrl)
            .centerCrop() // 이미지를 원본 비율을 유지하면서 이미지뷰를 꽉 채우도록 설정
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)
    }

    private fun showSalePostDetailsDialog(title: String, author: String, description: String) {
        dialogPostDetailsTextBinding = DialogPostDetailsTextBinding.inflate(layoutInflater)
        dialogPostDetailsTextBinding.dialogPostTitle.text = title
        dialogPostDetailsTextBinding.dialogPostAuthor.text = author
        dialogPostDetailsTextBinding.dialogPostDescription.text = description

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogPostDetailsTextBinding.root)
            .setPositiveButton("확인") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()

        dialog.show()
    }

    private fun showViewPager(startPosition: Int, imageUrls: List<String>) {
        // 어댑터에 이미지 리스트 업데이트
        adapter.updateImages(imageUrls)
        viewPager.setCurrentItem(startPosition, false)

        // ViewPager 가시성 설정
        viewPager.visibility = View.VISIBLE
        closeButton.visibility = View.VISIBLE
    }

    private fun hideViewPager() {
        viewPager.visibility = View.GONE
        closeButton.visibility = View.GONE

        fragmentSalePostBinding.imgSalePostPoster.visibility = View.VISIBLE  // 기존 이미지를 다시 표시
    }
}

class SalePostPagerAdapter(private var images: List<String>) : RecyclerView.Adapter<SalePostPagerAdapter.ImageViewHolder>() {

    fun updateImages(newImages: List<String>) {
        images = newImages
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_imageview, parent, false)
        return ImageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = images[position]
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .centerCrop() // 이미지를 원본 비율을 유지하면서 이미지뷰를 꽉 채우도록 설정
            .downsample(DownsampleStrategy.NONE) // 다운샘플링 없음
            .format(DecodeFormat.PREFER_ARGB_8888) // 고화질 포맷 사용
            .diskCacheStrategy(DiskCacheStrategy.ALL) // 캐시 전략 설정
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }
}