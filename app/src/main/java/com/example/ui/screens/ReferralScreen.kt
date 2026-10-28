package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NuevaRepository
import com.example.localization.NuevaStrings
import com.example.model.AppLanguage
import com.example.ui.components.ElectricButton
import com.example.ui.components.FuturisticCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferralScreen(
    repository: NuevaRepository,
    onBack: () -> Unit
) {
    val lang by repository.currentLanguage.collectAsState()
    val user by repository.currentUser.collectAsState()
    val settings by repository.settings.collectAsState()
    val referrals by repository.referrals.collectAsState()
    val context = LocalContext.current

    val verifiedCount = referrals.count { it.isVerified }
    val totalBonusEarned = verifiedCount * settings.referralReward

    Scaffold(
        containerColor = MidnightBase,
        topBar = {
            TopAppBar(
                title = { Text(NuevaStrings.referral(lang), color = TextWhite, fontWeight = FontWeight.Bold) },
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
                .testTag("referral_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = if (lang == AppLanguage.FR) "Programme d'Affiliation & Parrainage" else "Affiliate & Referral Program",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextWhite
                )
                Text(
                    text = if (lang == AppLanguage.FR)
                        "Partagez votre code et recevez ${settings.referralReward} FCFA pour chaque filleul vérifié après son adhésion."
                    else
                        "Share your invite code and earn ${settings.referralReward} FCFA for every eligible verified member.",
                    color = TextLight,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Referral Code Card
            item {
                FuturisticCard {
                    Text(
                        text = if (lang == AppLanguage.FR) "Votre Code de Parrainage Unique" else "Your Unique Referral Code",
                        color = TextMuted,
                        fontSize = 12.sp
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = user.referralCode,
                            color = ElectricBlue,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Referral Code", user.referralCode)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(
                                    context,
                                    if (lang == AppLanguage.FR) "Code copié dans le presse-papiers !" else "Code copied to clipboard!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier
                                .background(MidnightBorder, CircleShape)
                                .size(40.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copier", tint = ElectricBlue)
                        }
                    }

                    Text(
                        text = if (lang == AppLanguage.FR)
                            "Lien de partage : https://nueva.africa/join?ref=${user.referralCode}"
                        else
                            "Share link: https://nueva.africa/join?ref=${user.referralCode}",
                        color = NeonCyan,
                        fontSize = 11.sp
                    )
                }
            }

            // Stats Cards
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        color = MidnightCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MidnightBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(if (lang == AppLanguage.FR) "Filleuls Inscrits" else "Total Referrals", color = TextMuted, fontSize = 11.sp)
                            Text("${referrals.size}", color = TextWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        color = MidnightCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MidnightBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(if (lang == AppLanguage.FR) "Filleuls Vérifiés" else "Verified Members", color = TextMuted, fontSize = 11.sp)
                            Text("$verifiedCount", color = SuccessGreen, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        color = MidnightCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MidnightBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(if (lang == AppLanguage.FR) "Gains Générés" else "Total Bonus", color = TextMuted, fontSize = 11.sp)
                            Text("$totalBonusEarned F", color = ElectricBlue, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // List of Referrals
            item {
                Text(
                    text = if (lang == AppLanguage.FR) "Liste de Vos Filleuls" else "Your Referrals List",
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    fontSize = 15.sp
                )
            }

            items(referrals, key = { it.id }) { ref ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MidnightCard
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(ref.refereeName, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("${ref.refereePhone} • Inscription le ${ref.date}", color = TextMuted, fontSize = 11.sp)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (ref.isVerified) SuccessGreen.copy(alpha = 0.2f) else WarningAmber.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = if (ref.isVerified)
                                        (if (lang == AppLanguage.FR) "VÉRIFIÉ (+${settings.referralReward} F)" else "VERIFIED (+${settings.referralReward} F)")
                                    else
                                        (if (lang == AppLanguage.FR) "EN ATTENTE D'ADHÉSION" else "PENDING ACTIVATION"),
                                    color = if (ref.isVerified) SuccessGreen else WarningAmber,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
