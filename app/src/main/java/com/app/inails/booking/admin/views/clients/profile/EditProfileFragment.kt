package com.app.inails.booking.admin.views.clients.profile

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.post
import android.support.core.view.viewBinding
import android.support.di.Inject
import android.support.di.ShareScope
import android.support.navigation.findNavigator
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentProfileEditBinding
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.clients.UserApi
import com.app.inails.booking.admin.extention.inputTypePhoneUS
import com.app.inails.booking.admin.extention.lock
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.factory.client.ProfileClientFactory
import com.app.inails.booking.admin.formatter.TextFormatter
import com.app.inails.booking.admin.model.ui.client.IUserClientEdit
import com.app.inails.booking.admin.model.ui.client.UpdateProfileForm
import com.app.inails.booking.admin.views.dialog.picker.DatePickerDialog2
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarClientState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class EditProfileFragment : BaseFragment(R.layout.fragment_profile_edit), TopBarOwner {

    private val binding by viewBinding(FragmentProfileEditBinding::bind)
    private val viewModel by viewModel<EditProfileVM>()
    private val datePickerDialog by lazy { DatePickerDialog2(appActivity) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(SimpleTopBarClientState(R.string.title_edit_profile) { activity?.onBackPressed() })
        with(viewModel) {
            user.bind(::display)
            updateSuccess.bind {
                success(it)
                findNavigator().navigateUp()
            }
        }
        with(binding) {
            datePickerDialog.apply {
                setupClickWithView(edtPfDob)
                enableDateOfBirth()
            }
            edtPfPhone.inputTypePhoneUS()
            btnSave.onClick {
                with(viewModel) {
                    form.run {
                        name = edtPfName.text.toString()
                        phone = edtPfPhone.text.toString()
                        email = edtPfEmail.text.toString()
                        address = edtPfAddress.text.toString()
                        dobOrigin = edtPfDob.text.toString()
                        if (!dobOrigin.isNullOrEmpty())
                            dob = edtPfDob.tag.toString()
                    }
                    submit()
                }
            }
        }
    }

    private fun display(it: IUserClientEdit) = lock(binding) {
        edtPfName.setText(it.name)
        edtPfPhone.setText(it.phone)
        edtPfEmail.setText(it.emailEditable)
        edtPfAddress.setText(it.addressEditable)
        edtPfDob.setText(it.dobEditable)
    }
}

class EditProfileVM(private val editProfileRepo: EditProfileRepository) : ViewModel(),
    WindowStatusOwner by LiveDataStatusOwner() {

    val user = MutableLiveData<IUserClientEdit>()
    val form = UpdateProfileForm()
    val updateSuccess = SingleLiveEvent<Int>()

    init {
        launch {
            user.post(editProfileRepo.getUser())
        }
    }

    fun submit() = launch(loading, error) {
        editProfileRepo(form)
        updateSuccess.post(R.string.msg_profile_update_success)
    }
}

@Inject(ShareScope.Fragment)
class EditProfileRepository(
    private val userLocalSource: UserLocalSource,
    private val profileFactory: ProfileClientFactory,
    private val userApi: UserApi,
    private val textFormatter: TextFormatter
) {

    suspend operator fun invoke(
        form: UpdateProfileForm,
        hasValidate: Boolean = true
    ) {
        val dobOrigin = form.dobOrigin!!
        form.dobOrigin = null
        if (hasValidate)
            form.validate()
        form.phone = textFormatter.formatPhoneNumber(form.phone)
        userApi.updateProfile(form).await()
        val userOrigin = userLocalSource.getUserClientDto()
        val userData = userOrigin?.user?.copy(
            name = form.name,
            phone = form.phone,
            email = form.email,
            address = form.address,
            birthdate = dobOrigin
        )
        userOrigin?.copy(
            user = userData
        )?.let { userLocalSource.saveUserClient(it) }
    }

    fun getUser(): IUserClientEdit {
        return profileFactory.createEditProfile(userLocalSource.getUserClientDto()!!)
    }

}
