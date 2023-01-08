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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentEmailReceiveFeedbackBinding
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.MeApi
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.repository.auth.GetOwnerInformation
import com.app.inails.booking.admin.views.me.adapters.CreateUpdateEmailOwner
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class EmailReceiveFeedbackFragment : BaseFragment(R.layout.fragment_email_receive_feedback),
    TopBarOwner, CreateUpdateEmailOwner {
    val viewModel by viewModel<EmailReceiveFeedbackViewModel>()
    val binding by viewBinding(FragmentEmailReceiveFeedbackBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                R.string.title_email_receive_feedback,
                onBackClick = {
                    activity?.onBackPressed()
                },
            )
        )



        with(binding) {

            tvEmail.text = viewModel.email

            tvEditEmail.setOnClickListener {
                createUpdateEmailDialog.show {
                    viewModel.submit(it)
                }
            }

            btCreate.setOnClickListener {
                createUpdateEmailDialog.show {
                    viewModel.submit(it)
                }
            }
        }

        with(viewModel) {
            updatedEmail.bind {
                if (it.isBlank()) return@bind
                binding.tvEmail.text = it

                checkForVisibleView(it)
            }

            checkForVisibleView(email)

            success.bind {
                success(R.string.change_email_success)
            }
        }
    }


    private fun checkForVisibleView(email: String) {
        (email.isBlank()) show binding.layoutCreateEmail
        email.isNotBlank() show binding.layoutUpdateEmail
    }
}


class EmailReceiveFeedbackViewModel(
    private val createUpdateReceiveFeedbackRepository: CreateUpdateReceiveFeedbackRepository,
    private val getOwnerInformation: GetOwnerInformation
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {

    var email: String = ""
    val updatedEmail = createUpdateReceiveFeedbackRepository.result

    init {
        email = getCurrentEmail() ?: ""
    }

    val success = SingleLiveEvent<Any>()

    fun submit(email: String) = launch(loading, error) {
        success.post(createUpdateReceiveFeedbackRepository(email))
    }

    private fun getCurrentEmail() = getOwnerInformation.invoke()?.admin?.salon?.email
}

@Inject(ShareScope.Fragment)
class CreateUpdateReceiveFeedbackRepository(
    private val meApi: MeApi,
    private val userLocalSource: UserLocalSource,
) {
    val result = MutableLiveData("")
    suspend operator fun invoke(email: String) {
        meApi.settingEmailReceiveFeedback(email).await()
        userLocalSource.updateEmailFeedbackUser(email)
        result.post(email)
    }
}

