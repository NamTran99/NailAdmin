package com.app.inails.booking.admin.repository.auth
import android.support.di.Inject
import android.support.di.ShareScope
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.AuthenticateApi
import com.app.inails.booking.admin.model.ui.LoginForm

@Inject(ShareScope.Activity)
class LoginRepo(
	private val userLocalSource: UserLocalSource,
	private val authenticateApi: AuthenticateApi
) {
		suspend operator fun invoke(form: LoginForm) {
				form.validate()
				val token = authenticateApi.login(form.username, form.password).await()
				userLocalSource.saveToken(token)
		}
}
