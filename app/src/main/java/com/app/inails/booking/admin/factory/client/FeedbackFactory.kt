package com.app.inails.booking.admin.factory.client

import android.support.di.Inject
import android.support.di.ShareScope
import com.app.inails.booking.admin.extention.safe
import com.app.inails.booking.admin.formatter.TextFormatter
import com.app.inails.booking.admin.model.response.client.FeedbackClientDTO
import com.app.inails.booking.admin.model.response.client.FeedbackItemDTO
import com.app.inails.booking.admin.model.ui.client.IFeedback
import com.app.inails.booking.admin.model.ui.client.IFeedbackItem

@Inject(ShareScope.Singleton)
class FeedbackFactory(private val textFormatter: TextFormatter) {

    private fun createItem(itemDTO: FeedbackItemDTO?): IFeedbackItem {
        return object : IFeedbackItem {
            override val name: String
                get() = itemDTO?.customer_name.safe()
            override val phone: String
                get() = itemDTO?.customer_phone.safe()
            override val rating: Float
                get() = itemDTO?.rating.safe()
            override val time: String
                get() = itemDTO?.diff_time.safe()
            override val contents: String
                get() = itemDTO?.content.safe()
            override val images: List<String>?
                get() = itemDTO?.images?.map { it.image }
        }
    }

    private fun createItemList(feedbacks: List<FeedbackItemDTO?>?): List<IFeedbackItem>? {
        return feedbacks?.map(::createItem)
    }

    fun create(itemDTO: FeedbackClientDTO): IFeedback {
        return object : IFeedback {
            override val total: String
                get() = textFormatter.formatTotalFeedback(itemDTO)
            override val averageRating: Float
                get() = itemDTO.average_rating.safe()
            override val feedbacks: List<IFeedbackItem>?
                get() = createItemList(itemDTO.feedbacks)
        }
    }
}
