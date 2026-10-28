package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NuevaRepository
import com.example.localization.NuevaStrings
import com.example.model.AppLanguage
import com.example.model.UserRole
import com.example.ui.components.ElectricButton
import com.example.ui.components.FuturisticCard
import com.example.ui.components.PaymentModal
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileAndHelpScreen(
    repository: NuevaRepository,
    onBack: () -> Unit
) {
    val lang by repository.currentLanguage.collectAsState()
    val user by repository.currentUser.collectAsState()
    val settings by repository.settings.collectAsState()

    var showPaymentModal by remember { mutableStateOf(false) }
    var alertNotice by remember { mutableStateOf<String?>(null) }

    var termsExpanded by remember { mutableStateOf(false) }
    var privacyExpanded by remember { mutableStateOf(false) }
    var helpExpanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MidnightBase,
        topBar = {
            TopAppBar(
                title = { Text(NuevaStrings.profile(lang), color = TextWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = ElectricBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MidnightSurface)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("profile_and_help_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Card
            item {
                FuturisticCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = ElectricBlue.copy(alpha = 0.2f),
                            modifier = Modifier.size(56.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = user.fullName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.joinToString(""),
                                    color = ElectricBlue,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(user.fullName, fontWeight = FontWeight.ExtraBold, color = TextWhite, fontSize = 16.sp)
                            Text(user.email, color = TextLight, fontSize = 12.sp)
                            Text(user.phone, color = TextMuted, fontSize = 11.sp)
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (user.isMembershipActive) SuccessGreen.copy(alpha = 0.2f) else WarningAmber.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = if (user.isMembershipActive) (if (lang == AppLanguage.FR) "ACTIF" else "ACTIVE") else (if (lang == AppLanguage.FR) "1 000 F EN ATTENTE" else "1,000 F PENDING"),
                                color = if (user.isMembershipActive) SuccessGreen else WarningAmber,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = user.bio,
                        color = TextLight,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (!user.isMembershipActive) {
                        ElectricButton(
                            text = NuevaStrings.activateMembership(lang),
                            onClick = { showPaymentModal = true },
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "profile_activate_membership_btn"
                        )
                    }
                }
            }

            // Dedicated Administrator Account Card
            item {
                FuturisticCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = CircleShape,
                                color = if (user.role == UserRole.ADMIN) SuccessGreen.copy(alpha = 0.2f) else CyberBlue.copy(alpha = 0.2f),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.AdminPanelSettings,
                                        contentDescription = null,
                                        tint = if (user.role == UserRole.ADMIN) SuccessGreen else ElectricBlue,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (lang == AppLanguage.FR) "Compte Administrateur" else "Administrator Account",
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = if (user.role == UserRole.ADMIN)
                                        (if (lang == AppLanguage.FR) "Connecté en Super-Administrateur" else "Connected as Super-Admin")
                                    else
                                        (if (lang == AppLanguage.FR) "Accès réservé à la gestion de plateforme" else "Restricted platform management access"),
                                    color = if (user.role == UserRole.ADMIN) SuccessGreen else TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (user.role == UserRole.ADMIN) SuccessGreen.copy(alpha = 0.2f) else MidnightCard,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (user.role == UserRole.ADMIN) SuccessGreen else ElectricBlue.copy(alpha = 0.5f)),
                            modifier = Modifier.clickable {
                                if (user.role == UserRole.ADMIN) {
                                    repository.adminLogoutToMember()
                                    alertNotice = if (lang == AppLanguage.FR) "Mode Administrateur désactivé." else "Administrator mode disabled."
                                } else {
                                    repository.adminLoginWithPin("2026")
                                    alertNotice = if (lang == AppLanguage.FR) "Connecté au Compte Administrateur avec succès !" else "Connected to Administrator Account successfully!"
                                }
                            }
                        ) {
                            Text(
                                text = if (user.role == UserRole.ADMIN)
                                    (if (lang == AppLanguage.FR) "Se Déconnecter" else "Log Out")
                                else
                                    (if (lang == AppLanguage.FR) "Connexion Admin" else "Admin Login"),
                                color = if (user.role == UserRole.ADMIN) WarningAmber else ElectricBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (lang == AppLanguage.FR)
                            "Gérez les validations de paiements (1 000 FCFA), les approbations de retraits MoMo (min 1 100 FCFA), la modération de produits et les utilisateurs de NUEVA TECHNOLOGY."
                        else
                            "Manage payment verifications (1,000 FCFA), MoMo withdrawal approvals (min 1,100 FCFA), marketplace listings, and users of NUEVA TECHNOLOGY.",
                        color = TextLight,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }

            if (alertNotice != null) {
                item {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SuccessGreen.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(alertNotice!!, color = TextWhite, fontSize = 12.sp, modifier = Modifier.weight(1f))
                            IconButton(onClick = { alertNotice = null }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = TextMuted)
                            }
                        }
                    }
                }
            }

            // Language & Role Preferences
            item {
                FuturisticCard(borderGlow = false) {
                    Text(
                        text = if (lang == AppLanguage.FR) "Préférences & Contrôles" else "Preferences & Controls",
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Language toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(if (lang == AppLanguage.FR) "Langue de l'application" else "Application Language", color = TextLight, fontSize = 13.sp)

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                modifier = Modifier.clickable { repository.setLanguage(AppLanguage.FR) },
                                shape = RoundedCornerShape(8.dp),
                                color = if (lang == AppLanguage.FR) ElectricBlue else MidnightSurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (lang == AppLanguage.FR) ElectricBlue else MidnightBorder)
                            ) {
                                Text(
                                    "Français",
                                    color = if (lang == AppLanguage.FR) MidnightBase else TextLight,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }

                            Surface(
                                modifier = Modifier.clickable { repository.setLanguage(AppLanguage.EN) },
                                shape = RoundedCornerShape(8.dp),
                                color = if (lang == AppLanguage.EN) ElectricBlue else MidnightSurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (lang == AppLanguage.EN) ElectricBlue else MidnightBorder)
                            ) {
                                Text(
                                    "English",
                                    color = if (lang == AppLanguage.EN) MidnightBase else TextLight,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Role switch
                    Text(
                        text = if (lang == AppLanguage.FR) "Rôle Actif (Pour tester l'administration)" else "Active Role (Test Administrator View)",
                        color = TextLight,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        UserRole.entries.forEach { role ->
                            val isCurrent = user.role == role
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { repository.switchUserRole(role) },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isCurrent) CyberBlue else MidnightSurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isCurrent) ElectricBlue else MidnightBorder)
                            ) {
                                Text(
                                    text = role.name.take(5),
                                    color = TextWhite,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Legal & Help Accordions
            item {
                Text(
                    text = if (lang == AppLanguage.FR) "Assistance & Informations Légales" else "Support & Legal Information",
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    fontSize = 14.sp
                )
            }

            // FAQ / Help
            item {
                FuturisticCard(
                    modifier = Modifier.clickable { helpExpanded = !helpExpanded },
                    borderGlow = false
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(NuevaStrings.help(lang), fontWeight = FontWeight.Bold, color = TextWhite, fontSize = 13.sp)
                        Icon(
                            if (helpExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = ElectricBlue
                        )
                    }

                    AnimatedVisibility(visible = helpExpanded) {
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            Text(
                                text = if (lang == AppLanguage.FR)
                                    "Q: Comment activer mon compte ?\nR: Effectuez un paiement de 1 000 FCFA via Orange Money (#150#) ou MTN MoMo (*126#) et soumettez la référence SMS.\n\nQ: Quand puis-je demander un retrait ?\nR: Dès que votre solde atteint ${settings.minWithdrawal} FCFA. Un administrateur valide le virement sous 24h ouvrées."
                                else
                                    "Q: How to activate membership?\nA: Pay 1,000 FCFA registration fee via Orange Money (#150#) or MTN MoMo (*126#) and submit your SMS reference.\n\nQ: When can I withdraw?\nA: Once your balance hits ${settings.minWithdrawal} FCFA. An administrator approves the disbursement within 24 business hours.",
                                color = TextLight,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Terms of Service
            item {
                FuturisticCard(
                    modifier = Modifier.clickable { termsExpanded = !termsExpanded },
                    borderGlow = false
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(NuevaStrings.terms(lang), fontWeight = FontWeight.Bold, color = TextWhite, fontSize = 13.sp)
                        Icon(
                            if (termsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = ElectricBlue
                        )
                    }

                    AnimatedVisibility(visible = termsExpanded) {
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            Text(
                                text = if (lang == AppLanguage.FR)
                                    "1. Objet : NUEVA TECHNOLOGY fournit un écosystème d'apprentissage, de marketplace et de mise en relation d'affaires.\n2. Frais : Adhésion unique fixée à 1 000 FCFA. Les commissions de parrainage sont fixées à 100 FCFA par filleul éligible vérifié.\n3. Sécurité : Tout abus, tentative de soumission de fausse capture ou blanchiment entraîne le gel immédiat du compte."
                                else
                                    "1. Purpose: NUEVA TECHNOLOGY provides an educational ecosystem, digital marketplace, and business network.\n2. Fees: One-time 1,000 FCFA registration fee. Referral commission is 100 FCFA per verified eligible member.\n3. Security: Any fraudulent submission, fake screenshots, or abuse results in immediate account suspension.",
                                color = TextLight,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Privacy Policy
            item {
                FuturisticCard(
                    modifier = Modifier.clickable { privacyExpanded = !privacyExpanded },
                    borderGlow = false
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(NuevaStrings.privacy(lang), fontWeight = FontWeight.Bold, color = TextWhite, fontSize = 13.sp)
                        Icon(
                            if (privacyExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = ElectricBlue
                        )
                    }

                    AnimatedVisibility(visible = privacyExpanded) {
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            Text(
                                text = if (lang == AppLanguage.FR)
                                    "NUEVA TECHNOLOGY respecte votre vie privée. Vos numéros Orange Money et MTN MoMo ne sont utilisés que pour le traitement des paiements et des retraits autorisés. Aucune donnée n'est cédée à des tiers à des fins publicitaires."
                                else
                                    "NUEVA TECHNOLOGY respects your privacy. Your Orange Money and MTN MoMo numbers are utilized solely for verified payment processing and disbursements. No data is transferred to third parties for advertising.",
                                color = TextLight,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }

    if (showPaymentModal) {
        PaymentModal(
            purposeTitle = if (lang == AppLanguage.FR) "Frais d'Adhésion NUEVA TECHNOLOGY" else "NUEVA TECHNOLOGY Membership Fee",
            amount = settings.registrationFee,
            lang = lang,
            onDismiss = { showPaymentModal = false },
            onSubmit = { provider, ref, note ->
                repository.submitMembershipPayment(provider, ref, note)
                showPaymentModal = false
                alertNotice = if (lang == AppLanguage.FR)
                    "Demande d'adhésion enregistrée (Réf: $ref). Un administrateur validera sous peu."
                else
                    "Membership request recorded (Ref: $ref). An administrator will verify shortly."
            }
        )
    }
}
