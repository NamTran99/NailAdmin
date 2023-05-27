package com.app.inails.booking.admin.repository.job

import android.location.Location
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.post
import android.support.di.Inject
import android.support.di.ShareScope
import com.app.inails.booking.admin.datasource.local.JobAndRecruitmentSource
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.JobApi
import com.app.inails.booking.admin.extention.buildMultipart
import com.app.inails.booking.admin.extention.toImageMultiPartArray
import com.app.inails.booking.admin.extention.toImagePart
import com.app.inails.booking.admin.factory.JobFactory
import com.app.inails.booking.admin.helper.RequestBodyBuilder
import com.app.inails.booking.admin.model.ui.*
import com.google.gson.Gson

@Inject(ShareScope.Fragment)
class JobRepository(
    private val jobApi: JobApi,
    private val jobFactory: JobFactory,
    val userLocalSource: UserLocalSource,
    val jobAndRecruitmentSource: JobAndRecruitmentSource
) {

    val detailResult = SingleLiveEvent<IJobProfile?>()
    val listCity = SingleLiveEvent<List<ICity>>()
    val listState = SingleLiveEvent<List<IState>>()
    val listSkill = SingleLiveEvent<MutableList<ISkill>>()

    suspend fun getDetailProfile(location: Location? = null) {
        detailResult.post(
            jobApi.getDetailJobUser().awaitNullable()?.let {
                jobAndRecruitmentSource.saveJobProfile(it)
                jobFactory.create(it)
            }
        )
    }

    suspend fun updateJobProfile(form: CreateUpdateJobForm) {
        form.validate()
        jobApi.updateJobProfile(
            RequestBodyBuilder()
                .put("name", form.name)
                .put("phone", form.phone)
                .put("email", form.email)
                .put("gender", form.gender)
                .put("city", form.city)
                .put("state", form.state)
                .put("lat", form.lat)
                .put("lng", form.lng)
                .put("salary", form.salary)
                .put("description", form.des)
                .put("zipcode", form.zipCode)
                .put("experience", form.experience)
                .put("type_salary", form.salary_type)
                .putIf(form.deleteImage.isNotEmpty(),"image_delete", form.deleteImage)
                .put("id", userLocalSource.getUserId())
                .put("workplace_type", form.working_type)
                .put("skills", form.skillMain)
                .put("skill_custom", Gson().toJson(form.skillCustom))
                .putIf(form.skillDelete.isNotEmpty(), "skill_delete",form.skillDelete)
                .buildMultipart(),
            avatar = form.avatar.toImagePart("avatar"),
            *form.images.toImageMultiPartArray()
        ).await()
    }

    suspend fun createJobProfile(form: CreateUpdateJobForm) {
        form.validate()
        jobApi.createJobProfile(
            RequestBodyBuilder()
                .put("name", form.name)
                .put("phone", form.phone)
                .put("email", form.email)
                .put("gender", form.gender)
                .put("city", form.city)
                .put("state", form.state)
                .put("lat", form.lat)
                .put("type_salary", form.salary_type)
                .put("lng", form.lng)
                .put("salary", form.salary)
                .put("description", form.des)
                .put("zipcode", form.zipCode)
                .put("experience", form.experience)
                .put("workplace_type", form.working_type)
                .put("skills", form.skillMain)
                .put("skill_custom", Gson().toJson(form.skillCustom))
                .buildMultipart(),
            avatar = form.avatar.toImagePart("avatar"),
            *form.images.toImageMultiPartArray()
        ).await()
    }

    suspend fun changeStatus(status: Int) {
        detailResult.post(
            jobApi.changeStatusJob(status).awaitNullable()?.let {
                jobFactory.create(it)
            }
        )
    }

    suspend fun getListState(): List<IState> {
        return if (jobAndRecruitmentSource.isExistListState()) {
            jobAndRecruitmentSource.getListStateLocal()!!
        } else {
            jobFactory.createItemListState(
                jobApi.getListState().await()
            )
        }
    }

    suspend fun getListCity(state: String) {
        listCity.post(
            jobFactory.createItemListCity(jobApi.getListCity(state).await())
        )
    }

    suspend fun getListSkill() {
        listSkill.post(
            jobFactory.createListItemSkill(jobApi.getListSkill().await()).toMutableList()
        )
    }
}