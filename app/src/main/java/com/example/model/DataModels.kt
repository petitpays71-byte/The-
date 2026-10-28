package com.example.model

enum class AppLanguage {
    FR, EN
}

enum class UserRole {
    LEARNER, CREATOR, ENTREPRENEUR, ADMIN
}

enum class PaymentProvider {
    ORANGE_MONEY, MTN_MOMO
}

enum class TransactionType {
    REGISTRATION_FEE,
    DEPOSIT_ORANGE_MONEY,
    DEPOSIT_MTN_MOMO,
    REFERRAL_BONUS,
    COURSE_PURCHASE,
    WITHDRAWAL_REQUEST
}

enum class Status {
    PENDING,
    APPROVED,
    COMPLETED,
    REJECTED
}

data class UserProfile(
    val id: String = "usr_001",
    val fullName: String = "Junior Roberto",
    val email: String = "junior.roberto@nueva.africa",
    val phone: String = "+237 690 12 34 56",
    val role: UserRole = UserRole.ENTREPRENEUR,
    val isMembershipActive: Boolean = false,
    val registrationPaymentRef: String? = null,
    val referralCode: String = "NUEVA-AFR-779",
    val referredBy: String? = "ALAIN-DEV-237",
    val joinedDate: String = "2026-10-15",
    val bio: String = "Fondateur startup AgTech & passionné de solutions mobiles pour l'Afrique.",
    val isSuspended: Boolean = false
)

data class PlatformSettings(
    val registrationFee: Long = 1000L,       // 1,000 FCFA
    val conferencePrice: Long = 500L,        // 500 FCFA
    val referralReward: Long = 100L,         // 100 FCFA
    val minWithdrawal: Long = 1100L,         // 1,100 FCFA
    val testModeEnabled: Boolean = true,
    val orangeMoneyMerchant: String = "699 00 11 22 (NUEVA TECH)",
    val mtnMoMoMerchant: String = "677 33 44 55 (NUEVA TECH)"
)

data class Lesson(
    val id: String,
    val titleFr: String,
    val titleEn: String,
    val durationMinutes: Int,
    val keyTakeawayFr: String,
    val keyTakeawayEn: String,
    val isCompleted: Boolean = false
)

data class Course(
    val id: String,
    val titleFr: String,
    val titleEn: String,
    val category: String,
    val instructor: String,
    val price: Long, // 0 for free, or conferencePrice (500 FCFA)
    val isConference: Boolean = false,
    val conferenceDate: String? = null,
    val totalLessons: Int,
    val completedLessons: Int = 0,
    val rating: Float = 4.8f,
    val descriptionFr: String,
    val descriptionEn: String,
    val lessons: List<Lesson> = emptyList(),
    val isEnrolled: Boolean = false
)

data class MarketplaceProduct(
    val id: String,
    val title: String,
    val description: String,
    val price: Long,
    val category: String,
    val sellerName: String,
    val sellerPhone: String,
    val isApproved: Boolean = true,
    val rating: Float = 4.9f,
    val reviewsCount: Int = 12,
    val isUserProduct: Boolean = false
)

data class TransactionRecord(
    val id: String,
    val type: TransactionType,
    val amount: Long,
    val status: Status,
    val date: String,
    val reference: String,
    val note: String
)

data class ReferralItem(
    val id: String,
    val refereeName: String,
    val refereePhone: String,
    val date: String,
    val isVerified: Boolean,
    val bonusPaid: Boolean
)

data class WithdrawalRequest(
    val id: String,
    val userId: String,
    val userName: String,
    val userPhone: String,
    val provider: PaymentProvider,
    val amount: Long,
    val status: Status = Status.PENDING,
    val date: String,
    val adminNote: String = ""
)

data class PaymentVerificationRequest(
    val id: String,
    val userId: String,
    val userName: String,
    val userPhone: String,
    val purpose: String, // "MEMBERSHIP" or "CONFERENCE"
    val provider: PaymentProvider,
    val transactionRef: String,
    val amount: Long,
    val userNote: String,
    val status: Status = Status.PENDING,
    val date: String
)

data class AdminNotification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val isRead: Boolean = false
)

data class ChatMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: String
)

data class PlatformAnnouncement(
    val id: String,
    val title: String,
    val content: String,
    val date: String,
    val isUrgent: Boolean = false
)
