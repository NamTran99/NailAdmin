package com.app.inails.booking.admin.datasource.local

import android.content.Context
import android.support.core.livedata.post
import android.support.di.Inject
import android.support.di.ShareScope
import android.support.persistent.cache.GsonCaching
import androidx.lifecycle.MutableLiveData
import com.app.inails.booking.admin.helper.ShareIOScope
import com.app.inails.booking.admin.model.response.JobProfileDTO
import com.app.inails.booking.admin.model.response.StateDTO
import com.app.inails.booking.admin.model.ui.IState
import kotlinx.coroutines.launch

@Inject(ShareScope.Singleton)
class JobAndRecruitmentSource(
    private val shareIOScope: ShareIOScope,
    context: Context
) {

    private val caching = GsonCaching(context)
    private var listState: List<IState>? by caching.reference(StateDTO::class.java.name)
    var jobProfileUser: JobProfileDTO? by caching.reference(JobProfileDTO::class.java.name)

    fun saveJobProfile(item: JobProfileDTO?){
        jobProfileUser = item
    }

    fun isHaveJobProfile(): Boolean{
        return jobProfileUser != null
    }


    fun isExistListState(): Boolean{
        return !listState.isNullOrEmpty()
    }

    fun getListStateLocal(): List<IState>? {
        return listState
    }

}