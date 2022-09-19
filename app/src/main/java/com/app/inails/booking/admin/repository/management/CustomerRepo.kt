import android.support.core.livedata.post
import android.support.di.Inject
import android.support.di.ShareScope
import androidx.lifecycle.MutableLiveData
import com.app.inails.booking.admin.datasource.remote.CustomerApi
import com.app.inails.booking.admin.factory.BookingFactory
import com.app.inails.booking.admin.factory.CustomerFactory
import com.app.inails.booking.admin.model.ui.ICustomer

@Inject(ShareScope.Fragment)
class FetchListCustomerRepo(
    private val CustomerApi: CustomerApi,
    private val customerFactory: CustomerFactory,
) {
    val results = MutableLiveData<List<ICustomer>>()
    suspend operator fun invoke() {
        results.post(customerFactory
                .createCustomerList(
                    CustomerApi.getAllListCustomer().await()
                )
        )
    }
}