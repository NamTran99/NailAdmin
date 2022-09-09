package com.app.inails.booking.admin.factory
import android.support.di.Inject
import android.support.di.ShareScope
import com.app.inails.booking.admin.model.entity.AccountEntity
import com.app.inails.booking.admin.model.ui.LoginForm
@Inject(ShareScope.Activity)
class AccountFactory {

    fun createForm(account: AccountEntity?): LoginForm {
        if (account != null)
            return LoginForm(account.username, account.password)
        return LoginForm()
    }
}