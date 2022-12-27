package com.app.inails.booking.admin.views.clients.feedbacks

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.LoadingEvent
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.LoadingLiveData
import android.support.core.livedata.post
import android.support.core.route.BundleArgument
import android.support.core.route.lazyArgument
import android.support.core.view.viewBinding
import android.support.di.Inject
import android.support.di.ShareScope
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseRefreshFragment
import com.app.inails.booking.admin.databinding.FragmentFeedbackBinding
import com.app.inails.booking.admin.datasource.remote.clients.FeedbackApi
import com.app.inails.booking.admin.extention.lock
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.factory.client.FeedbackFactory
import com.app.inails.booking.admin.model.ui.client.IFeedback
import kotlinx.parcelize.Parcelize

@Parcelize
class FeedbackArg(
    val idSalon: Long = 0
) : BundleArgument

class FeedbackFragment : BaseRefreshFragment(R.layout.fragment_feedback) {

    private val binding by viewBinding(FragmentFeedbackBinding::bind)
    private val viewModel by viewModel<FetchAllFeedbackVM>()
    private val arg by lazyArgument<FeedbackArg>()
    private lateinit var mAdapterFeedback: FeedbackAdapter

    override fun onRefreshListener() {
        refresh()
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            btnBack.onClick { activity?.onBackPressed() }
            mAdapterFeedback = FeedbackAdapter(rcvFeedback)
            mAdapterFeedback.onLoadMoreListener = { nexPage, _ ->
                with(viewModel) {
                    page = nexPage
                    fetch(arg.idSalon)
                }
            }
            mAdapterFeedback.onImageSelectedListener = {index,images->
                viewImagesDialog.shows(index,images)
            }
            with(viewModel) {
                loadingCustom.bind {
                    mAdapterFeedback.isLoading = it
                    viewRefresh.isRefreshing = it
                }
                feedbacks.bind(::displays)
            }
        }
    }

    private fun displays(it: Pair<IFeedback, Boolean>) = lock(binding) {
        val item = it.first
        ratingBar.rating = item.averageRating
        txtTotalRating.text = item.total
        if (!it.second)//isMore
            mAdapterFeedback.clear()
        mAdapterFeedback.submit(it.first.feedbacks)
    }

    private fun refresh() = with(viewModel) {
        mAdapterFeedback.clear()
        page = 1
        fetch(arg.idSalon)
    }
}

class FetchAllFeedbackVM(private val fetchAllFeedbackRepo: FetchAllFeedbackRepo) : ViewModel(),
    WindowStatusOwner by LiveDataStatusOwner() {
    var page = 1
    val loadingCustom: LoadingEvent = LoadingLiveData()
    val feedbacks = fetchAllFeedbackRepo.results

    fun fetch(salonID: Long) = launch(loadingCustom, error) {
        fetchAllFeedbackRepo(salonID, page)
    }

}

@Inject(ShareScope.Fragment)
class FetchAllFeedbackRepo(
    private val feedbackApi: FeedbackApi,
    private val feedbackFactory: FeedbackFactory
) {
    val results = MutableLiveData<Pair<IFeedback, Boolean>>()

    suspend operator fun invoke(salonID: Long, page: Int) {
        results.post(
            Pair(
                feedbackFactory.create(feedbackApi.feedback(salonID, page).await()),
                page > 1
            )
        )
    }

}
