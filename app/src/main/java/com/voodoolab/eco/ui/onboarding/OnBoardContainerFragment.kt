package com.voodoolab.eco.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.rd.PageIndicatorView
import com.rd.animation.type.AnimationType
import com.voodoolab.eco.R
import com.voodoolab.eco.adapters.OnboardingRecyclerViewAdapter
import com.voodoolab.eco.interfaces.OnSnapPositionChangeListener
import com.voodoolab.eco.interfaces.SkipListener
import com.voodoolab.eco.models.OnboardModel
import com.voodoolab.eco.utils.SnapOnScrollListener

class OnBoardContainerFragment(var skipListener: SkipListener?) : Fragment(),
    OnSnapPositionChangeListener {

    var skipButton: Button? = null
    var nextButton: Button? = null
    var pageIndicator: PageIndicatorView? = null
    var onBoardingRecyclerView: RecyclerView? = null

    var adapter: OnboardingRecyclerViewAdapter? = null
    var mutableList: MutableLiveData<ArrayList<OnboardModel>> = MutableLiveData()

    var currentPosition: Int? = null

    var nextButtonListener = View.OnClickListener {

        println("DEBUG: inside listener $currentPosition")
        currentPosition?.let { position ->
            if (position < 2)
                onBoardingRecyclerView?.smoothScrollToPosition(position +1)

            if (position == 2) {
                skipListener?.skipGuide()
            }
        }
    }

    var skipButtonListener = View.OnClickListener {
        skipListener?.skipGuide()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.onboard_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews(view)
        initOnBoard()
        setList()
    }

    private fun initViews(view: View) {
        skipButton = view.findViewById(R.id.skip_button)
        skipButton?.setOnClickListener(skipButtonListener)
        nextButton = view.findViewById(R.id.next_button)
        nextButton?.setOnClickListener(nextButtonListener)
        pageIndicator = view.findViewById(R.id.pageIndicatorView)
        pageIndicator?.setAnimationType(AnimationType.WORM)
        onBoardingRecyclerView = view.findViewById(R.id.onBoardRecyclerView)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = RecyclerView.HORIZONTAL
        onBoardingRecyclerView?.layoutManager = layoutManager
        onBoardingRecyclerView?.hasFixedSize()
        onBoardingRecyclerView?.setItemViewCacheSize(3)

        // init recyclerview
        adapter = OnboardingRecyclerViewAdapter()
        onBoardingRecyclerView?.adapter = adapter
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(onBoardingRecyclerView)
        val snapOnScrollListener =
            SnapOnScrollListener(snapHelper, SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL, this)
        onBoardingRecyclerView?.addOnScrollListener(snapOnScrollListener)

        // init listeners
        // todo skip button -> убираем этот фрагмент и заменяем его на авторизацию
        // todo next button -> переходим на слеующий item, если item последний, тогда переходим на вторизацию
        // todo подключить pager indicator
    }

    private fun setList() {
        mutableList.observe(viewLifecycleOwner, Observer { list ->
            adapter?.setModels(list)
        })
    }

    private fun initOnBoard() {
        val list = ArrayList<OnboardModel>()
        list.add(OnboardModel(getString(R.string.on_board_text1), R.drawable.vector_onboard_1))
        list.add(OnboardModel(getString(R.string.on_board_text2), R.drawable.vector_onboard_2))
        list.add(OnboardModel(getString(R.string.on_board_text3), R.drawable.vector_onboard_3))
        mutableList.postValue(list)
    }

    override fun onSnapPositionChange(position: Int) {
        currentPosition = position
        println("DEBUG: $currentPosition")
        pageIndicator?.setSelected(position)
    }
}