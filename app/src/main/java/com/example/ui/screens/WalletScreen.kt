package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.data.NuevaRepository
import com.example.localization.NuevaStrings
import com.example.model.AppLanguage
import com.example.model.PaymentProvider
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun WalletScreen(
    repository: NuevaRepository
) {
    val lang by repository.currentLanguage.collectAsState()
    val balance by repository.walletBalance.collectAsState()
    val pendingBal by repository.pendingBalance.collectAsState()
    val transactions by repository.transactions.collectAsState()
    val settings by repository.settings.collectAsState()
    val user by repository.currentUser.collectAsState()

    var showDepositModal by remember { mutableStateOf(false) }
    var showWithdrawalModal by remember { mutableStateOf(false) }
    var alertMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("wallet_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = if (lang == AppLanguage.FR) "Portefeuille & Transactions" else "Wallet & Transactions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = TextWhite
            )
            Text(
                text = if (lang == AppLanguage.FR)
                    "Gérez vos gains, dépôts Orange/MTN MoMo et demandes de retraits validées par l'administration."
                else
                    "Manage your earnings, Orange/MTN MoMo deposits, and admin-verified withdrawal requests.",
                color = TextLight,
                fontSize = 13.sp
            )
        }

        // Main Balance Card
        item {
            FuturisticCard {
                Text(
                    text = NuevaStrings.balance(lang),
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "$balance FCFA",
                    color = ElectricBlue,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                if (pendingBal > 0) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = WarningAmber.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, WarningAmber.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.HourglassBottom, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${NuevaStrings.pendingBalance(lang)} : $pendingBal FCFA",
                                color = WarningAmber,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ElectricButton(
                        text = if (lang == AppLanguage.FR) "Dépôt Mobile Money" else "Mobile Money Deposit",
                        onClick = { showDepositModal = true },
                        icon = Icons.Default.AddCard,
                        modifier = Modifier.weight(1f),
                        testTag = "wallet_deposit_btn"
                    )

                    OutlinedButton(
                        onClick = { showWithdrawalModal = true },
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .testTag("wallet_withdraw_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MidnightBorder)
                    ) {
                        Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (lang == AppLanguage.FR) "Demander Retrait" else "Withdraw",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        if (alertMessage != null) {
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
                        Text(alertMessage!!, color = TextWhite, fontSize = 12.sp, modifier = Modifier.weight(1f))
                        IconButton(onClick = { alertMessage = null }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = TextMuted)
                        }
                    }
                }
            }
        }

        // Cameroon MoMo Quick Guide Card
        item {
            FuturisticCard(borderGlow = false) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (lang == AppLanguage.FR) "Canaux de Paiement Mobile Money Officiels" else "Official Cameroon Mobile Money Channels",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        color = OrangeMoney.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, OrangeMoney.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Orange Money", color = OrangeMoney, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Code direct : #150#", color = TextLight, fontSize = 11.sp)
                            Text("Marchand : ${settings.orangeMoneyMerchant}", color = TextMuted, fontSize = 10.sp)
                        }
                    }

                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        color = MtnMoMo.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MtnMoMo.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("MTN Mobile Money", color = MtnMoMo, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Code direct : *126#", color = TextLight, fontSize = 11.sp)
                            Text("Marchand : ${settings.mtnMoMoMerchant}", color = TextMuted, fontSize = 10.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (lang == AppLanguage.FR)
                        "• Seuil minimum de retrait : ${settings.minWithdrawal} FCFA.\n• Tout retrait est soumis à l'approbation préalable d'un administrateur.\n• Aucun compte n'est activé ni crédité sur simple capture d'écran."
                    else
                        "• Minimum withdrawal threshold: ${settings.minWithdrawal} FCFA.\n• All withdrawals require prior administrator approval.\n• No account is credited or activated solely on screenshots.",
                    color = TextLight,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }

        // Transactions History Header
        item {
            Text(
                text = if (lang == AppLanguage.FR) "Historique Complet des Opérations" else "Full Transaction Ledger",
                color = TextWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        if (transactions.isEmpty()) {
            item {
                FuturisticCard {
                    Text(
                        text = if (lang == AppLanguage.FR) "Aucune transaction enregistrée pour le moment." else "No transactions recorded yet.",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                }
            }
        }

        items(transactions, key = { it.id }) { tx ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MidnightCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, MidnightBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = tx.note,
                            color = TextWhite,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "${tx.date} • Réf: ${tx.reference}",
                            color = TextMuted,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${tx.amount} FCFA",
                            color = ElectricBlue,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        StatusChip(status = tx.status)
                    }
                }
            }
        }
    }

    // Deposit Modal
    if (showDepositModal) {
        PaymentModal(
            purposeTitle = if (lang == AppLanguage.FR) "Recharger le Portefeuille NUEVA" else "Top Up NUEVA Wallet",
            amount = 2000L,
            lang = lang,
            onDismiss = { showDepositModal = false },
            onSubmit = { provider, ref, _ ->
                repository.depositFunds(2000L, provider, ref)
                showDepositModal = false
                alertMessage = if (lang == AppLanguage.FR)
                    "Dépôt de 2 000 FCFA crédité avec succès (Réf: $ref) !"
                else
                    "2,000 FCFA deposit credited successfully (Ref: $ref)!"
            }
        )
    }

    // Withdrawal Modal
    if (showWithdrawalModal) {
        WithdrawalModal(
            availableBalance = balance,
            minWithdrawal = settings.minWithdrawal,
            lang = lang,
            onDismiss = { showWithdrawalModal = false },
            onSubmit = { amount, provider, phone ->
                val result = repository.requestWithdrawal(amount, provider, phone)
                showWithdrawalModal = false
                alertMessage = result.getOrNull() ?: result.exceptionOrNull()?.message
            }
        )
    }
}
