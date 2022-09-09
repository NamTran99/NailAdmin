package com.app.inails.booking.admin.repository.auth
import android.support.di.Inject
import android.support.di.ShareScope
import com.app.inails.booking.admin.datasource.local.UserLocalSource

@Inject(ShareScope.Fragment)
class CheckLoginRepo(private val userLocalSource: UserLocalSource) {
		operator fun invoke(): Boolean {
				return userLocalSource.getToken()!!.isNotBlank()
		}
}