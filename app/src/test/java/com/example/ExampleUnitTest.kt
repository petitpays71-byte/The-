package com.example

import com.example.data.NuevaRepository
import com.example.model.PaymentProvider
import com.example.model.Status
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ExampleUnitTest {

    private lateinit var repository: NuevaRepository

    @Before
    fun setUp() {
        repository = NuevaRepository()
    }

    @Test
    fun testPlatformDefaultRules() {
        val settings = repository.settings.value
        assertEquals("Registration fee must be 1,000 FCFA", 1000L, settings.registrationFee)
        assertEquals("Conference default price must be 500 FCFA", 500L, settings.conferencePrice)
        assertEquals("Referral reward must be 100 FCFA", 100L, settings.referralReward)
        assertEquals("Minimum withdrawal must be 1,100 FCFA", 1100L, settings.minWithdrawal)
    }

    @Test
    fun testWithdrawalMinimumRuleRejection() {
        // Attempting to withdraw 1000 FCFA when min is 1100 FCFA must fail
        val result = repository.requestWithdrawal(1000L, PaymentProvider.ORANGE_MONEY, "+237 690 00 00 00")
        assertTrue("Withdrawal under minimum threshold must fail", result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("1100") == true)
    }

    @Test
    fun testWithdrawalValidRequestMovesToPending() {
        val initialBal = repository.walletBalance.value
        val initialPending = repository.pendingBalance.value
        val withdrawAmount = 1500L

        val result = repository.requestWithdrawal(withdrawAmount, PaymentProvider.MTN_MOMO, "+237 677 11 22 33")
        assertTrue("Valid withdrawal request must succeed", result.isSuccess)
        assertEquals(initialBal - withdrawAmount, repository.walletBalance.value)
        assertEquals(initialPending + withdrawAmount, repository.pendingBalance.value)

        val latestReq = repository.withdrawalRequests.value.first()
        assertEquals(Status.PENDING, latestReq.status)
        assertEquals(withdrawAmount, latestReq.amount)
    }

    @Test
    fun testAdminApproveWithdrawal() {
        val withdrawAmount = 1500L
        repository.requestWithdrawal(withdrawAmount, PaymentProvider.MTN_MOMO, "+237 677 11 22 33")
        val req = repository.withdrawalRequests.value.first()

        repository.adminApproveWithdrawal(req.id, "MTN-OUT-999")
        val updated = repository.withdrawalRequests.value.find { it.id == req.id }
        assertEquals(Status.APPROVED, updated?.status)
    }

    @Test
    fun testAdminApprovePaymentActivatesMembership() {
        assertFalse(repository.currentUser.value.isMembershipActive)

        // Submit membership payment
        repository.submitMembershipPayment(PaymentProvider.ORANGE_MONEY, "OM-TEST-REF", "Paiement frais 1000 FCFA")
        val req = repository.paymentRequests.value.first()
        assertEquals(Status.PENDING, req.status)

        // Admin approves payment
        repository.adminApprovePayment(req.id)
        assertTrue("User membership must be active upon admin confirmation", repository.currentUser.value.isMembershipActive)
    }

    @Test
    fun testAdminLoginAndUserManagement() {
        val success = repository.adminLoginWithPin("2026")
        assertTrue(success)
        assertEquals(com.example.model.UserRole.ADMIN, repository.currentUser.value.role)

        // Toggle user membership
        val targetUser = repository.allUsers.value.find { it.fullName == "Boris Manga" }!!
        assertFalse(targetUser.isMembershipActive)
        repository.adminToggleUserMembership(targetUser.id)
        val updatedUser = repository.allUsers.value.find { it.id == targetUser.id }!!
        assertTrue(updatedUser.isMembershipActive)

        // Add announcement
        val initialCount = repository.announcements.value.size
        repository.adminAddAnnouncement("Test Notice", "Contenu de test", true)
        assertEquals(initialCount + 1, repository.announcements.value.size)
    }
}
