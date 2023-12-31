import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.post
import android.support.di.Inject
import android.support.di.ShareScope
import androidx.lifecycle.MutableLiveData
import com.app.inails.booking.admin.app.AppConst
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.AppEvent
import com.app.inails.booking.admin.datasource.remote.CustomerApi
import com.app.inails.booking.admin.factory.CustomerFactory
import com.app.inails.booking.admin.model.form.UpdateCustomerForm
import com.app.inails.booking.admin.model.ui.ICustomer

@Inject(ShareScope.Fragment)
class FetchListCustomerRepo(
    private val CustomerApi: CustomerApi,
    private val customerFactory: CustomerFactory,
    private val userLocalSource: UserLocalSource
) {
    val results = MutableLiveData<List<ICustomer>>(listOf())
    val resultWithPage = SingleLiveEvent<Pair<Int, List<ICustomer>>>()
    val updateCustomerResult = SingleLiveEvent<ICustomer>()
    val addCustomerResult = SingleLiveEvent<ICustomer>()
    suspend operator fun invoke(search: String?) {
        results.post(
            customerFactory
                .createCustomerList(
                    CustomerApi.getAllListCustomer(search ?: "").await()
                )
        )
    }

    suspend fun addCustomer(phone: String, name: String) {
        addCustomerResult.post(
            customerFactory
                .createCustomer(
                    CustomerApi.addCustomer(phone = phone, name = name, salonID = userLocalSource.getSalonID())
                        .await()
                )
        )
    }

    suspend fun search(keyword: String, page: Int) {
        resultWithPage.post(
            page to
                    customerFactory
                        .createCustomerList(
                            CustomerApi.getAllListCustomer(
                                search = keyword,
                                page = page,
                                perPage = AppConst.perPage
                            ).await()
                        )
        )
    }

    suspend fun updateCustomer(form: UpdateCustomerForm) {
        form.validate()
        updateCustomerResult.post(
            customerFactory.createCustomer(
                CustomerApi.updateCustomer(form)
                    .await()
            )
        )
    }
}