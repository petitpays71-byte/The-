package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.NuevaRepository
import com.example.localization.NuevaStrings
import com.example.model.*
import com.example.ui.components.ElectricButton
import com.example.ui.components.FuturisticCard
import com.example.ui.components.StatusChip
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    repository: NuevaRepository,
    onBack: () -> Unit
) {
    val lang by repository.currentLanguage.collectAsState()
    val settings by repository.settings.collectAsState()
    val withdrawalRequests by repository.withdrawalRequests.collectAsState()
    val paymentRequests by repository.paymentRequests.collectAsState()
    val courses by repository.courses.collectAsState()
    val marketplaceProducts by repository.marketplaceProducts.collectAsState()
    val notifications by repository.adminNotifications.collectAsState()
    val allUsers by repository.allUsers.collectAsState()
    val announcements by repository.announcements.collectAsState()
    val currentUser by repository.currentUser.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var feedbackNotice by remember { mutableStateOf<String?>(null) }

    // Dialog for approving withdrawal with payout reference
    var approvingWithdrawal by remember { mutableStateOf<WithdrawalRequest?>(null) }
    var payoutRefInput by remember { mutableStateOf("") }

    // Dialog for editing platform settings
    var showEditSettings by remember { mutableStateOf(false) }

    // Dialog for creating an announcement
    var showNewAnnouncementDialog by remember { mutableStateOf(false) }

    val adminTabs = if (lang == AppLanguage.FR) {
        listOf(
            "Retraits (${withdrawalRequests.count { it.status == Status.PENDING }})",
            "Paiements (${paymentRequests.count { it.status == Status.PENDING }})",
            "Utilisateurs (${allUsers.size})",
            "Annonces (${announcements.size})",
            "Formations",
            "Marketplace",
            "Paramètres",
            "Journal"
        )
    } else {
        listOf(
            "Withdrawals (${withdrawalRequests.count { it.status == Status.PENDING }})",
            "Payments (${paymentRequests.count { it.status == Status.PENDING }})",
            "Users (${allUsers.size})",
            "Announcements (${announcements.size})",
            "Courses",
            "Marketplace",
            "Settings",
            "Audit Log"
        )
    }

    Scaffold(
        containerColor = MidnightBase,
        topBar = {
            TopAppBar(
                title = { Text(NuevaStrings.adminDashboard(lang), color = TextWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = ElectricBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MidnightSurface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("admin_dashboard_screen")
        ) {
            // Administrator Account Profile Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MidnightSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MidnightBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = CircleShape,
                            color = SuccessGreen.copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (lang == AppLanguage.FR) "Compte Administrateur :" else "Admin Account:",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = currentUser.fullName,
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                            Text(
                                text = currentUser.email,
                                color = ElectricBlue,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (currentUser.role == UserRole.ADMIN) SuccessGreen.copy(alpha = 0.2f) else MidnightCard,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (currentUser.role == UserRole.ADMIN) SuccessGreen else MidnightBorder
                        ),
                        modifier = Modifier.clickable {
                            if (currentUser.role == UserRole.ADMIN) {
                                repository.adminLogoutToMember()
                                feedbackNotice = if (lang == AppLanguage.FR) "Basculé en mode membre standard." else "Switched to standard user mode."
                            } else {
                                repository.adminLoginWithPin("2026")
                                feedbackNotice = if (lang == AppLanguage.FR) "Connecté en Super-Administrateur !" else "Logged in as Super-Admin!"
                            }
                        }
                    ) {
                        Text(
                            text = if (currentUser.role == UserRole.ADMIN)
                                (if (lang == AppLanguage.FR) "Super-Admin Actif" else "Super-Admin Active")
                            else
                                (if (lang == AppLanguage.FR) "Activer Mode Admin" else "Enable Admin Mode"),
                            color = if (currentUser.role == UserRole.ADMIN) SuccessGreen else ElectricBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            // Tabs Row
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MidnightSurface,
                contentColor = ElectricBlue,
                edgePadding = 8.dp
            ) {
                adminTabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) ElectricBlue else TextMuted,
                                fontSize = 12.sp
                            )
                        }
                    )
                }
            }

            if (feedbackNotice != null) {
                Surface(
                    modifier = Modifier.padding(12.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = SuccessGreen.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(feedbackNotice!!, color = TextWhite, fontSize = 12.sp, modifier = Modifier.weight(1f))
                        IconButton(onClick = { feedbackNotice = null }, modifier = Modifier.size(20.dp)) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = TextMuted)
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // TAB 0: WITHDRAWALS
                if (selectedTab == 0) {
                    item {
                        Text(
                            text = if (lang == AppLanguage.FR) "Demandes de Retrait en Attente" else "Pending Withdrawal Requests",
                            fontWeight = FontWeight.Bold,
                            color = TextWhite,
                            fontSize = 15.sp
                        )
                        Text(
                            text = if (lang == AppLanguage.FR)
                                "Règle stricte : Tout retrait requiert l'approbation d'un administrateur (Minimum : ${settings.minWithdrawal} FCFA)."
                            else
                                "Strict rule: All withdrawals require administrator approval (Minimum: ${settings.minWithdrawal} FCFA).",
                            color = WarningAmber,
                            fontSize = 11.sp
                        )
                    }

                    if (withdrawalRequests.isEmpty()) {
                        item {
                            Text(if (lang == AppLanguage.FR) "Aucune demande de retrait." else "No withdrawal requests.", color = TextMuted, fontSize = 13.sp)
                        }
                    }

                    items(withdrawalRequests, key = { it.id }) { req ->
                        FuturisticCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(req.userName, fontWeight = FontWeight.Bold, color = TextWhite, fontSize = 14.sp)
                                    Text("Numéro : ${req.userPhone} (${req.provider.name})", color = TextMuted, fontSize = 12.sp)
                                    Text("Date : ${req.date}", color = TextMuted, fontSize = 11.sp)
                                    if (req.adminNote.isNotEmpty()) {
                                        Text("Note admin : ${req.adminNote}", color = NeonCyan, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
                                    }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("${req.amount} FCFA", fontWeight = FontWeight.ExtraBold, color = ElectricBlue, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    StatusChip(status = req.status)
                                }
                            }

                            if (req.status == Status.PENDING) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            repository.adminRejectWithdrawal(req.id, "Informations MoMo non vérifiables")
                                            feedbackNotice = "Demande rejetée et fonds restitués au solde de l'utilisateur."
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(if (lang == AppLanguage.FR) "Rejeter" else "Reject", fontSize = 12.sp)
                                    }

                                    ElectricButton(
                                        text = if (lang == AppLanguage.FR) "Approuver Virement" else "Approve Payout",
                                        onClick = {
                                            approvingWithdrawal = req
                                            payoutRefInput = "OM-PAYOUT-${(1000..9999).random()}"
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }

                // TAB 1: PAYMENTS VERIFICATION
                if (selectedTab == 1) {
                    item {
                        Text(
                            text = if (lang == AppLanguage.FR) "Vérification des Paiements Mobile Money" else "Mobile Money Payment Verification",
                            fontWeight = FontWeight.Bold,
                            color = TextWhite,
                            fontSize = 15.sp
                        )
                        Text(
                            text = if (lang == AppLanguage.FR)
                                "Règle de sécurité : Ne jamais valider sur simple capture d'écran. Vérifiez toujours la transaction sur le portail marchand Orange/MTN."
                            else
                                "Security policy: Never approve solely on screenshot. Always verify on official merchant portal.",
                            color = WarningAmber,
                            fontSize = 11.sp
                        )
                    }

                    if (paymentRequests.isEmpty()) {
                        item {
                            Text(if (lang == AppLanguage.FR) "Aucun paiement en attente." else "No pending payments.", color = TextMuted, fontSize = 13.sp)
                        }
                    }

                    items(paymentRequests, key = { it.id }) { pay ->
                        FuturisticCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(pay.userName, fontWeight = FontWeight.Bold, color = TextWhite, fontSize = 14.sp)
                                    Text("Objet : ${pay.purpose}", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    Text("Canal : ${pay.provider.name} • Tél: ${pay.userPhone}", color = TextMuted, fontSize = 11.sp)
                                    Text("Réf SMS soumise : ${pay.transactionRef}", color = ElectricBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    if (pay.userNote.isNotEmpty()) {
                                        Text("Note client : ${pay.userNote}", color = TextLight, fontSize = 11.sp)
                                    }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("${pay.amount} FCFA", fontWeight = FontWeight.ExtraBold, color = ElectricBlue, fontSize = 15.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    StatusChip(status = pay.status)
                                }
                            }

                            if (pay.status == Status.PENDING) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            repository.adminRejectPayment(pay.id, "Réf SMS introuvable chez l'opérateur")
                                            feedbackNotice = "Paiement rejeté."
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(if (lang == AppLanguage.FR) "Rejeter" else "Reject", fontSize = 12.sp)
                                    }

                                    ElectricButton(
                                        text = if (lang == AppLanguage.FR) "Valider Adhésion" else "Confirm & Activate",
                                        onClick = {
                                            repository.adminApprovePayment(pay.id)
                                            feedbackNotice = "Paiement vérifié ! Adhésion activée et bonus de parrainage attribué si applicable."
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }

                // TAB 2: USERS DIRECTORY MANAGEMENT (NEW!)
                if (selectedTab == 2) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (lang == AppLanguage.FR) "Gestion des Utilisateurs & Membres" else "Users & Member Directory",
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = if (lang == AppLanguage.FR) "Activez manuellement des adhésions ou modifiez les statuts." else "Manually activate memberships or change roles.",
                                    color = TextLight,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    items(allUsers, key = { it.id }) { u ->
                        FuturisticCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(u.fullName, fontWeight = FontWeight.Bold, color = TextWhite, fontSize = 14.sp)
                                        if (u.role == UserRole.ADMIN) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(shape = RoundedCornerShape(4.dp), color = SuccessGreen.copy(alpha = 0.2f)) {
                                                Text("ADMIN", color = SuccessGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                            }
                                        }
                                    }
                                    Text(u.email, color = ElectricBlue, fontSize = 12.sp)
                                    Text("${u.phone} • Rôle : ${u.role.name}", color = TextMuted, fontSize = 11.sp)
                                    Text("Inscrit le : ${u.joinedDate} • Code : ${u.referralCode}", color = TextMuted, fontSize = 11.sp)
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (u.isMembershipActive) SuccessGreen.copy(alpha = 0.2f) else WarningAmber.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = if (u.isMembershipActive) (if (lang == AppLanguage.FR) "ADHÉSION ACTIVE" else "ACTIVE") else (if (lang == AppLanguage.FR) "EN ATTENTE 1 000 F" else "PENDING"),
                                        color = if (u.isMembershipActive) SuccessGreen else WarningAmber,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        repository.adminToggleUserMembership(u.id)
                                        feedbackNotice = if (u.isMembershipActive) "Adhésion suspendue pour ${u.fullName}." else "Adhésion activée pour ${u.fullName} !"
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = if (u.isMembershipActive) (if (lang == AppLanguage.FR) "Désactiver" else "Deactivate") else (if (lang == AppLanguage.FR) "Activer (1 000 F)" else "Activate"),
                                        fontSize = 11.sp,
                                        color = if (u.isMembershipActive) WarningAmber else SuccessGreen
                                    )
                                }

                                if (u.role != UserRole.ADMIN) {
                                    Button(
                                        onClick = {
                                            repository.adminPromoteUser(u.id, UserRole.ADMIN)
                                            feedbackNotice = "${u.fullName} est désormais Administrateur !"
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(if (lang == AppLanguage.FR) "Rendre Admin" else "Make Admin", fontSize = 11.sp)
                                    }
                                } else {
                                    OutlinedButton(
                                        onClick = {
                                            repository.adminPromoteUser(u.id, UserRole.ENTREPRENEUR)
                                            feedbackNotice = "${u.fullName} est désormais Membre Entrepreneur."
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(if (lang == AppLanguage.FR) "Rôle Membre" else "Set Member", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // TAB 3: ANNOUNCEMENTS MANAGEMENT (NEW!)
                if (selectedTab == 3) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (lang == AppLanguage.FR) "Annonces & Communications Officielles" else "Broadcast Announcements",
                                fontWeight = FontWeight.Bold,
                                color = TextWhite,
                                fontSize = 15.sp
                            )
                            ElectricButton(
                                text = if (lang == AppLanguage.FR) "+ Nouvelle Annonce" else "+ Broadcast",
                                onClick = { showNewAnnouncementDialog = true }
                            )
                        }
                    }

                    items(announcements, key = { it.id }) { anc ->
                        FuturisticCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (anc.isUrgent) {
                                            Surface(shape = RoundedCornerShape(4.dp), color = ErrorRed.copy(alpha = 0.2f)) {
                                                Text("IMPORTANT", color = ErrorRed, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                        }
                                        Text(anc.title, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(anc.content, color = TextLight, fontSize = 12.sp, lineHeight = 16.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Publié le : ${anc.date}", color = TextMuted, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }

                // TAB 4: COURSES & CONFERENCES MANAGEMENT
                if (selectedTab == 4) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (lang == AppLanguage.FR) "Catalogue Formations & Conférences" else "Curriculum & Conferences",
                                fontWeight = FontWeight.Bold,
                                color = TextWhite,
                                fontSize = 15.sp
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = CyberBlue.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue)
                            ) {
                                Text(
                                    text = "Prix Conférence : ${settings.conferencePrice} FCFA",
                                    color = ElectricBlue,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    items(courses, key = { it.id }) { c ->
                        FuturisticCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(c.titleFr, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Instructeur: ${c.instructor} • ${c.totalLessons} leçons", color = TextMuted, fontSize = 11.sp)
                                    Text(
                                        if (c.isConference) "Type : Conférence en direct" else "Type : Formation standard",
                                        color = if (c.isConference) MtnMoMo else NeonCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Text("${c.price} FCFA", color = ElectricBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }

                // TAB 5: MARKETPLACE MODERATION
                if (selectedTab == 5) {
                    item {
                        Text(
                            text = if (lang == AppLanguage.FR) "Modération Marketplace" else "Marketplace Moderation",
                            fontWeight = FontWeight.Bold,
                            color = TextWhite,
                            fontSize = 15.sp
                        )
                    }

                    items(marketplaceProducts, key = { it.id }) { prod ->
                        FuturisticCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(prod.title, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Vendeur : ${prod.sellerName} (${prod.sellerPhone})", color = TextMuted, fontSize = 11.sp)
                                    Text("Prix : ${prod.price} FCFA • Cat: ${prod.category}", color = ElectricBlue, fontSize = 11.sp)
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (prod.isApproved) SuccessGreen.copy(alpha = 0.2f) else WarningAmber.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = if (prod.isApproved) "APPROUVÉ" else "EN ATTENTE",
                                        color = if (prod.isApproved) SuccessGreen else WarningAmber,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (!prod.isApproved) {
                                    Button(
                                        onClick = {
                                            repository.adminApproveProduct(prod.id)
                                            feedbackNotice = "Produit ${prod.title} approuvé pour la Marketplace !"
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(if (lang == AppLanguage.FR) "Approuver" else "Approve", fontSize = 11.sp)
                                    }
                                }

                                OutlinedButton(
                                    onClick = {
                                        repository.adminDeleteProduct(prod.id)
                                        feedbackNotice = "Produit supprimé."
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(if (lang == AppLanguage.FR) "Supprimer" else "Delete", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                // TAB 6: PLATFORM SETTINGS
                if (selectedTab == 6) {
                    item {
                        FuturisticCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (lang == AppLanguage.FR) "Paramètres & Tarifs de la Plateforme" else "Platform Pricing & Rules",
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                TextButton(onClick = { showEditSettings = true }) {
                                    Text(if (lang == AppLanguage.FR) "Modifier" else "Edit", color = ElectricBlue)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            SettingItemRow(
                                label = if (lang == AppLanguage.FR) "Frais d'Adhésion Membre" else "Membership Registration Fee",
                                value = "${settings.registrationFee} FCFA"
                            )
                            SettingItemRow(
                                label = if (lang == AppLanguage.FR) "Prix Conférence Premium" else "Premium Conference Price",
                                value = "${settings.conferencePrice} FCFA"
                            )
                            SettingItemRow(
                                label = if (lang == AppLanguage.FR) "Récompense de Parrainage" else "Referral Reward per Member",
                                value = "${settings.referralReward} FCFA"
                            )
                            SettingItemRow(
                                label = if (lang == AppLanguage.FR) "Seuil Minimum de Retrait" else "Minimum Withdrawal Threshold",
                                value = "${settings.minWithdrawal} FCFA"
                            )
                        }
                    }
                }

                // TAB 7: NOTIFICATIONS & AUDIT LOG
                if (selectedTab == 7) {
                    item {
                        Text(
                            text = if (lang == AppLanguage.FR) "Historique des Notifications & Journal d'Audit" else "Admin Notifications & Audit Log",
                            fontWeight = FontWeight.Bold,
                            color = TextWhite,
                            fontSize = 15.sp
                        )
                    }

                    items(notifications, key = { it.id }) { notif ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = MidnightCard
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(notif.title, color = ElectricBlue, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(notif.timestamp, color = TextMuted, fontSize = 10.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(notif.message, color = TextLight, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal to approve withdrawal
    if (approvingWithdrawal != null) {
        val req = approvingWithdrawal!!
        Dialog(onDismissRequest = { approvingWithdrawal = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (lang == AppLanguage.FR) "Confirmer le Virement MoMo" else "Confirm MoMo Payout",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ElectricBlue
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Virement de ${req.amount} FCFA vers ${req.userName} (${req.userPhone}) via ${req.provider.name}.",
                        color = TextLight,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = payoutRefInput,
                        onValueChange = { payoutRefInput = it },
                        label = { Text(if (lang == AppLanguage.FR) "Référence du virement émis" else "Outgoing Transfer Reference") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = MidnightBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { approvingWithdrawal = null }, modifier = Modifier.weight(1f)) {
                            Text(NuevaStrings.cancel(lang))
                        }
                        ElectricButton(
                            text = if (lang == AppLanguage.FR) "Valider Virement" else "Approve Payout",
                            onClick = {
                                repository.adminApproveWithdrawal(req.id, payoutRefInput.trim())
                                approvingWithdrawal = null
                                feedbackNotice = "Retrait de ${req.amount} FCFA validé avec succès !"
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }

    // Edit Settings Dialog
    if (showEditSettings) {
        var regFeeStr by remember { mutableStateOf(settings.registrationFee.toString()) }
        var confPriceStr by remember { mutableStateOf(settings.conferencePrice.toString()) }
        var refRewardStr by remember { mutableStateOf(settings.referralReward.toString()) }
        var minWithStr by remember { mutableStateOf(settings.minWithdrawal.toString()) }

        Dialog(onDismissRequest = { showEditSettings = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (lang == AppLanguage.FR) "Modifier Tarifs Plateforme" else "Edit Platform Rates",
                        fontWeight = FontWeight.Bold,
                        color = ElectricBlue,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = regFeeStr,
                        onValueChange = { regFeeStr = it },
                        label = { Text("Frais Adhésion (FCFA)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = confPriceStr,
                        onValueChange = { confPriceStr = it },
                        label = { Text("Prix Conférence (FCFA)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = refRewardStr,
                        onValueChange = { refRewardStr = it },
                        label = { Text("Bonus Parrainage (FCFA)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = minWithStr,
                        onValueChange = { minWithStr = it },
                        label = { Text("Retrait Minimum (FCFA)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { showEditSettings = false }, modifier = Modifier.weight(1f)) {
                            Text(NuevaStrings.cancel(lang))
                        }
                        ElectricButton(
                            text = NuevaStrings.confirm(lang),
                            onClick = {
                                val rf = regFeeStr.toLongOrNull() ?: settings.registrationFee
                                val cp = confPriceStr.toLongOrNull() ?: settings.conferencePrice
                                val rr = refRewardStr.toLongOrNull() ?: settings.referralReward
                                val mw = minWithStr.toLongOrNull() ?: settings.minWithdrawal
                                repository.adminUpdateSettings(rf, cp, rr, mw)
                                showEditSettings = false
                                feedbackNotice = "Paramètres de la plateforme mis à jour !"
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }

    // New Announcement Dialog
    if (showNewAnnouncementDialog) {
        var ancTitle by remember { mutableStateOf("") }
        var ancContent by remember { mutableStateOf("") }
        var isUrgent by remember { mutableStateOf(false) }

        Dialog(onDismissRequest = { showNewAnnouncementDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (lang == AppLanguage.FR) "Publier une Annonce Plateforme" else "Publish Platform Broadcast",
                        fontWeight = FontWeight.Bold,
                        color = ElectricBlue,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = ancTitle,
                        onValueChange = { ancTitle = it },
                        label = { Text("Titre de l'annonce") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = ancContent,
                        onValueChange = { ancContent = it },
                        label = { Text("Message pour les utilisateurs") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isUrgent,
                            onCheckedChange = { isUrgent = it }
                        )
                        Text(
                            text = if (lang == AppLanguage.FR) "Marquer comme annonce urgente / importante" else "Mark as urgent / critical broadcast",
                            color = TextLight,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { showNewAnnouncementDialog = false }, modifier = Modifier.weight(1f)) {
                            Text(NuevaStrings.cancel(lang))
                        }
                        ElectricButton(
                            text = if (lang == AppLanguage.FR) "Diffuser" else "Broadcast",
                            onClick = {
                                if (ancTitle.isNotBlank() && ancContent.isNotBlank()) {
                                    repository.adminAddAnnouncement(ancTitle.trim(), ancContent.trim(), isUrgent)
                                    showNewAnnouncementDialog = false
                                    feedbackNotice = "Annonce publiée avec succès pour tous les membres !"
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingItemRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = TextLight, fontSize = 13.sp)
        Text(value, color = ElectricBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}
