import android.content.Context
import android.support.core.livedata.post
import android.support.di.Inject
import android.support.di.ShareScope
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.ServiceApi
import com.app.inails.booking.admin.extention.buildMultipart
import com.app.inails.booking.admin.extention.getFilePath
import com.app.inails.booking.admin.extention.scalePhotoLibrary
import com.app.inails.booking.admin.extention.toImagePart
import com.app.inails.booking.admin.factory.BookingFactory
import com.app.inails.booking.admin.helper.RequestBodyBuilder
import com.app.inails.booking.admin.model.ui.IService
import com.app.inails.booking.admin.model.ui.ServiceForm
import okhttp3.MultipartBody

@Inject(ShareScope.Fragment)
class CreateServiceRepository(
    private val serviceApi: ServiceApi,
    private val bookingFactory: BookingFactory,
    val context: Context
) {
    val results = MutableLiveData<IService>()
    suspend operator fun invoke(form: ServiceForm) {
        form.validate()
        val imageParts =
            form.moreImage.filter { !it.path.contains("http") }.mapIndexed { index, uriLink ->
                context.getFilePath(uriLink.path.toUri())!!.scalePhotoLibrary(context)
                    .toImagePart("images")
            }.toTypedArray()

        var avatar:
                MultipartBody.Part? = null
        if (form.avatar!= null && !form.avatar!!.contains("http")){
            avatar =  context.getFilePath(form.avatar!!.toUri())!!.scalePhotoLibrary(context)
                .toImagePart("avatar")
        }
        results.post(
            bookingFactory
                .createAService(
                    serviceApi.createService(
                        RequestBodyBuilder()
                            .put("name", form.name)
                            .put ("price", form.price)
                            .buildMultipart(),
                        images = imageParts,
                        avatar = avatar
                        ).await()
                )
        )
    }
}

@Inject(ShareScope.Fragment)
class UpdateServiceRepository(
    private val serviceApi: ServiceApi,
    private val bookingFactory: BookingFactory,
    val context: Context
) {
    val results = MutableLiveData<IService>()

    suspend operator fun invoke(form: ServiceForm) {
        form.validate()
        val imageParts =
            form.moreImage.filter { !it.path.contains("http") }.mapIndexed { index, uriLink ->
                context.getFilePath(uriLink.path.toUri())!!.scalePhotoLibrary(context)
                    .toImagePart("images")
            }.toTypedArray()

        var avatar:
                MultipartBody.Part? = null
        if (form.avatar!= null && !form.avatar!!.contains("http")){
            avatar =  context.getFilePath(form.avatar!!.toUri())!!.scalePhotoLibrary(context)
                .toImagePart("avatar")
        }

        val currentServerImage = form.moreImage.filter { it.path.contains("http") }
        val deleteID = form.oldServerImages.filterNot {image ->
            currentServerImage.any { it.path == image.path }
        }.map { it.id }
        results.post(
            bookingFactory
                .createAService(
                    serviceApi.updateService(  RequestBodyBuilder()
                        .put("id", form.id)
                        .put("name", form.name)
                        .put("price", form.price)
                        .putIf(deleteID.isNotEmpty(), "delete_images", deleteID.toString())
                        .buildMultipart(),
                        images = imageParts,
                        avatar = avatar
                    ).await()
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