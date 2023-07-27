package com.app.inails.booking.admin.views.management.findstaff

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.combineNotNull
import android.support.core.livedata.post
import android.support.core.route.nullableArguments
import android.support.core.route.open
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
import com.app.inails.booking.admin.databinding.FragmentCreateRecruitmentBinding
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.model.response.AppImage
import com.app.inails.booking.admin.model.ui.CreateUpdateRecruitmentForm
import com.app.inails.booking.admin.model.ui.ISkill
import com.app.inails.booking.admin.model.ui.IState
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.popups.PopupUserMoreOwner
import com.app.inails.booking.admin.repository.management.RecruitmentRepo
import com.app.inails.booking.admin.views.management.findstaff.adapter.CustomerSkinColor
import com.app.inails.booking.admin.views.management.findstaff.adapter.CustomerSkinColorAdapter
import com.app.inails.booking.admin.views.management.findstaff.adapter.MainSkillSetAdapter
import com.app.inails.booking.admin.views.management.findstaff.adapter.MoreSkillSetAdapter
import com.app.inails.booking.admin.views.management.staff.CreateUpdateStaffOwner
import com.app.inails.booking.admin.views.me.ProfileRepository
import com.app.inails.booking.admin.views.me.adapters.UploadPhotoAdapter
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter

class CreateUpdatePostRecruitmentFragment : BaseFragment(R.layout.fragment_create_recruitment),
    TopBarOwner,
    CreateUpdateStaffOwner, PopupUserMoreOwner {
    private val binding by viewBinding(FragmentCreateRecruitmentBinding::bind)
    private val vm by viewModel<CreateRecruitmentVM>()
    val args by lazy { nullableArguments<Routing.UpdateRecruitment>() }

    //    var mWorkTimeJob: Int = -1
//    var isSelectOtherPos = false
//    var isClearJobPosition = false
//    var mJobPosition: Int = -1
    var mPayrollMethod: Int = -1
    var mGender: Int = -1
    var mJobShuttle: Int = -1
    var mIsRelocate: Int = -1

    private lateinit var imageAdapter: UploadPhotoAdapter
    var pathServerImage = ArrayList<AppImage>()
    var pathLocalImage = ArrayList<AppImage>()
    var allImage = ArrayList<AppImage>()
    private lateinit var mainSkillAdapter: MainSkillSetAdapter
    private lateinit var moreSkillAdapter: MoreSkillSetAdapter
    private lateinit var customerSkinColorAdapter: CustomerSkinColorAdapter

    private val form: CreateUpdateRecruitmentForm get() = vm.recruitmentForm
    private var stateFilterForm get() = vm.recruitmentForm.stateFilterForm
       set(value){
           vm.recruitmentForm.stateFilterForm = value
       }

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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                args?.let { R.string.mn_update_recruitment } ?: R.string.mn_create_recruitment,
                onBackClick = {
                    confirmDialog.show(
                        title = getString(R.string.tittle_exit_update_recruitment),
                        message = getString(R.string.message_exit),
                        function = {
                            onBackPress()
                        }
                    )
                },
            )
        )

        with(binding) {
//            binding.etPrice.setInputSignedDecimal(10, 2)
            appEvent.findingWorkingArea.bind {
                stateFilterForm = it
                etWorking.setText(it.format)
            }
            etWorking.onClick{
                open<FindWorkingAreaActivity>(vm.recruitmentForm.stateFilterForm)
            }
            etSalary.setInputSignedDecimal(6,2)
            etOwnerPhone.inputTypePhoneUS()
//            spCity.configSpinner(true, arrayOf(getString(R.string.city)))
//            spCity.isEnabled = false
            customerSkinColorAdapter = CustomerSkinColorAdapter(rvSkincolor).apply {
                onClickItem = {
                    form.customer_skin_color = it.id
                }
            }.apply {

            }
            if (args == null) {
                vm.getDetailSalon()
                customerSkinColorAdapter.submit(CustomerSkinColor.getList())
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

            imageAdapter = UploadPhotoAdapter(rvImages).apply {
                onItemClickListener = {
                    Router.open(self, Routing.PhotoViewer(it))
                }
                onAddImagesAction = {
                    FishBun.with(self)
                        .setImageAdapter(GlideAdapter())
                        .setMaxCount(20)
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
                        vm.recruitmentForm.deleteImage.add(it.id)
                        pathServerImage.remove(it)
                    }
                    allImage.remove(it)
                }
            }

            groupPayrollMethod.setOnCheckedChangeListener { _, checkedId ->
                mPayrollMethod = when (checkedId) {
                    rbBaoLuong.id -> 1
                    rb46.id -> 2
                    rbNegotiate.id -> 3
                    else -> 1
                }
            }

            groupShuttle.setOnCheckedChangeListener { group, checkedId ->
                mJobShuttle = when (checkedId) {
                    rbShuttleYes.id -> 1
                    rbShuttleNo.id -> 0
                    else -> 1
                }
            }

            groupRelocate.setOnCheckedChangeListener { group, checkedId ->
                mIsRelocate = when (checkedId) {
                    rbRelocateYes.id -> 1
                    rbRelocateNo.id -> 0
                    else -> 1
                }
            }

            groupGender.setOnCheckedChangeListener { _, checkedId ->
                mGender = when(checkedId){
                    rbGenderMale.id -> 1
                    rbGenderFemale.id -> 2
                    rbGenderAll.id -> 4
                    else -> 1
                }
            }
            rbShuttleNo.isChecked = true
            rbRelocateNo.isChecked = true
//            groupTimeJob.setOnCheckedChangeListener { _, checkID ->
//                mWorkTimeJob =
//                    when (checkID) {
//                        btnFullTime.id -> {
//                            etNumberOfWeek.hide()
//                            2
//                        }
//                        btnPartTime.id -> {
//                            etNumberOfWeek.show()
//                            1
//                        }
//                        else -> 1
//                    }
//            }

//            val listGender = requireContext().resources.getStringArray(R.array.arr_gender)
//            spGender.configSpinner(true, listGender) { position ->
//                vm.recruitmentForm.gender = position
//            }
//
//            spGender.setSelection(4, true)

            val listSalaryType = requireContext().resources.getStringArray(R.array.arr_salary_type)
            spSalaryType.configSpinner(false, listSalaryType) { pos ->
                vm.recruitmentForm.salary_type = pos + 1
            }
//
//            groupPosition.setOnCheckedChangeListener { radioGroup, i ->
//                if (isClearJobPosition) {
//                    isSelectOtherPos = false
//                    isClearJobPosition = false
//                    return@setOnCheckedChangeListener
//                }
//                etOtherPos.setText("")
//                mJobPosition = when (i) {
//                    btManager.id -> 1
//                    btMainWorker.id -> 2
//                    btAssistant.id -> 3
//                    else -> mJobPosition
//                }
//            }

//            etOtherPos.setOnFocusChangeListener { view, b ->
//                if (b) {
//                    isSelectOtherPos = true
//                    isClearJobPosition = true
//                    groupPosition.clearCheck()
//                } else if (!b && etOtherPos.text.isEmpty()) {
//                    isSelectOtherPos = false
//                    when (mJobPosition) {
//                        1 -> btManager.isChecked = true
//                        2 -> btMainWorker.isChecked = true
//                        3 -> btAssistant.isChecked = true
//                    }
//                }
//            }

            btSubmit.onClick {
                vm.recruitmentForm.apply {
//                    experience = etExperience.text.toDoubleOrNull() ?: 0.0
//                    working_form = mWorkTimeJob
//                    working_term = etNumberOfWeek.text
//                    position = mJobPosition
//                    position_other = etOtherPos.getTextString()
                    skillDefaultLength =
                        moreSkillAdapter.items.size + mainSkillAdapter.items.filter { it.selected }.size
                    ownerName = etOwnerName.getTextString()
                    ownerPhone = etOwnerPhone.getTextString().convertPhoneToNormalFormat()
                    salon_exists_time = etYearExist.text.toIntOrNull() ?: -1
                    salary = etSalary.getTextString()
                    title = edtTitleAds.getTextString()
                    description = edtContentAds.getTextString()
                    images = allImage.toListStringImage()
                    customer_skin_color = customerSkinColorAdapter.getIdSelected()
                    salary_form = mPayrollMethod
                    is_shuttle = mJobShuttle
                    is_there_house = mIsRelocate
                    gender = mGender
                    skillMain =
                        mainSkillAdapter.items.filter { it.selected && !it.isServer }.map { it.id }
                    skillCustom = moreSkillAdapter.items.filter { it.id == -1 }.map { it.name }
                }
                args?.let {
                    vm.recruitmentForm.id = it.id
                    vm.updateRecruitment()
                } ?: run {
                    vm.createRecruitment()
                }
            }
            vm.apply {
                args?.let {
                    getDetailRecruitment(it.id)
                }

//                updateState.bind {
//                    spState.setSelection(it)
//                }
//
//                updateCity.bind {
//                    spCity.setSelection(it)
//                }

                listSkillResult.bind {
                    if(args != null) return@bind
                    mainSkillAdapter.submit(it)
                }

//                listStateResult.bind {
//                    val listItem = it.map { it.fullName }.toMutableList()
//                    listItem.add(0, getString(R.string.state))
////                    spState.configSpinner(true, listItem.toTypedArray()) { pos ->
////                        if (pos != 0) {
////                            form.state = it[pos - 1].name
////                            vm.getListCity(form.state)
////                        }
////                    }
//                }

//                listCityResult.bind {
//                    spCity.isEnabled = true
//                    val listCity = it.map { it.name }.toMutableList()
//                    listCity.add(0, getString(R.string.city))
//                    spCity.configSpinner(true, listCity.toTypedArray()) { pos ->
//                        if (pos == 0) return@configSpinner
//                        form.city = listCity[pos]
//                    }
//                }

                updateMainSkill.bind {
                    etOwnerName.post {
                        mainSkillAdapter.submit(it.first)
                        mainSkillAdapter.items.forEach {
                            Log.d(
                                "TAG", "onViewCreated: NamTD8 ${
                                    it.selected
                                } -- ${it.name}"
                            )
                        }
                    }
                    moreSkillAdapter.submit(it.second)
                    if (it.second.isEmpty()) rvMoreSkillSet.hide()
                }

                salonDetail.bind {
                    etOwnerName.setText(it.salonName)
                    etOwnerPhone.setText(it.phoneNumber)
                }

                detailResult.bind {

                    stateFilterForm.apply {
                        stateSearch = it.salonState
                        citySearch = it.salonCity
                        etWorking.setText(formatDisplay())
                    }
                    edtTitleAds.setText(it.title)
                    etOwnerName.setText(it.salonName)
                    etOwnerPhone.setText(it.contactPhone)
                    etYearExist.text = it.salonExist.toString()
                    etYearExist.post {
                        customerSkinColorAdapter.submit(CustomerSkinColor.getList(it.customerSkinColor))
                    }
                    etSalary.setText(it.salary.display())
                    groupPayrollMethod.check(
                        when (it.payrollMethod) {
                            1 -> rbBaoLuong.id
                            2 -> rb46.id
                            else -> rbNegotiate.id
                        }
                    )
                    groupShuttle.check(
                        when (it.isShuttleId) {
                            0 -> rbShuttleYes.id
                            else -> rbShuttleNo.id
                        }
                    )
                    groupRelocate.check(
                        when (it.isThereHouseId) {
                            0 -> rbRelocateYes.id
                            else -> rbRelocateNo.id
                        }
                    )
                    groupGender.check(
                        when(it.gender){
                            1 -> rbGenderMale.id
                            2 -> rbGenderFemale.id
                            else -> rbGenderAll.id
                        }
                    )
                    spSalaryType.setSelection(it.salaryType - 1)
                    edtContentAds.setText(it.description)
                    pathServerImage.addAll(it.serverImages)
                    allImage.addAll(pathServerImage)
                    imageAdapter.changePath(allImage)
                }
                success.bind {
                    success(it)
                    Router.run {
                        redirectToMyRecruitment()
                    }
                }
            }
        }
    }
}

class CreateRecruitmentVM(
    private val recruitmentRepo: RecruitmentRepo,
    private val profileRepository: ProfileRepository
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {


    val recruitmentForm = CreateUpdateRecruitmentForm()

    var listState: List<IState> = listOf()
    val listStateResult = SingleLiveEvent<List<IState>>()
    val success = SingleLiveEvent<Int>()
    val detailResult = recruitmentRepo.detailRecruitmentResult
    val listSkillResult = recruitmentRepo.listSkill
//    val listCityResult = recruitmentRepo.listCity
    val salonDetail = profileRepository.result

    val updateMainSkill = combineNotNull(detailResult, listSkillResult) { detail, listMainSkill ->
        val listMoreSkill = mutableListOf<ISkill>()
        detail?.skillSet?.forEach { item ->
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


    init {
        getListState()
        getListSkillSet()
    }

    fun getDetailSalon() = launch(loading, error) {
        profileRepository()
    }


//    val updateState = combineNotNull(detailResult, listStateResult) { detail, listState ->
//        listState.map { it.name }.indexOf(detail?.salonState ?: "") + 1
//    }
////
//    val updateCity = combineNotNull(detailResult, listCityResult) { detaial, listCity ->
//        listCity.map { it.name }.indexOf(detaial?.salonCity ?: "") + 1
//    }


    fun createRecruitment() = launch(loading, error) {
        recruitmentRepo.createRecruitment(recruitmentForm)
        success.post(R.string.success_create_recruitment)
    }

    fun updateRecruitment() = launch(loading, error) {
        recruitmentRepo.updateRecruitment(recruitmentForm)
        success.post(R.string.success_update_recruitment)
    }

    fun getDetailRecruitment(id: Int) = launch(loading, error) {
        recruitmentRepo.getDetailRecruitment(id)
    }

    private fun getListState() = launch(loading, error) {
        listState = recruitmentRepo.getListState()
        listStateResult.post(listState)
    }

    fun getListCity(state: String) = launch(loading, error) {
        recruitmentRepo.getListCity(state)
    }

    fun getListSkillSet() = launch(loading, error) {
        recruitmentRepo.getListSkill()
    }
}
