package com.app.inails.booking.admin.views.me

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.combineNotNull
import android.support.core.livedata.post
import android.support.core.route.argument
import android.support.core.route.nullableArguments
import android.support.core.view.viewBinding
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentCreateJobProfileBinding
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.model.response.AppImage
import com.app.inails.booking.admin.model.ui.CreateUpdateJobForm
import com.app.inails.booking.admin.model.ui.ISkill
import com.app.inails.booking.admin.model.ui.IState
import com.app.inails.booking.admin.navigate.Router.Companion.navigate
import com.app.inails.booking.admin.navigate.Router.Companion.open
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.repository.job.JobRepository
import com.app.inails.booking.admin.views.clients.profile.ProfileDisplayRepository
import com.app.inails.booking.admin.views.management.findstaff.adapter.MainSkillSetAdapter
import com.app.inails.booking.admin.views.management.findstaff.adapter.MoreSkillSetAdapter
import com.app.inails.booking.admin.views.me.adapters.UploadPhotoAdapter
import com.app.inails.booking.admin.views.me.adapters.UploadPhotoAdapterJob
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter

class CreateEditJobProfileFragment : BaseFragment(R.layout.fragment_create_job_profile),
    TopBarOwner {

    private val binding by viewBinding(FragmentCreateJobProfileBinding::bind)
    private val viewModel by viewModel<CreateEditJobProfileVM>()
    private lateinit var imageAdapter: UploadPhotoAdapterJob
    private lateinit var mainSkillAdapter: MainSkillSetAdapter
    private lateinit var moreSkillAdapter: MoreSkillSetAdapter

    var pathServerImage = ArrayList<AppImage>()
    var pathLocalImage = ArrayList<AppImage>()
    var allImage = ArrayList<AppImage>()
    private var imageAvatar = ""
        set(value) {
            if (value.isNotEmpty()) {
                field = value
                binding.apply {
                    circleImageView.setImageUrl(imageAvatar)
                    imgCamera.hide()
                    tvCamera.hide()
                }
            }
        }
    val form get() = viewModel.form
    val args by lazy { argument<Routing.CreateEditJobProfile>() }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                val pathImage =
                    it.data?.getParcelableArrayListExtra(FishBun.INTENT_PATH) ?: arrayListOf<Uri>()
                pathLocalImage.clear()
                pathLocalImage.addAll(pathImage.filter { !it.toString().contains("http") }
                    .map { pathUri -> AppImage(image = pathUri.toString()) })
                allImage.clear()
                allImage.addAll(pathServerImage)
                allImage.addAll(pathLocalImage)
                imageAdapter.changePath(allImage)
            }
        }

    private val avatarResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                val pathImage =
                    it.data?.getParcelableArrayListExtra(FishBun.INTENT_PATH) ?: arrayListOf<Uri>()
                if (pathImage.isNotEmpty()) {
                    imageAvatar = pathImage[0].toString()
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(SimpleTopBarState(if(!args.isCreate) R.string.mn_update_cv else R.string.mn_create_cv, onBackClick = { activity?.onBackPressed() }))

        with(binding) {
            if(!args.isCreate){
                viewModel.getDetailJobProfile()
            }else{
                viewModel.getProfileDetail()
            }

            etPhone.inputTypePhoneUS()

            mainSkillAdapter = MainSkillSetAdapter(rvMainSkillSet.apply {
                layoutManager = FlexboxLayoutManager(requireContext()).apply {
                    flexWrap = FlexWrap.WRAP
                    flexDirection = FlexDirection.ROW
                }
            }).apply {
                onItemServerClick = {
                    if (!it.selected) form.skillDelete.add(it.id)
                    else form.skillDelete.remove(it.id)
                }
            }
            moreSkillAdapter = MoreSkillSetAdapter(rvMoreSkillSet.apply {
                layoutManager = FlexboxLayoutManager(requireContext()).apply {
                    flexWrap = FlexWrap.WRAP
                    flexDirection = FlexDirection.ROW
                }
            }).apply {
                onRemoveMoreSkillServer = {
                    form.skillDelete.add(it.id)
                }
            }
            rvMoreSkillSet.apply {
                layoutManager = FlexboxLayoutManager(requireContext()).apply {
                    flexWrap = FlexWrap.WRAP
                    flexDirection = FlexDirection.ROW
                }
            }
            groupGender.setOnCheckedChangeListener { _, checkedId ->
                form.gender = when (checkedId) {
                    rdMale.id -> 1
                    rdFemale.id -> 2
                    else -> 1
                }
            }

            imageAdapter = UploadPhotoAdapterJob(rvImages).apply {
                onItemClickListener = {
//                    redirectToShowZoomImage()
                    open(
                        self,
                        Routing.ShowZoomImageArgs(imageAdapter.listImage.toLocalImage1(), it - 1)
                    )
                }
                onAddImagesAction = {
                    FishBun.with(self)
                        .setImageAdapter(GlideAdapter())
                        .setMaxCount(5)
                        .setMinCount(1)
                        .setSelectedImages(ArrayList(allImage.map { it.image.toUri() }))
                        .setActionBarColor(
                            ContextCompat.getColor(requireContext(), R.color.colorPrimary),
                            ContextCompat.getColor(requireContext(), R.color.colorPrimary),
                            true
                        )
                        .setActionBarTitleColor(Color.parseColor("#ffffff"))
                        .startAlbumWithActivityResultCallback(startForResult)
                }
                onRemoveImageAction = {
                    if (it.id == null) {
                        pathLocalImage.remove(it)
                    } else {
                        form.deleteImage.add(it.id)
                        pathServerImage.remove(it)
                    }
                    allImage.remove(it)
                }
            }

            spCity.configSpinner(true, arrayOf("City"))
            spCity.isEnabled = false

            groupWorkingType.setOnCheckedChangeListener { group, checkedId ->
                form.working_type = when (checkedId) {
                    rdLocalPos.id -> 1
                    rdRelocate.id -> 2
                    else -> 1
                }
            }

            lvUploadPhoto.onClick {
                FishBun.with(self)
                    .setImageAdapter(GlideAdapter())
                    .setMaxCount(1)
                    .setMinCount(1)
                    .setActionBarColor(
                        ContextCompat.getColor(requireContext(), R.color.colorPrimary),
                        ContextCompat.getColor(requireContext(), R.color.colorPrimary),
                        true
                    )
                    .setActionBarTitleColor(Color.parseColor("#ffffff"))
                    .startAlbumWithActivityResultCallback(avatarResult)
            }
            btnAddSkill.onClick {
                if (etMoreSkill.getTextString().isBlank()) {
                    etMoreSkill.displayErrorAndFocus(R.string.error_blank_add_skill_set)
                    return@onClick
                }
                rvMoreSkillSet.show()
                val content = etMoreSkill.getTextString()
                moreSkillAdapter.addData(object : ISkill {
                    override var selected = false
                    override val name: String
                        get() = content
                    override val id: Int
                        get() = -1
                    override var isServer: Boolean = false
                })
                etMoreSkill.clearText()
            }

            btSubmit.onClick {
                viewModel.form.apply {
                    name = etName.getTextString()
                    avatar = imageAvatar
                    images = allImage.toListStringImage()
                    phone = etPhone.getTextString().convertPhoneToNormalFormat()
                    experience = etExperience.text
                    des = etDes.getTextString()
                    skillMain =
                        mainSkillAdapter.items.filter { it.selected && !it.isServer }.map { it.id }
                    skillCustom = moreSkillAdapter.items.filter { it.id == -1 }.map { it.name }
                    skillDefaultLength =
                        moreSkillAdapter.items.size + mainSkillAdapter.items.filter { it.selected }.size
                }

                if(args.isCreate) {
                    viewModel.createJobProfile()
                } else{
                    viewModel.updateJobProfile()
                }
            }

            with(viewModel) {
                detailResult.bind {
                    it?.let {
                        form.city = it.city
                        form.state = it.state
                        pathServerImage.addAll(it.images)
                        allImage.addAll(pathServerImage)
                        imageAdapter.changePath(allImage)
                        etName.setText(it.name)
                        if (it.workingPlaceType == 1) rdLocalPos.isChecked =
                            true else rdRelocate.isChecked = true
                        if (it.gender == 1) rdMale.isChecked = true else rdFemale.isChecked = true
                        etPhone.setText(it.phone)
                        etExperience.text = it.experience.toString()
                        etDes.setText(it.description)
                        imageAvatar = it.avatar
                    }
                }

                listStateResult.bind {
                    val listItem = it.map { it.fullName }.toMutableList()
                    listItem.add(0, getString(R.string.state))
                    spState.configSpinner(true, listItem.toTypedArray()) { pos ->
                        if (pos != 0) {
                            form.state = it[pos - 1].name
                            viewModel.getListCity(form.state)
                        }
                    }
                }

                listCityResult.bind {
                    spCity.isEnabled = true
                    val listCity = it.map { it.name }.toMutableList()
                    listCity.add(0, getString(R.string.city))
                    spCity.configSpinner(true, listCity.toTypedArray()) { pos ->
                        if (pos == 0) return@configSpinner
                        form.city = listCity[pos]
                    }
                }

                listSkillResult.bind {
                    if (!args.isCreate) return@bind
                    mainSkillAdapter.submit(it)
                }

                updateState.bind {
                    spState.setSelection(it)
                }

                updateCity.bind {
                    spCity.setSelection(it)
                }

                updateMainSkill.bind {
                    mainSkillAdapter.submit(it.first)
                    moreSkillAdapter.submit(it.second)
                    if (it.second.isEmpty()) rvMoreSkillSet.hide()
                }

                autofillDetail.bind {
                    etName.setText(it?.fullName)
                    etPhone.setText(it?.phone)
                }

                success.bind {
                    success(it)
                    activity?.onBackPressed()
                }
            }
        }
    }
}

class CreateEditJobProfileVM(
    private val jobRepository: JobRepository,
    private val profileDisplayRepository: ProfileDisplayRepository
) : ViewModel(),
    WindowStatusOwner by LiveDataStatusOwner() {

    init {
        getListState()
        getListSkillSet()
    }

    val form = CreateUpdateJobForm()
    val detailResult = jobRepository.detailResult
    val success = SingleLiveEvent<Int>()
    var listState: List<IState> = listOf()
    val listStateResult = SingleLiveEvent<List<IState>>()
    val listCityResult = jobRepository.listCity
    val listSkillResult = jobRepository.listSkill
    val autofillDetail = profileDisplayRepository.detailUser

    val updateState = combineNotNull(detailResult, listStateResult) { detail, listState ->
        listState.map { it.name }.indexOf(detail?.state ?: "") + 1
    }

    val updateCity = combineNotNull(detailResult, listCityResult) { detaial, listCity ->
        listCity.map { it.name }.indexOf(detaial?.city ?: "") + 1
    }

    val updateMainSkill = combineNotNull(detailResult, listSkillResult) { detail, listMainSkill ->
        val listMoreSkill = mutableListOf<ISkill>()
        detail?.skills?.forEach { item ->
            listMainSkill.findIndex { it.name == item.name }.apply {
                if (this != -1) {
                    listMainSkill[this] = item
                    listMainSkill[this].selected = true
                    listMainSkill[this].isServer = true
                } else
                    listMoreSkill.add(item)
            }
        }
        listMainSkill to listMoreSkill
    }


    fun getProfileDetail() = launch(loading, error) {
        profileDisplayRepository.getProfileDetail()
    }

    private fun getListState() = launch(loading, error) {
        listState = jobRepository.getListState()
        listStateResult.post(listState)
    }

    fun getListCity(state: String) = launch(loading, error) {
        jobRepository.getListCity(state)
    }

    fun getListSkillSet() = launch(loading, error) {
        jobRepository.getListSkill()
    }

    fun createJobProfile() = launch(loading, error) {
        jobRepository.createJobProfile(form)
        success.post(R.string.success_create_job_profile)
    }

    fun getDetailJobProfile() = launch(loading, error) {
        jobRepository.getDetailProfile()
    }

    fun updateJobProfile() = launch(loading, error) {
        jobRepository.updateJobProfile(form)
        success.post(R.string.success_update_job_profile)
    }
}