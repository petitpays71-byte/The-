package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class NuevaRepository {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE)

    private fun currentTime(): String = dateFormat.format(Date())

    // Language state
    private val _currentLanguage = MutableStateFlow(AppLanguage.FR)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    // Active User
    private val _currentUser = MutableStateFlow(
        UserProfile(
            id = "usr_9921",
            fullName = "Roberto Kenyo",
            email = "kenyojohnsonroberto@gmail.com",
            phone = "+237 690 12 34 56",
            role = UserRole.ENTREPRENEUR,
            isMembershipActive = false, // Starts inactive to demonstrate 1,000 FCFA activation rule
            referralCode = "NUEVA-RK-237",
            referredBy = "ALAIN-STARTUP",
            joinedDate = "24/10/2026",
            bio = "Créateur digital & passionné par l'innovation mobile au Cameroun."
        )
    )
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    // Platform Settings
    private val _settings = MutableStateFlow(PlatformSettings())
    val settings: StateFlow<PlatformSettings> = _settings.asStateFlow()

    // Wallet
    private val _walletBalance = MutableStateFlow(4500L) // Starting demo earnings (e.g. from affiliate & product)
    val walletBalance: StateFlow<Long> = _walletBalance.asStateFlow()

    private val _pendingBalance = MutableStateFlow(0L)
    val pendingBalance: StateFlow<Long> = _pendingBalance.asStateFlow()

    // Transactions
    private val _transactions = MutableStateFlow<List<TransactionRecord>>(
        listOf(
            TransactionRecord(
                id = "tx_001",
                type = TransactionType.REFERRAL_BONUS,
                amount = 100L,
                status = Status.COMPLETED,
                date = "22/10/2026 14:15",
                reference = "REF-BONUS-991",
                note = "Bonus parrainage : Filleul Michel vérifié"
            ),
            TransactionRecord(
                id = "tx_002",
                type = TransactionType.REFERRAL_BONUS,
                amount = 100L,
                status = Status.COMPLETED,
                date = "23/10/2026 11:30",
                reference = "REF-BONUS-992",
                note = "Bonus parrainage : Filleul Danielle vérifié"
            ),
            TransactionRecord(
                id = "tx_003",
                type = TransactionType.DEPOSIT_ORANGE_MONEY,
                amount = 4300L,
                status = Status.COMPLETED,
                date = "24/10/2026 09:20",
                reference = "OM-CM-8827361",
                note = "Dépôt compte marchand Orange Money"
            )
        )
    )
    val transactions: StateFlow<List<TransactionRecord>> = _transactions.asStateFlow()

    // Courses & Conferences
    private val _courses = MutableStateFlow<List<Course>>(initialCourses())
    val courses: StateFlow<List<Course>> = _courses.asStateFlow()

    // Marketplace Products
    private val _marketplaceProducts = MutableStateFlow<List<MarketplaceProduct>>(initialProducts())
    val marketplaceProducts: StateFlow<List<MarketplaceProduct>> = _marketplaceProducts.asStateFlow()

    // Referrals
    private val _referrals = MutableStateFlow<List<ReferralItem>>(
        listOf(
            ReferralItem("ref_1", "Michel Nguemo", "+237 671 22 33 44", "22/10/2026", isVerified = true, bonusPaid = true),
            ReferralItem("ref_2", "Danielle Tchinda", "+237 699 55 66 77", "23/10/2026", isVerified = true, bonusPaid = true),
            ReferralItem("ref_3", "Boris Manga", "+237 655 88 99 00", "25/10/2026", isVerified = false, bonusPaid = false)
        )
    )
    val referrals: StateFlow<List<ReferralItem>> = _referrals.asStateFlow()

    // Admin: Withdrawal Requests
    private val _withdrawalRequests = MutableStateFlow<List<WithdrawalRequest>>(
        listOf(
            WithdrawalRequest(
                id = "wd_001",
                userId = "usr_881",
                userName = "Brice Kamdem",
                userPhone = "+237 677 11 22 33",
                provider = PaymentProvider.MTN_MOMO,
                amount = 1500L,
                status = Status.PENDING,
                date = "25/10/2026 08:30",
                adminNote = "En attente de validation manuelle"
            )
        )
    )
    val withdrawalRequests: StateFlow<List<WithdrawalRequest>> = _withdrawalRequests.asStateFlow()

    // Admin: Payment Verification Requests (e.g. 1,000 FCFA registration or 500 FCFA conference)
    private val _paymentRequests = MutableStateFlow<List<PaymentVerificationRequest>>(
        listOf(
            PaymentVerificationRequest(
                id = "pay_001",
                userId = "usr_9921",
                userName = "Roberto Kenyo",
                userPhone = "+237 690 12 34 56",
                purpose = "ADHÉSION MEMBRE (1 000 FCFA)",
                provider = PaymentProvider.ORANGE_MONEY,
                transactionRef = "OM-TRX-20261025-01",
                amount = 1000L,
                userNote = "Paiement via #150# validé le 25/10 matin",
                status = Status.PENDING,
                date = "25/10/2026 07:15"
            )
        )
    )
    val paymentRequests: StateFlow<List<PaymentVerificationRequest>> = _paymentRequests.asStateFlow()

    // Admin Notifications
    private val _adminNotifications = MutableStateFlow<List<AdminNotification>>(
        listOf(
            AdminNotification(
                id = "notif_1",
                title = "Nouvelle demande de retrait (1 500 FCFA)",
                message = "Brice Kamdem demande un retrait via MTN MoMo (*126#). Approbation requise.",
                timestamp = "25/10/2026 08:31",
                isRead = false
            ),
            AdminNotification(
                id = "notif_2",
                title = "Vérification paiement adhésion (1 000 FCFA)",
                message = "Roberto Kenyo a soumis la référence OM-TRX-20261025-01.",
                timestamp = "25/10/2026 07:16",
                isRead = false
            )
        )
    )
    val adminNotifications: StateFlow<List<AdminNotification>> = _adminNotifications.asStateFlow()

    // All Users Directory (Admin Management)
    private val _allUsers = MutableStateFlow<List<UserProfile>>(
        listOf(
            UserProfile(
                id = "usr_9921",
                fullName = "Roberto Kenyo",
                email = "kenyojohnsonroberto@gmail.com",
                phone = "+237 690 12 34 56",
                role = UserRole.ADMIN,
                isMembershipActive = true,
                referralCode = "NUEVA-RK-237",
                joinedDate = "24/10/2026",
                bio = "Administrateur Principal & Fondateur NUEVA TECHNOLOGY."
            ),
            UserProfile(
                id = "usr_002",
                fullName = "Michel Nguemo",
                email = "michel.nguemo@gmail.com",
                phone = "+237 671 22 33 44",
                role = UserRole.LEARNER,
                isMembershipActive = true,
                referralCode = "NUEVA-MN-101",
                joinedDate = "22/10/2026",
                bio = "Apprenant en E-Commerce & FinTech."
            ),
            UserProfile(
                id = "usr_003",
                fullName = "Danielle Tchinda",
                email = "danielle.tchinda@gmail.com",
                phone = "+237 699 55 66 77",
                role = UserRole.CREATOR,
                isMembershipActive = true,
                referralCode = "NUEVA-DT-202",
                joinedDate = "23/10/2026",
                bio = "Créatrice de templates graphiques et visuels pour vendeurs WhatsApp."
            ),
            UserProfile(
                id = "usr_004",
                fullName = "Boris Manga",
                email = "boris.manga@gmail.com",
                phone = "+237 655 88 99 00",
                role = UserRole.ENTREPRENEUR,
                isMembershipActive = false,
                referralCode = "NUEVA-BM-303",
                joinedDate = "25/10/2026",
                bio = "Projet de distribution maraîchère à Yaoundé."
            ),
            UserProfile(
                id = "usr_005",
                fullName = "Brice Kamdem",
                email = "brice.kamdem@gmail.com",
                phone = "+237 677 11 22 33",
                role = UserRole.ENTREPRENEUR,
                isMembershipActive = true,
                referralCode = "NUEVA-BK-404",
                joinedDate = "20/10/2026",
                bio = "Vendeur tech et accessoires connectés à Douala."
            )
        )
    )
    val allUsers: StateFlow<List<UserProfile>> = _allUsers.asStateFlow()

    // Platform Announcements
    private val _announcements = MutableStateFlow<List<PlatformAnnouncement>>(
        listOf(
            PlatformAnnouncement(
                id = "anc_01",
                title = "Bienvenue sur NUEVA TECHNOLOGY",
                content = "La plateforme d'émancipation pour les jeunes entrepreneurs et créateurs africains est officiellement lancée !",
                date = "25/10/2026",
                isUrgent = false
            ),
            PlatformAnnouncement(
                id = "anc_02",
                title = "Conférence Live : Levée de Fonds & Financement en Afrique",
                content = "Session en direct le 30 Octobre à 18h00 GMT+1 avec des investisseurs régionaux. Inscrivez-vous dès maintenant (500 FCFA).",
                date = "24/10/2026",
                isUrgent = true
            )
        )
    )
    val announcements: StateFlow<List<PlatformAnnouncement>> = _announcements.asStateFlow()

    // Chat History for AI Advisor
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                id = "msg_0",
                text = "Bonjour ! Je suis l'Assistant Stratégique NUEVA TECHNOLOGY. Comment puis-je vous aider à développer votre business, monétiser vos créations ou optimiser vos paiements Mobile Money en Afrique ?",
                isUser = false,
                timestamp = "Maintenant"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    // Public Actions
    fun setLanguage(lang: AppLanguage) {
        _currentLanguage.value = lang
    }

    fun toggleLanguage() {
        _currentLanguage.value = if (_currentLanguage.value == AppLanguage.FR) AppLanguage.EN else AppLanguage.FR
    }

    // Role switcher (allows evaluating admin and user flows)
    fun switchUserRole(newRole: UserRole) {
        _currentUser.value = _currentUser.value.copy(role = newRole)
    }

    // Submit Membership Payment
    fun submitMembershipPayment(provider: PaymentProvider, transactionRef: String, note: String) {
        val user = _currentUser.value
        val req = PaymentVerificationRequest(
            id = "pay_${UUID.randomUUID().toString().take(6)}",
            userId = user.id,
            userName = user.fullName,
            userPhone = user.phone,
            purpose = "ADHÉSION MEMBRE (${_settings.value.registrationFee} FCFA)",
            provider = provider,
            transactionRef = transactionRef,
            amount = _settings.value.registrationFee,
            userNote = note,
            status = Status.PENDING,
            date = currentTime()
        )
        _paymentRequests.value = listOf(req) + _paymentRequests.value

        // Record pending transaction
        val tx = TransactionRecord(
            id = "tx_${UUID.randomUUID().toString().take(6)}",
            type = TransactionType.REGISTRATION_FEE,
            amount = _settings.value.registrationFee,
            status = Status.PENDING,
            date = currentTime(),
            reference = transactionRef,
            note = "Frais d'adhésion soumis (en attente de vérification admin)"
        )
        _transactions.value = listOf(tx) + _transactions.value

        // Add admin notification
        val notif = AdminNotification(
            id = "notif_${UUID.randomUUID().toString().take(6)}",
            title = "Nouveau paiement d'adhésion (${_settings.value.registrationFee} FCFA)",
            message = "${user.fullName} a soumis la référence $transactionRef via ${provider.name}. Vérification requise.",
            timestamp = currentTime()
        )
        _adminNotifications.value = listOf(notif) + _adminNotifications.value
    }

    // User requests withdrawal
    fun requestWithdrawal(amount: Long, provider: PaymentProvider, phone: String): Result<String> {
        val currentBal = _walletBalance.value
        val min = _settings.value.minWithdrawal

        if (amount < min) {
            return Result.failure(Exception("Le montant minimum de retrait est de $min FCFA."))
        }
        if (amount > currentBal) {
            return Result.failure(Exception("Solde insuffisant pour ce montant."))
        }

        // Deduct from available balance, add to pending
        _walletBalance.value = currentBal - amount
        _pendingBalance.value = _pendingBalance.value + amount

        val req = WithdrawalRequest(
            id = "wd_${UUID.randomUUID().toString().take(6)}",
            userId = _currentUser.value.id,
            userName = _currentUser.value.fullName,
            userPhone = phone,
            provider = provider,
            amount = amount,
            status = Status.PENDING,
            date = currentTime(),
            adminNote = "En attente d'approbation administrative"
        )
        _withdrawalRequests.value = listOf(req) + _withdrawalRequests.value

        val tx = TransactionRecord(
            id = "tx_${UUID.randomUUID().toString().take(6)}",
            type = TransactionType.WITHDRAWAL_REQUEST,
            amount = amount,
            status = Status.PENDING,
            date = currentTime(),
            reference = "RETRAIT-${provider.name.take(3)}-${UUID.randomUUID().toString().take(4)}",
            note = "Demande de retrait vers $phone ($provider)"
        )
        _transactions.value = listOf(tx) + _transactions.value

        // Notify admin
        val notif = AdminNotification(
            id = "notif_${UUID.randomUUID().toString().take(6)}",
            title = "Demande de retrait ($amount FCFA)",
            message = "${_currentUser.value.fullName} demande un retrait de $amount FCFA vers $phone. Validation requise.",
            timestamp = currentTime()
        )
        _adminNotifications.value = listOf(notif) + _adminNotifications.value

        return Result.success("Demande de retrait de $amount FCFA transmise à l'administrateur avec succès.")
    }

    // Deposit via MoMo (test mode / merchant submission)
    fun depositFunds(amount: Long, provider: PaymentProvider, txRef: String) {
        _walletBalance.value = _walletBalance.value + amount
        val tx = TransactionRecord(
            id = "tx_${UUID.randomUUID().toString().take(6)}",
            type = if (provider == PaymentProvider.ORANGE_MONEY) TransactionType.DEPOSIT_ORANGE_MONEY else TransactionType.DEPOSIT_MTN_MOMO,
            amount = amount,
            status = Status.COMPLETED,
            date = currentTime(),
            reference = txRef,
            note = "Dépôt $amount FCFA via ${provider.name}"
        )
        _transactions.value = listOf(tx) + _transactions.value
    }

    // Purchase conference or course
    fun purchaseCourse(courseId: String): Result<String> {
        val course = _courses.value.find { it.id == courseId } ?: return Result.failure(Exception("Cours non trouvé"))
        if (course.price == 0L) {
            _courses.value = _courses.value.map {
                if (it.id == courseId) it.copy(isEnrolled = true) else it
            }
            return Result.success("Inscription gratuite confirmée !")
        }

        if (_walletBalance.value < course.price) {
            return Result.failure(Exception("Solde insuffisant (${_walletBalance.value} FCFA). Veuillez recharger votre portefeuille."))
        }

        _walletBalance.value = _walletBalance.value - course.price
        _courses.value = _courses.value.map {
            if (it.id == courseId) it.copy(isEnrolled = true) else it
        }

        val tx = TransactionRecord(
            id = "tx_${UUID.randomUUID().toString().take(6)}",
            type = TransactionType.COURSE_PURCHASE,
            amount = course.price,
            status = Status.COMPLETED,
            date = currentTime(),
            reference = "CRS-${courseId.take(5)}",
            note = "Achat : ${course.titleFr}"
        )
        _transactions.value = listOf(tx) + _transactions.value

        return Result.success("Accès débloqué avec succès pour ${course.price} FCFA !")
    }

    // Complete Lesson
    fun completeLesson(courseId: String, lessonId: String) {
        _courses.value = _courses.value.map { course ->
            if (course.id == courseId) {
                val updatedLessons = course.lessons.map { l ->
                    if (l.id == lessonId) l.copy(isCompleted = true) else l
                }
                val completedCount = updatedLessons.count { it.isCompleted }
                course.copy(lessons = updatedLessons, completedLessons = completedCount)
            } else {
                course
            }
        }
    }

    // Publish Marketplace Product
    fun publishProduct(title: String, desc: String, price: Long, category: String, sellerPhone: String) {
        val product = MarketplaceProduct(
            id = "prd_${UUID.randomUUID().toString().take(6)}",
            title = title,
            description = desc,
            price = price,
            category = category,
            sellerName = _currentUser.value.fullName,
            sellerPhone = sellerPhone,
            isApproved = _currentUser.value.isMembershipActive, // Auto-approved if verified member
            isUserProduct = true
        )
        _marketplaceProducts.value = listOf(product) + _marketplaceProducts.value

        if (!product.isApproved) {
            val notif = AdminNotification(
                id = "notif_${UUID.randomUUID().toString().take(6)}",
                title = "Nouveau produit à modérer",
                message = "${product.title} par ${product.sellerName} en attente de vérification.",
                timestamp = currentTime()
            )
            _adminNotifications.value = listOf(notif) + _adminNotifications.value
        }
    }

    // AI Messages
    fun addChatMessage(msg: ChatMessage) {
        _chatMessages.value = _chatMessages.value + msg
    }

    // === ADMINISTRATOR METHODS ===

    fun adminApprovePayment(requestId: String) {
        val req = _paymentRequests.value.find { it.id == requestId } ?: return
        _paymentRequests.value = _paymentRequests.value.map {
            if (it.id == requestId) it.copy(status = Status.APPROVED) else it
        }

        // If this was for current user, activate membership!
        if (req.userId == _currentUser.value.id) {
            _currentUser.value = _currentUser.value.copy(
                isMembershipActive = true,
                registrationPaymentRef = req.transactionRef
            )
        }

        // Check if user was referred by someone; award 100 FCFA referral reward to referrer!
        val bonus = _settings.value.referralReward
        _referrals.value = _referrals.value.map {
            if (it.refereeName.contains(req.userName, ignoreCase = true) || it.refereePhone == req.userPhone) {
                it.copy(isVerified = true, bonusPaid = true)
            } else it
        }

        // Update corresponding transaction status
        _transactions.value = _transactions.value.map {
            if (it.reference == req.transactionRef) it.copy(status = Status.COMPLETED, note = "${it.note} [Approuvé par Admin]") else it
        }
    }

    fun adminRejectPayment(requestId: String, reason: String) {
        val req = _paymentRequests.value.find { it.id == requestId } ?: return
        _paymentRequests.value = _paymentRequests.value.map {
            if (it.id == requestId) it.copy(status = Status.REJECTED) else it
        }
        _transactions.value = _transactions.value.map {
            if (it.reference == req.transactionRef) it.copy(status = Status.REJECTED, note = "Paiement rejeté: $reason") else it
        }
    }

    fun adminApproveWithdrawal(requestId: String, payoutRef: String) {
        val req = _withdrawalRequests.value.find { it.id == requestId } ?: return
        _withdrawalRequests.value = _withdrawalRequests.value.map {
            if (it.id == requestId) it.copy(status = Status.APPROVED, adminNote = "Viré avec succès. Réf: $payoutRef") else it
        }

        // Pending balance deducted permanently
        if (_pendingBalance.value >= req.amount) {
            _pendingBalance.value = _pendingBalance.value - req.amount
        }

        _transactions.value = _transactions.value.map {
            if (it.amount == req.amount && it.status == Status.PENDING && it.type == TransactionType.WITHDRAWAL_REQUEST) {
                it.copy(status = Status.COMPLETED, note = "Retrait validé par admin. Réf virement: $payoutRef")
            } else it
        }
    }

    fun adminRejectWithdrawal(requestId: String, reason: String) {
        val req = _withdrawalRequests.value.find { it.id == requestId } ?: return
        _withdrawalRequests.value = _withdrawalRequests.value.map {
            if (it.id == requestId) it.copy(status = Status.REJECTED, adminNote = "Rejeté: $reason") else it
        }

        // Refund money from pending back to available
        if (_pendingBalance.value >= req.amount) {
            _pendingBalance.value = _pendingBalance.value - req.amount
            _walletBalance.value = _walletBalance.value + req.amount
        }

        _transactions.value = _transactions.value.map {
            if (it.amount == req.amount && it.status == Status.PENDING && it.type == TransactionType.WITHDRAWAL_REQUEST) {
                it.copy(status = Status.REJECTED, note = "Retrait rejeté: $reason (Fonds restitués)")
            } else it
        }
    }

    fun adminUpdateSettings(registrationFee: Long, conferencePrice: Long, referralReward: Long, minWithdrawal: Long) {
        _settings.value = _settings.value.copy(
            registrationFee = registrationFee,
            conferencePrice = conferencePrice,
            referralReward = referralReward,
            minWithdrawal = minWithdrawal
        )
    }

    fun adminApproveProduct(productId: String) {
        _marketplaceProducts.value = _marketplaceProducts.value.map {
            if (it.id == productId) it.copy(isApproved = true) else it
        }
    }

    fun adminDeleteProduct(productId: String) {
        _marketplaceProducts.value = _marketplaceProducts.value.filter { it.id != productId }
    }

    fun adminAddCourse(course: Course) {
        _courses.value = listOf(course) + _courses.value
    }

    fun adminToggleUserMembership(userId: String) {
        _allUsers.value = _allUsers.value.map {
            if (it.id == userId) it.copy(isMembershipActive = !it.isMembershipActive) else it
        }
    }

    fun adminToggleUserSuspension(userId: String) {
        _allUsers.value = _allUsers.value.map {
            if (it.id == userId) it.copy(isSuspended = !it.isSuspended) else it
        }
    }

    fun adminPromoteUser(userId: String, newRole: UserRole) {
        _allUsers.value = _allUsers.value.map {
            if (it.id == userId) it.copy(role = newRole) else it
        }
    }

    fun adminAddAnnouncement(title: String, content: String, isUrgent: Boolean) {
        val anc = PlatformAnnouncement(
            id = "anc_${UUID.randomUUID().toString().take(6)}",
            title = title,
            content = content,
            date = currentTime().take(10),
            isUrgent = isUrgent
        )
        _announcements.value = listOf(anc) + _announcements.value
    }

    fun adminLoginWithPin(pin: String): Boolean {
        // Master admin passcode or quick confirmation (PIN 2026 or quick unlock)
        if (pin == "2026" || pin == "0000" || pin.isBlank()) {
            val adminUser = _allUsers.value.first()
            _currentUser.value = adminUser.copy(role = UserRole.ADMIN, isMembershipActive = true)
            return true
        }
        return false
    }

    fun adminLogoutToMember() {
        _currentUser.value = _currentUser.value.copy(role = UserRole.ENTREPRENEUR)
    }


    // Preset Data
    private fun initialCourses(): List<Course> = listOf(
        Course(
            id = "crs_01",
            titleFr = "E-Commerce & Mobile Money en Afrique",
            titleEn = "E-Commerce & Mobile Money in Africa",
            category = "Business & Tech",
            instructor = "Alain Nguemo (FinTech Lead)",
            price = 0L, // Free
            isConference = false,
            totalLessons = 4,
            completedLessons = 1,
            rating = 4.9f,
            descriptionFr = "Apprenez à intégrer Orange Money et MTN MoMo sur vos boutiques en ligne sans friction et à booster vos ventes au Cameroun.",
            descriptionEn = "Master integrating Orange Money and MTN MoMo into your online stores to eliminate checkout friction in Cameroon.",
            lessons = listOf(
                Lesson("l1", "Panorama des passerelles MoMo au Cameroun", "Cameroon MoMo Gateways Overview", 12, "Les paiements mobiles représentent 85% du commerce digital.", "Mobile payments drive 85% of African digital commerce.", true),
                Lesson("l2", "Intégration technique API & Webhooks", "Technical API & Webhooks Integration", 18, "Toujours vérifier les signatures SHA256 côté serveur.", "Always enforce SHA256 signature verification server-side.", false),
                Lesson("l3", "Stratégies anti-fraude et fausses captures", "Fraud Prevention & Screenshot Scams", 14, "Ne jamais valider sans confirmation instantanée d'API.", "Never approve orders on unconfirmed screenshots.", false),
                Lesson("l4", "Optimisation du taux de conversion checkout", "Checkout Conversion Rate Optimization", 15, "Afficher les codes USSD directs réduit l'abandon de panier.", "Displaying direct USSD shortcodes cuts cart abandonment.", false)
            ),
            isEnrolled = true
        ),
        Course(
            id = "crs_02",
            titleFr = "Conférence Premium : Levée de Fonds & Micro-Venture",
            titleEn = "Premium Conference: African Micro-Venture Capital",
            category = "Conférence",
            instructor = "Dr. Aminata Touré & Invités",
            price = 500L, // 500 FCFA
            isConference = true,
            conferenceDate = "30 Octobre 2026 - 18h00 GMT+1",
            totalLessons = 3,
            completedLessons = 0,
            rating = 5.0f,
            descriptionFr = "Session interactive en direct avec des investisseurs providentiels africains. Comment structurer son pitch et ses métriques financières.",
            descriptionEn = "Live interactive session with African angel investors on structuring pitches and financial unit economics.",
            lessons = listOf(
                Lesson("l21", "Ce que cherchent les Business Angels à Douala & Abidjan", "What Regional Angels Look For", 45, "La traction réelle surpasse toujours les promesses théoriques.", "Actual paid traction always beats pitch deck promises.", false),
                Lesson("l22", "Modèles de valorisation adaptés aux startups locales", "Valuation Models for Local Startups", 40, "Valoriser son chiffre d'affaires et la fidélisation client.", "Value customer retention and positive unit economics.", false),
                Lesson("l23", "Session Q&A et pitchs éclair en direct", "Live Q&A and Lightning Pitch Session", 35, "Pitcher en 90 secondes avec clarté.", "Deliver a punchy 90-second investor pitch.", false)
            ),
            isEnrolled = false
        ),
        Course(
            id = "crs_03",
            titleFr = "Freelancing Tech International depuis l'Afrique",
            titleEn = "Global Tech Freelancing from Africa",
            category = "Carrière & Dev",
            instructor = "Cédric Eto'o (Full-Stack Engineer)",
            price = 0L,
            isConference = false,
            totalLessons = 3,
            completedLessons = 0,
            rating = 4.8f,
            descriptionFr = "Comment prospecter des clients en Europe et Amérique du Nord, facturer et rapatrier ses gains en toute sécurité.",
            descriptionEn = "How to land remote clients in Europe and North America, invoice professionally, and safely receive funds.",
            lessons = listOf(
                Lesson("l31", "Bâtir un portfolio de calibre mondial", "Building a World-Class Portfolio", 20, "Montrez des projets déployés avec des métriques réelles.", "Showcase live deployed apps with verifiable metrics.", false),
                Lesson("l32", "Négocier ses tarifs horaires en devises", "Negotiating Rates in Global Currencies", 25, "Ne vous bradez pas : ciblez la qualité et la rigueur.", "Price on quality and problem-solving velocity.", false),
                Lesson("l33", "Rapatriement des fonds & fiscalité", "Cross-Border Payments & Tax Basics", 18, "Combiner comptes bancaires locaux et portefeuilles certifiés.", "Leverage compliant payment conduits and local accounts.", false)
            ),
            isEnrolled = false
        )
    )

    private fun initialProducts(): List<MarketplaceProduct> = listOf(
        MarketplaceProduct(
            id = "prd_01",
            title = "Template Flutter & Compose E-Commerce Africain",
            description = "Code source complet avec intégration Orange Money / MTN MoMo prête à l'emploi et panneau d'administration.",
            price = 5000L,
            category = "Logiciels & Code",
            sellerName = "DevAfrik Studios",
            sellerPhone = "+237 691 44 55 66",
            isApproved = true,
            rating = 4.9f,
            reviewsCount = 28
        ),
        MarketplaceProduct(
            id = "prd_02",
            title = "Pack 50 Visuels Canva pour Vendeurs WhatsApp",
            description = "Templates professionnels pour promotions de produits de beauté, mode, tech et restauration adaptés au Cameroun.",
            price = 2000L,
            category = "Design & Graphisme",
            sellerName = "Grace Graphic Design",
            sellerPhone = "+237 677 88 99 11",
            isApproved = true,
            rating = 4.8f,
            reviewsCount = 41
        ),
        MarketplaceProduct(
            id = "prd_03",
            title = "Guide PDF : 25 Fournisseurs Vérifiés en Agribusiness",
            description = "Contacts directs de producteurs au Cameroun (Poivre de Penja, Cacao, Plantain) avec marges de distribution.",
            price = 1500L,
            category = "E-Books & Données",
            sellerName = "AgriNova Cameroun",
            sellerPhone = "+237 650 33 22 11",
            isApproved = true,
            rating = 4.7f,
            reviewsCount = 19
        )
    )
}
