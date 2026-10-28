package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.localization.NuevaStrings
import com.example.model.AppLanguage
import com.example.model.PaymentProvider
import com.example.model.Status
import com.example.model.UserRole
import com.example.ui.theme.*

@Composable
fun FuturisticCard(
    modifier: Modifier = Modifier,
    borderGlow: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = MidnightCard),
        border = if (borderGlow) {
            BorderStroke(
                1.dp,
                Brush.horizontalGradient(
                    listOf(
                        ElectricBlue.copy(alpha = 0.4f),
                        CyberBlue.copy(alpha = 0.1f),
                        NeonCyan.copy(alpha = 0.3f)
                    )
                )
            )
        } else {
            BorderStroke(1.dp, MidnightBorder)
        },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
fun ElectricButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    testTag: String = "electric_button"
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .minimumInteractiveComponentSize()
            .testTag(testTag),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ElectricBlue,
            contentColor = MidnightBase,
            disabledContainerColor = MidnightBorder,
            disabledContentColor = TextMuted
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
fun StatusChip(status: Status, modifier: Modifier = Modifier) {
    val (bgColor, textColor, label) = when (status) {
        Status.PENDING -> Triple(WarningAmber.copy(alpha = 0.2f), WarningAmber, "En Attente / Pending")
        Status.APPROVED, Status.COMPLETED -> Triple(SuccessGreen.copy(alpha = 0.2f), SuccessGreen, "Approuvé / Approved")
        Status.REJECTED -> Triple(ErrorRed.copy(alpha = 0.2f), ErrorRed, "Rejeté / Rejected")
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = bgColor,
        border = BorderStroke(1.dp, textColor.copy(alpha = 0.5f))
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun TestModeNotice(lang: AppLanguage) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        color = CyberBlue.copy(alpha = 0.15f),
        border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Science,
                contentDescription = null,
                tint = ElectricBlue,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (lang == AppLanguage.FR)
                    "Mode Test Cameroun Activé : Vous pouvez simuler les références Orange Money (#150#) & MTN MoMo (*126#) sans débit réel."
                else
                    "Cameroon Test Mode Active: Simulate Orange Money (#150#) & MTN MoMo (*126#) transaction references without actual charges.",
                color = TextLight,
                fontSize = 11.sp,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
fun PaymentModal(
    purposeTitle: String,
    amount: Long,
    lang: AppLanguage,
    onDismiss: () -> Unit,
    onSubmit: (provider: PaymentProvider, ref: String, note: String) -> Unit
) {
    var selectedProvider by remember { mutableStateOf(PaymentProvider.ORANGE_MONEY) }
    var transactionRef by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("+237 6") }
    var userNote by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MidnightSurface),
            border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (lang == AppLanguage.FR) "Paiement Sécurisé" else "Secure Payment",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ElectricBlue
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Fermer", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = purposeTitle,
                    color = TextWhite,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "$amount FCFA",
                    color = ElectricBlue,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(vertical = 6.dp)
                )

                TestModeNotice(lang = lang)

                Spacer(modifier = Modifier.height(12.dp))

                // Provider selector tabs
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Orange Money
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedProvider = PaymentProvider.ORANGE_MONEY },
                        shape = RoundedCornerShape(10.dp),
                        color = if (selectedProvider == PaymentProvider.ORANGE_MONEY) OrangeMoney.copy(alpha = 0.25f) else MidnightCard,
                        border = BorderStroke(
                            1.5.dp,
                            if (selectedProvider == PaymentProvider.ORANGE_MONEY) OrangeMoney else MidnightBorder
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Orange Money",
                                color = if (selectedProvider == PaymentProvider.ORANGE_MONEY) OrangeMoney else TextLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text("#150*1*1#", color = TextMuted, fontSize = 10.sp)
                        }
                    }

                    // MTN MoMo
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedProvider = PaymentProvider.MTN_MOMO },
                        shape = RoundedCornerShape(10.dp),
                        color = if (selectedProvider == PaymentProvider.MTN_MOMO) MtnMoMo.copy(alpha = 0.25f) else MidnightCard,
                        border = BorderStroke(
                            1.5.dp,
                            if (selectedProvider == PaymentProvider.MTN_MOMO) MtnMoMo else MidnightBorder
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "MTN MoMo",
                                color = if (selectedProvider == PaymentProvider.MTN_MOMO) MtnMoMo else TextLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text("*126*1*1#", color = TextMuted, fontSize = 10.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Official USSD Guidance
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = MidnightCardElevated
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = if (selectedProvider == PaymentProvider.ORANGE_MONEY)
                                "Procédure USSD Orange Money :\n1. Composez #150*1*1*699001122*$amount#\n2. Validez votre code secret PIN\n3. Copiez la référence reçue par SMS."
                            else
                                "Procédure USSD MTN MoMo :\n1. Composez *126*1*1*677334455*$amount#\n2. Entrez votre code PIN secret\n3. Copiez la référence ID reçue par SMS.",
                            color = TextLight,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = transactionRef,
                    onValueChange = { transactionRef = it; errorMessage = null },
                    label = { Text(if (lang == AppLanguage.FR) "ID Transaction / Réf SMS" else "Transaction ID / SMS Ref") },
                    placeholder = { Text("Ex: OM-20261025-992") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("tx_ref_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = MidnightBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                // Quick Simulation Helper
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = if (lang == AppLanguage.FR) "[Générer Réf Test]" else "[Generate Test Ref]",
                        color = ElectricBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable {
                                val prefix = if (selectedProvider == PaymentProvider.ORANGE_MONEY) "OM-TEST" else "MTN-TEST"
                                transactionRef = "$prefix-${(100000..999999).random()}"
                            }
                            .padding(4.dp)
                    )
                }

                // Security disclaimer banner
                Text(
                    text = if (lang == AppLanguage.FR)
                        "⚠️ Règle de sécurité : L'adhésion ou l'accès est activé après vérification. Aucune validation n'est faite sur simple capture d'écran."
                    else
                        "⚠️ Security rule: Access is activated only upon payment verification. No approval is granted solely on screenshots.",
                    color = WarningAmber,
                    fontSize = 10.sp,
                    lineHeight = 13.sp,
                    modifier = Modifier.padding(vertical = 6.dp)
                )

                if (errorMessage != null) {
                    Text(errorMessage!!, color = ErrorRed, fontSize = 12.sp, modifier = Modifier.padding(bottom = 6.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextLight)
                    ) {
                        Text(NuevaStrings.cancel(lang))
                    }

                    ElectricButton(
                        text = NuevaStrings.confirm(lang),
                        onClick = {
                            if (transactionRef.isBlank()) {
                                errorMessage = if (lang == AppLanguage.FR) "Veuillez renseigner la référence SMS de la transaction." else "Please enter the SMS transaction reference."
                            } else {
                                onSubmit(selectedProvider, transactionRef.trim(), userNote)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        testTag = "submit_payment_button"
                    )
                }
            }
        }
    }
}

@Composable
fun WithdrawalModal(
    availableBalance: Long,
    minWithdrawal: Long,
    lang: AppLanguage,
    onDismiss: () -> Unit,
    onSubmit: (amount: Long, provider: PaymentProvider, phone: String) -> Unit
) {
    var amountStr by remember { mutableStateOf(minWithdrawal.toString()) }
    var selectedProvider by remember { mutableStateOf(PaymentProvider.ORANGE_MONEY) }
    var phone by remember { mutableStateOf("+237 690 12 34 56") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MidnightSurface),
            border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (lang == AppLanguage.FR) "Demande de Retrait" else "Withdrawal Request",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ElectricBlue
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Fermer", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (lang == AppLanguage.FR) "Solde disponible : $availableBalance FCFA" else "Available balance: $availableBalance FCFA",
                    color = TextLight,
                    fontSize = 13.sp
                )

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = WarningAmber.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, WarningAmber.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = NuevaStrings.minWithdrawalNotice(minWithdrawal, lang),
                        color = WarningAmber,
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                // Provider Choice
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedProvider = PaymentProvider.ORANGE_MONEY },
                        shape = RoundedCornerShape(10.dp),
                        color = if (selectedProvider == PaymentProvider.ORANGE_MONEY) OrangeMoney.copy(alpha = 0.25f) else MidnightCard,
                        border = BorderStroke(1.dp, if (selectedProvider == PaymentProvider.ORANGE_MONEY) OrangeMoney else MidnightBorder)
                    ) {
                        Text(
                            "Orange Money",
                            modifier = Modifier.padding(10.dp),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedProvider == PaymentProvider.ORANGE_MONEY) OrangeMoney else TextLight,
                            fontSize = 12.sp
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedProvider = PaymentProvider.MTN_MOMO },
                        shape = RoundedCornerShape(10.dp),
                        color = if (selectedProvider == PaymentProvider.MTN_MOMO) MtnMoMo.copy(alpha = 0.25f) else MidnightCard,
                        border = BorderStroke(1.dp, if (selectedProvider == PaymentProvider.MTN_MOMO) MtnMoMo else MidnightBorder)
                    ) {
                        Text(
                            "MTN MoMo",
                            modifier = Modifier.padding(10.dp),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedProvider == PaymentProvider.MTN_MOMO) MtnMoMo else TextLight,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it; errorMessage = null },
                    label = { Text(if (lang == AppLanguage.FR) "Montant (FCFA)" else "Amount (FCFA)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("withdrawal_amount_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = MidnightBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it; errorMessage = null },
                    label = { Text(if (lang == AppLanguage.FR) "Numéro Bénéficiaire" else "Recipient Phone") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = MidnightBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                if (errorMessage != null) {
                    Text(errorMessage!!, color = ErrorRed, fontSize = 12.sp, modifier = Modifier.padding(vertical = 4.dp))
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text(NuevaStrings.cancel(lang))
                    }
                    ElectricButton(
                        text = NuevaStrings.confirm(lang),
                        onClick = {
                            val parsed = amountStr.toLongOrNull()
                            if (parsed == null || parsed < minWithdrawal) {
                                errorMessage = if (lang == AppLanguage.FR) "Le montant minimum est de $minWithdrawal FCFA." else "Minimum amount is $minWithdrawal FCFA."
                            } else if (parsed > availableBalance) {
                                errorMessage = if (lang == AppLanguage.FR) "Solde disponible insuffisant." else "Insufficient available balance."
                            } else if (phone.length < 9) {
                                errorMessage = if (lang == AppLanguage.FR) "Numéro de téléphone invalide." else "Invalid phone number."
                            } else {
                                onSubmit(parsed, selectedProvider, phone)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        testTag = "submit_withdrawal_button"
                    )
                }
            }
        }
    }
}
