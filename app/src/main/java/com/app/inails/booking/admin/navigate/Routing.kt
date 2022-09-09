package com.app.inails.booking.admin.navigate
import android.support.core.route.BundleArgument
import android.support.navigation.NavOptions
import androidx.fragment.app.Fragment
import com.app.inails.booking.admin.R
import kotlinx.parcelize.Parcelize
import kotlin.reflect.KClass


interface IRoute : BundleArgument {
    val fragmentClass: KClass<out Fragment>
}

fun animOptions() = NavOptions(
    animEnter = R.anim.slide_in_from_right,
    animExit = R.anim.slide_out_to_left,
    animPopEnter = R.anim.slide_in_from_left,
    animPopExit = R.anim.slide_out_to_right
)