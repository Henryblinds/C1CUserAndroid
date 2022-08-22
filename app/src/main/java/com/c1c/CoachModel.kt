package com.c1c

data class CoachModel(
    var coachId: String = "",
    var name: String = "",
    var type: String = "",
    var email: String = "",
    var number: String = "",
    var rate: String = ""
)

data class CoachProfileModel(
    var cprofileId: String = "",
    var name: String = ""
)

data class CoachPhoto(
    var coachPhotoId: String = "",
    var imagefile: String = ""
)

data class CoachLocModel(
    var cpLocId: String = "",
    var name: String = ""
)

data class CoachSocModel(
    var cpSocId: String = "",
    var name: String = ""
)

data class BookingModel(
    var bookId: String = "",
    var coachId: String = "",
    var name: String = "",
    var email: String = "",
    var number: String = "",
    var date: String = "",
    var notes: String = ""
)

data class RecordModel(
    val coach: CoachModel,
    val booked: BookingModel
)

data class RatingModel(
    var rateId: String = "",
    var coachId: String = "",
    var name: String = "",
    var rate: String = "",
    var review: String = "",
    var dateBook: String = ""
)


