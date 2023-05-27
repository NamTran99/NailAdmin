package com.app.inails.booking.admin.helper.firebase

import android.net.Uri
import androidx.core.net.toUri
import com.app.inails.booking.admin.helper.firebase.DynamicFireBase.androidPackageClient
import com.app.inails.booking.admin.helper.firebase.DynamicFireBase.androidPackageOwner
import com.app.inails.booking.admin.helper.firebase.DynamicFireBase.firebasePrefixDomain
import com.app.inails.booking.admin.helper.firebase.DynamicFireBase.iosBundleIdClient
import com.app.inails.booking.admin.helper.firebase.DynamicFireBase.iosBundleIdOwner
import com.app.inails.booking.admin.helper.firebase.FirebaseType.salon
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ktx.androidParameters

object FirebaseType {
    const val salon = "share_salon"
    const val job = "share_job"
    const val staff = "share_staff"
}

object DynamicFireBase {
    val firebasePrefixDomain: String get() = "https://inails.page.link/"
    val androidPackageClient = "com.app.inails.booking"
    val androidPackageOwner = "com.app.booking.inails.admin"
    val iosBundleIdOwner = "com.booking.solution.owner"
    val iosBundleIdClient = "com.booking.solution"
}

fun generateSharingLink(
    type: String,
    id: String,
    domain: String = firebasePrefixDomain,
    imageLink: Uri? = null,
    getShareableLink: (String) -> Unit = {},
) {
    FirebaseDynamicLinks.getInstance().createDynamicLink()
        .run {
            // What is this link parameter? You will get to know when we will actually use this function.
            domainUriPrefix = firebasePrefixDomain
            val buildUri = domain.toUri().buildUpon()
                .appendQueryParameter(
                    "link",
                    if (type == salon) "https://play.google.com/store/apps/details?id=com.app.inails.booking"
                    else "https://play.google.com/store/apps/details?id=com.app.booking.inails.admin"
                )
                .appendQueryParameter("id", id)
                .appendQueryParameter("type", type)
                .build()

            link = buildUri
            // [domainUriPrefix] will be the domain name you added when setting up Dynamic Links at Firebase Console.
            // You can find it in the Dynamic Links dashboard.
            // Required

            val pkn = when (type) {
                salon -> androidPackageClient
                else -> androidPackageOwner
            }

            val bundleId = when (type) {
                salon -> iosBundleIdClient
                else -> iosBundleIdOwner
            }
            androidParameters(pkn) {
                build()
            }
            setIosParameters(DynamicLink.IosParameters.Builder(bundleId).build())
            val title = when (type) {
                salon -> "123VietNails - Application for booking nail appointments online"
                else -> "123VietNails - A platform that facilities job connections between nail technicians and nail salon proprietors"
            }

            val builder = DynamicLink.SocialMetaTagParameters.Builder().apply {
                setTitle(title)
                imageUrl = imageLink ?: "https://images2.imgbox.com/4a/38/Qd8qQwXB_o.png".toUri()
            }.build()

            setSocialMetaTagParameters(
                builder
            )
            // Finally
            buildShortDynamicLink()
        }.also {
            it.addOnSuccessListener { dynamicLink ->
                getShareableLink.invoke(dynamicLink.shortLink.toString())
            }.addOnFailureListener {
            }
        }
}