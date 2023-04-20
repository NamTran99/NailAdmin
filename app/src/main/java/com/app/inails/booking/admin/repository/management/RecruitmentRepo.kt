package com.app.inails.booking.admin.repository.management

import android.location.Location
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.post
import android.support.di.Inject
import android.support.di.ShareScope
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.RecruitmentApi
import com.app.inails.booking.admin.extention.buildMultipart
import com.app.inails.booking.admin.extention.toImageMultiPartArray
import com.app.inails.booking.admin.factory.RecruitmentFactory
import com.app.inails.booking.admin.helper.RequestBodyBuilder
import com.app.inails.booking.admin.model.ui.*
import com.google.gson.Gson

@Inject(ShareScope.Fragment)
class RecruitmentRepo(
    private val recruitmentApi: RecruitmentApi,
    private val recruitmentFactory: RecruitmentFactory,
    private val userLocalSource: UserLocalSource
) {
    val fetchListResult = SingleLiveEvent<Pair<Int, List<IRecruitment>>>()
    val idRemove = SingleLiveEvent<Int>()
    val detailRecruitmentResult = SingleLiveEvent<IRecruitment>()
    val detailProfileResult = SingleLiveEvent<IJobProfile>()
    val updateResult = SingleLiveEvent<IRecruitment>()
    val listProfileResult = SingleLiveEvent<Pair<Int, List<IJobProfile>>>()

    val listCity = SingleLiveEvent<List<ICity>>()
    val listSkill = SingleLiveEvent<MutableList<ISkill>>()

    suspend fun createRecruitment(form: CreateUpdateRecruitmentForm) {
        form.validate()
        recruitmentApi.createRecruitment(
            RequestBodyBuilder()
                .put("title", form.title)
//                .put("salary", form.salary)
//                .putIf(form.position_other.isBlank(), "position", form.position)
                .put("description", form.description)
//                .put("working_form", form.working_form)
                .put("gender", form.gender)
                .put("experience_required", form.experience)
                .put("type_salary", form.salary_type)
                .put("is_there_house", form.is_there_house)
                .put("is_shuttle", form.is_shuttle)
                .put("salary_form", form.salary_form)
                .put("skills", form.skillMain)
                .put("salary", form.salary)
                .put("skill_custom", Gson().toJson(form.skillCustom))
                .put("salon_name", form.ownerName)
                .put("contact_phone", form.ownerPhone)
                .put("salon_city", form.city)
                .put("salon_state", form.state)
                .put("salon_exists_time", form.salon_exists_time)
                .put("customer_skin_color", form.customer_skin_color)
                .put("contact_name", form.ownerName)
//                .putIf(form.working_form == 1, "working_term", form.working_term)
//                .putIf(form.position_other.isNotBlank(), "position_other", form.position_other)
                .buildMultipart(),
            *form.images.toImageMultiPartArray()
        ).await()
    }

    suspend fun updateRecruitment(form: CreateUpdateRecruitmentForm) {
        form.validate()
        recruitmentApi.updateRecruitment(
            RequestBodyBuilder()
                .put("id", form.id)
                .put("title", form.title)
                .put("salary", form.salary)
                .put("gender", form.gender)
                .put("contact_name", form.ownerName)
//                .putIf(form.position_other.isEmpty(), "position", form.position)
                .put("description", form.description)
//                .put("working_form", form.working_form)
                .put("type_salary", form.salary_type)
//                .put("working_term", form.working_term)
                .put("is_there_house", form.is_there_house)
                .put("is_shuttle", form.is_shuttle)
                .put("salary_form", form.salary_form)
                .putIf(form.deleteImage.isNotEmpty(), "image_delete", form.deleteImage)
                .put("experience_required", form.experience)
                .put("skills", form.skillMain)
//                .put("income_weekly", form.income_weekly)
                .put("skill_custom", Gson().toJson(form.skillCustom))
                .put("salon_name", form.ownerName)
                .put("contact_phone", form.ownerPhone)
                .put("salon_city", form.city)
                .put("salon_state", form.state)
                .put("salon_exists_time", form.salon_exists_time)
                .put("customer_skin_color", form.customer_skin_color)
                .putIf(form.skillDelete.isNotEmpty(), "skill_delete", form.skillDelete)
//                .putIf(form.position_other.isNotBlank(), "position_other", form.position_other)
                .buildMultipart(),
            *form.images.toImageMultiPartArray()
        ).await()
    }

    suspend fun fetchListRecruitment(searchFilter: RecruitmentFilterForm, page: Int) {
        fetchListResult.post(
            page to
                    recruitmentFactory.createListRecruitment(
                        recruitmentApi.fetchRecruitment(
                            searchFilter.search,
                            page = page,
                            city = searchFilter.city,
                            state = searchFilter.state
                        ).await()
                    )
        )
    }

    suspend fun deleteRecruitment(id: Int) {
        recruitmentApi.deleteRecruitment(id).await()
        idRemove.post(id)
    }

    suspend fun publishRecruitment(status: Int, id: Int) {
        updateResult.post(
            recruitmentFactory.createARecruitment(
                recruitmentApi.publishRecruitment(status, id).await()
            )
        )
    }

    suspend fun getDetailRecruitment(id: Int) {
        detailRecruitmentResult.post(
            recruitmentFactory.createARecruitment(
                recruitmentApi.getDetailRecruitment(id).await()
            )
        )
    }

    suspend fun getListProfile(form: JobFilterForm, page: Int = 1, location: Location? = null) {
        listProfileResult.post(
            page to recruitmentFactory.createJobProfileList(
                recruitmentApi.getListProfile(
                    page = 1,
                    search = form.search,
                    state = form.state,
                    city = form.city,
                    gender = form.gender,
                    lat = location?.latitude,
                    lng = location?.longitude
                ).await()
            )
        )
    }

    suspend fun getDetailProfile(id: Int) {
        detailProfileResult.post(
            recruitmentFactory.createAJobProfile(
                recruitmentApi.getProfileDetail(id).await()
            )
        )
    }

    suspend fun getListState(): List<IState> {
        return if (userLocalSource.isExistListState()) {
            userLocalSource.getListStateLocal()!!
        } else {
            recruitmentFactory.createStateList(
                recruitmentApi.getListState().await()
            )
        }
    }

    suspend fun getListCity(state: String) {
        listCity.post(
            recruitmentFactory.createItemListCity(recruitmentApi.getListCity(state).await())
        )
    }

    suspend fun getListSkill() {
        listSkill.post(
            recruitmentFactory.createListItemSkill(recruitmentApi.getListSkill().await())
                .toMutableList()
        )
    }
}