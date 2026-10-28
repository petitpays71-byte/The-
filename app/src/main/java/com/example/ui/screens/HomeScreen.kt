package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NuevaRepository
import com.example.localization.NuevaStrings
import com.example.model.AppLanguage
import com.example.model.PaymentProvider
import com.example.model.UserRole
import com.example.ui.components.ElectricButton
import com.example.ui.components.FuturisticCard
import com.example.ui.components.PaymentModal
import com.example.ui.components.StatusChip
import com.example.ui.components.WithdrawalModal
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    repository: NuevaRepository,
    onNavigateToTab: (Int) -> Unit,
    onOpenReferral: () -> Unit,
    onOpenCrypto: () -> Unit,
    onOpenAdmin: () -> Unit
) {
    val lang by repository.currentLanguage.collectAsState()
    val user by repository.currentUser.collectAsState()
    val settings by repository.settings.collectAsState()
    val balance by repository.walletBalance.collectAsState()
    val pendingBal by repository.pendingBalance.collectAsState()
    val transactions by repository.transactions.collectAsState()
    val courses by repository.courses.collectAsState()
    val announcements by repository.announcements.collectAsState()

    var showMembershipModal by remember { mutableStateOf(false) }
    var showWithdrawalModal by remember { mutableStateOf(false) }
    var actionMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                MidnightCardElevated,
                                CyberBlue.copy(alpha = 0.5f),
                                MidnightBase
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = ElectricBlue.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "NUEVA AFRICA TECH",
                                color = ElectricBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        // Role Tag
                        Surface(
                            shape = CircleShape,
                            color = MidnightSurface
                        ) {
                            Text(
                                text = user.role.name,
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (lang == AppLanguage.FR) "Bienvenue, ${user.fullName} 👋" else "Welcome, ${user.fullName} 👋",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextWhite
                    )

                    Text(
                        text = NuevaStrings.slogan(lang),
                        color = TextLight,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Membership Status Bar
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = if (user.isMembershipActive) SuccessGreen.copy(alpha = 0.15f) else WarningAmber.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (user.isMembershipActive) SuccessGreen.copy(alpha = 0.5f) else WarningAmber.copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = if (user.isMembershipActive) Icons.Default.Verified else Icons.Default.HourglassTop,
                                    contentDescription = null,
                                    tint = if (user.isMembershipActive) SuccessGreen else WarningAmber,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (user.isMembershipActive) NuevaStrings.membershipActive(lang) else NuevaStrings.membershipPending(lang),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (user.isMembershipActive) SuccessGreen else WarningAmber
                                    )
                                    Text(
                                        text = if (user.isMembershipActive)
                                            (if (lang == AppLanguage.FR) "Adhésion vérifiée et active" else "Verified active membership")
                                        else
                                            (if (lang == AppLanguage.FR) "Paiement Orange/MTN MoMo requis" else "Orange/MTN MoMo payment required"),
                                        fontSize = 11.sp,
                                        color = TextLight
                                    )
                                }
                            }

                            if (!user.isMembershipActive) {
                                Button(
                                    onClick = { showMembershipModal = true },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = WarningAmber, contentColor = MidnightBase),
                                    modifier = Modifier.testTag("activate_membership_btn")
                                ) {
                                    Text(
                                        text = if (lang == AppLanguage.FR) "Payer 1 000 F" else "Pay 1,000 F",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Dedicated Administrator Account Shortcut Card
        if (user.role == UserRole.ADMIN) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable(onClick = onOpenAdmin),
                    color = SuccessGreen.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, SuccessGreen)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Surface(shape = CircleShape, color = SuccessGreen.copy(alpha = 0.25f), modifier = Modifier.size(38.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(22.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (lang == AppLanguage.FR) "Compte Administrateur Actif" else "Administrator Account Active",
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = if (lang == AppLanguage.FR) "Gérer les retraits, utilisateurs et tarifs de la plateforme" else "Manage withdrawals, users, and platform rates",
                                    color = TextLight,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = SuccessGreen)
                    }
                }
            }
        }

        // Platform Announcements
        if (announcements.isNotEmpty()) {
            item {
                Text(
                    text = if (lang == AppLanguage.FR) "Annonces de la Direction" else "Official Platform Broadcasts",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    announcements.forEach { anc ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = MidnightCard,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (anc.isUrgent) ErrorRed.copy(alpha = 0.5f) else MidnightBorder)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (anc.isUrgent) {
                                            Surface(shape = RoundedCornerShape(4.dp), color = ErrorRed.copy(alpha = 0.2f)) {
                                                Text("URGENT", color = ErrorRed, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                        }
                                        Text(anc.title, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                    Text(anc.date, color = TextMuted, fontSize = 10.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(anc.content, color = TextLight, fontSize = 11.sp, lineHeight = 15.sp)
                            }
                        }
                    }
                }
            }
        }

        // Action feedback message banner
        if (actionMessage != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = CyberBlue.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(actionMessage!!, color = TextWhite, fontSize = 12.sp, modifier = Modifier.weight(1f))
                        IconButton(onClick = { actionMessage = null }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = TextMuted)
                        }
                    }
                }
            }
        }

        // Wallet Balance Card
        item {
            FuturisticCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = NuevaStrings.balance(lang),
                            color = TextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "$balance FCFA",
                            color = ElectricBlue,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        if (pendingBal > 0) {
                            Text(
                                text = "${NuevaStrings.pendingBalance(lang)} : $pendingBal FCFA",
                                color = WarningAmber,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = ElectricBlue.copy(alpha = 0.1f),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = ElectricBlue,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ElectricButton(
                        text = if (lang == AppLanguage.FR) "Dépôt MoMo" else "Deposit MoMo",
                        onClick = { onNavigateToTab(3) }, // Go to Wallet tab
                        icon = Icons.Default.Add,
                        modifier = Modifier.weight(1f),
                        testTag = "home_deposit_btn"
                    )

                    OutlinedButton(
                        onClick = { showWithdrawalModal = true },
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .testTag("home_withdraw_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MidnightBorder)
                    ) {
                        Icon(Icons.Default.ArrowOutward, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (lang == AppLanguage.FR) "Retrait" else "Withdraw",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Quick Navigation Grid
        item {
            Text(
                text = if (lang == AppLanguage.FR) "Accès Rapide & Modules" else "Quick Access & Modules",
                color = TextWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickActionCard(
                    icon = Icons.Default.People,
                    title = NuevaStrings.referral(lang),
                    subtitle = if (lang == AppLanguage.FR) "Gagnez 100 F" else "Earn 100 F",
                    accent = NeonCyan,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenReferral
                )
                QuickActionCard(
                    icon = Icons.Default.CurrencyBitcoin,
                    title = "NUEVA CRYPTO",
                    subtitle = if (lang == AppLanguage.FR) "Info & Web3" else "Info & Web3",
                    accent = WarningAmber,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenCrypto
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickActionCard(
                    icon = Icons.Default.SmartToy,
                    title = NuevaStrings.aiAdvisorTitle(lang),
                    subtitle = if (lang == AppLanguage.FR) "Gemini IA" else "Gemini AI",
                    accent = ElectricBlue,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateToTab(4) }
                )
                QuickActionCard(
                    icon = Icons.Default.AdminPanelSettings,
                    title = NuevaStrings.adminDashboard(lang),
                    subtitle = if (lang == AppLanguage.FR) "Gestion & Val." else "Manage & Val.",
                    accent = SuccessGreen,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenAdmin
                )
            }
        }

        // Official Business Rules & Security Card
        item {
            FuturisticCard(borderGlow = false) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = NuevaStrings.businessRulesTitle(lang),
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = NuevaStrings.businessRulesText(
                        regFee = settings.registrationFee,
                        confPrice = settings.conferencePrice,
                        refReward = settings.referralReward,
                        minWith = settings.minWithdrawal,
                        lang = lang
                    ),
                    color = TextLight,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }

        // Featured Courses / Conferences Preview
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (lang == AppLanguage.FR) "Formations & Conférences En Vedette" else "Featured Courses & Conferences",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                TextButton(onClick = { onNavigateToTab(1) }) {
                    Text(if (lang == AppLanguage.FR) "Voir tout" else "View all", color = ElectricBlue, fontSize = 12.sp)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                courses.take(2).forEach { course ->
                    FuturisticCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (course.isConference) MtnMoMo.copy(alpha = 0.2f) else CyberBlue.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = if (course.isConference)
                                            (if (lang == AppLanguage.FR) "CONFÉRENCE DIRECT" else "LIVE CONFERENCE")
                                        else course.category,
                                        color = if (course.isConference) MtnMoMo else NeonCyan,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (lang == AppLanguage.FR) course.titleFr else course.titleEn,
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Par ${course.instructor}",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                            Text(
                                text = if (course.price == 0L) (if (lang == AppLanguage.FR) "GRATUIT" else "FREE") else "${course.price} FCFA",
                                color = if (course.price == 0L) SuccessGreen else ElectricBlue,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // Recent Transactions Preview
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (lang == AppLanguage.FR) "Dernières Transactions" else "Recent Transactions",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                TextButton(onClick = { onNavigateToTab(3) }) {
                    Text(if (lang == AppLanguage.FR) "Historique" else "History", color = ElectricBlue, fontSize = 12.sp)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                transactions.take(3).forEach { tx ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MidnightCard
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(tx.note, color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Text("${tx.date} • Réf: ${tx.reference}", color = TextMuted, fontSize = 11.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${tx.amount} FCFA",
                                    color = ElectricBlue,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                StatusChip(status = tx.status)
                            }
                        }
                    }
                }
            }
        }
    }

    // Membership payment modal
    if (showMembershipModal) {
        PaymentModal(
            purposeTitle = if (lang == AppLanguage.FR) "Frais d'Adhésion NUEVA TECHNOLOGY" else "NUEVA TECHNOLOGY Membership Fee",
            amount = settings.registrationFee,
            lang = lang,
            onDismiss = { showMembershipModal = false },
            onSubmit = { provider, ref, note ->
                repository.submitMembershipPayment(provider, ref, note)
                showMembershipModal = false
                actionMessage = if (lang == AppLanguage.FR)
                    "Demande soumise ! Réf: $ref. Un administrateur validera votre adhésion sous peu."
                else
                    "Request submitted! Ref: $ref. An administrator will verify your membership shortly."
            }
        )
    }

    // Withdrawal modal
    if (showWithdrawalModal) {
        WithdrawalModal(
            availableBalance = balance,
            minWithdrawal = settings.minWithdrawal,
            lang = lang,
            onDismiss = { showWithdrawalModal = false },
            onSubmit = { amount, provider, phone ->
                val result = repository.requestWithdrawal(amount, provider, phone)
                showWithdrawalModal = false
                actionMessage = result.getOrNull() ?: result.exceptionOrNull()?.message
            }
        )
    }
}

@Composable
fun QuickActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        color = MidnightCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, MidnightBorder)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = accent.copy(alpha = 0.15f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(22.dp))
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(title, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(subtitle, color = TextMuted, fontSize = 11.sp)
            }
        }
    }
}
