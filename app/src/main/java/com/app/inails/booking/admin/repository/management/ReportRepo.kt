import android.support.core.livedata.post
import android.support.di.Inject
import android.support.di.ShareScope
import androidx.lifecycle.MutableLiveData
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.ServiceApi
import com.app.inails.booking.admin.factory.BookingFactory
import com.app.inails.booking.admin.model.ui.IService
import com.app.inails.booking.admin.model.ui.ServiceForm

@Inject(ShareScope.Fragment)
class FetchListReportRepo(
    private val serviceApi: ServiceApi,
    private val bookingFactory: BookingFactory,
    private val userLocalSource: UserLocalSource
) {
    val results = MutableLiveData<List<IService>>()
    suspend operator fun invoke(search: String = "", page: Int) {
        results.post(
            bookingFactory
                .createServiceList(
                    serviceApi.getAllServiceList(
                        userLocalSource.getSalonID().toString(),
                        page,
                        search
                    )
                        .await()
                )
        )
    }
}