package com.app.inails.booking.admin.views.me

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.post
import android.support.core.view.viewBinding
import android.support.di.Inject
import android.support.di.ShareScope
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.View
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentEmailReceiveFeedbackBinding
import com.app.inails.booking.admin.datasource.remote.MeApi
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class EmailReceiveFeedbackFragment : BaseFragment(R.layout.fragment_email_receive_feedback), TopBarOwner  {
    val viewModel by viewModel<EmailReceiveFeedbackViewModel>()
    val binding by viewBinding(FragmentEmailReceiveFeedbackBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                R.string.title_email_receive_feedback
            ) { activity?.onBackPressed() })

//        with(binding){
//            btSave.setOnClickListener {
//                viewModel.submit(etEnterEmail.text.toString())
//            }
//        }

        with(viewModel){
            success.bind {
                success("Success")
                activity?.onBackPressed()
            }
        }
    }

}


class EmailReceiveFeedbackViewModel(
    private val emailReceiveFeedbackRepository: EmailReceiveFeedbackRepository
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    val success = SingleLiveEvent<Any>()

    fun submit(email: String) = launch(loading, error) {
        success.post(emailReceiveFeedbackRepository(email))
    }
}

@Inject(ShareScope.Fragment)
class EmailReceiveFeedbackRepository(
    private val meApi: MeApi
){
    suspend operator fun invoke(email: String){
        meApi.settingEmailReceiveFeedback(email).await()
    }
}

