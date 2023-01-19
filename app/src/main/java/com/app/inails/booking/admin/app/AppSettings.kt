package com.app.inails.booking.admin.app

import android.app.Activity
import android.app.Dialog
import android.content.ComponentCallbacks
import android.content.Context
import android.content.Intent
import android.support.core.view.ViewScopeOwner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.helper.DriverUtils
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.util.*


private const val SELECT_MULTIPLE_IMAGE = 2
private const val CAMERA_REQUEST = 3
class AppSettings(private val context: Context) {

    val activityContext = context as AppCompatActivity

    companion object{
        const val AUTOCOMPLETE_REQUEST_CODE = 1
    }

    fun navigateMyLocationWithGoogleMap(latitude: Float, longitude: Float) {
        DriverUtils.navigateMyLocationWithGoogleMap(context, latitude, longitude)
    }

    fun callPhone(phoneNumber: String) {
        DriverUtils.call(context,phoneNumber)
    }

    fun openPlaceAutoComplete(placeOriginal: String, function: (Place) -> Unit) {
        val contextActivity = context as BaseActivity
        var statusCode = -1
        try {
            val fields = Arrays.asList<Place.Field>(
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG,
            )
            val builder = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .setInitialQuery(placeOriginal)
            contextActivity.startActivityForResult(
                builder.build(context), AUTOCOMPLETE_REQUEST_CODE
            )
        } catch (exception: GooglePlayServicesRepairableException) {
            statusCode = exception.connectionStatusCode
        } catch (exception: GooglePlayServicesNotAvailableException) {
            statusCode = exception.errorCode
        }
        if (statusCode != -1) {
            GoogleApiAvailability.getInstance().showErrorDialogFragment(context, statusCode, 30422)
        }
        contextActivity.resultLifecycle.onActivityResult { requestCode, resultCode, intent ->
            if (requestCode != AUTOCOMPLETE_REQUEST_CODE || intent == null) return@onActivityResult
            if (resultCode == AutocompleteActivity.RESULT_OK) {
                function(Autocomplete.getPlaceFromIntent(intent))
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Toast.makeText(
                    context, Autocomplete.getStatusFromIntent(intent).statusMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}

interface AppSettingsOwner : ViewScopeOwner {
    val appSettings
        get() = viewScope.getOr("app_settings") {
            when (this) {
                is Fragment -> AppSettings(this.requireContext())
                is FragmentActivity -> AppSettings(this)
                is BaseDialog -> AppSettings(this.activityContext)
                else -> error("Not support get permission for ${this.javaClass.name} ")
            }
        }
}