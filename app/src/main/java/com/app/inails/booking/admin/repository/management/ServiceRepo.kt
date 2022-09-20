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
class CreateServiceRepository(
    private val serviceApi: ServiceApi,
    private val bookingFactory: BookingFactory,
) {
    val results = MutableLiveData<IService>()
    suspend operator fun invoke(form: ServiceForm) {
        form.validate()
        results.post(
            bookingFactory
                .createAService(
                    serviceApi.createService(form).await()
                )
        )
    }
}

@Inject(ShareScope.Fragment)
class UpdateServiceRepository(
    private val serviceApi: ServiceApi,
    private val bookingFactory: BookingFactory,
) {
    val results = MutableLiveData<IService>()
    suspend operator fun invoke(form: ServiceForm) {
        form.validate()
        results.post(
            bookingFactory
                .createAService(
                    serviceApi.updateService(form).await()
                )
        )
    }
}

@Inject(ShareScope.Fragment)
class DeleteServiceRepository(
    private val serviceApi: ServiceApi,
) {
    val results = MutableLiveData<Int>()
    suspend operator fun invoke(serviceID: Int) {
        serviceApi.deleteService(serviceID).await()
        results.post(
            serviceID
        )
    }
}

@Inject(ShareScope.Fragment)
class ChangeActiveServiceRepository(
    private val serviceApi: ServiceApi,
    private val bookingFactory: BookingFactory,
) {
    val results = MutableLiveData<IService>()
    suspend operator fun invoke(serviceID: Int) {
        results.post(
            bookingFactory
                .createAService(
                    serviceApi.changeActiveService(serviceID).await()
                )
        )
    }
}

@Inject(ShareScope.Fragment)
class FetchListServiceRepo(
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