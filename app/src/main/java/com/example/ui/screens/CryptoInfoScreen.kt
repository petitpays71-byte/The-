package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ui.components.ElectricButton
import com.example.ui.components.FuturisticCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptoInfoScreen(
    repository: NuevaRepository,
    onBack: () -> Unit
) {
    val lang by repository.currentLanguage.collectAsState()

    var quizAnswerIndex by remember { mutableStateOf<Int?>(null) }
    var quizSubmitted by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MidnightBase,
        topBar = {
            TopAppBar(
                title = { Text(NuevaStrings.cryptoInfo(lang), color = TextWhite, fontWeight = FontWeight.Bold) },
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
                .testTag("crypto_info_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mandatory Disclaimer Card
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = WarningAmber.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, WarningAmber)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = NuevaStrings.cryptoDisclaimerTitle(lang),
                                color = WarningAmber,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = NuevaStrings.cryptoDisclaimer(lang),
                            color = TextLight,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            // Introduction to Web3 & African Economy
            item {
                FuturisticCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (lang == AppLanguage.FR) "1. Pourquoi la Blockchain intéresse l'Afrique ?" else "1. Why Blockchain Matters in Africa",
                            fontWeight = FontWeight.Bold,
                            color = TextWhite,
                            fontSize = 15.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (lang == AppLanguage.FR)
                            "• Réduction des frais de transfert transfrontaliers (actuellement 8 à 12% via les canaux traditionnels).\n• Traçabilité des filières agricoles (cacao, café, bois) via des registres immuables.\n• Protection contre la dévaluation et accès à des stablecoins adossés aux réserves internationales."
                        else
                            "• Slashing cross-border remittance costs (often 8-12% through legacy banking conduits).\n• Supply-chain traceability for agricultural exports (cocoa, coffee, timber).\n• Hedging against local currency depreciation through compliant digital reserve assets.",
                        color = TextLight,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            // Security & Scams Warning
            item {
                FuturisticCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (lang == AppLanguage.FR) "2. Sécurité : Pièges & Arnaques Courantes" else "2. Security: Avoiding Scams & Ponzi Schemes",
                            fontWeight = FontWeight.Bold,
                            color = TextWhite,
                            fontSize = 15.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (lang == AppLanguage.FR)
                            "⚠️ Règle absolue : Ne JAMAIS partager votre phrase de récupération (Seed Phrase de 12/24 mots).\n⚠️ Méfiez-vous des groupes Telegram/WhatsApp promettant des rendements garantis (ex: 'Doublez vos FCFA en 24h').\n⚠️ Aucun membre de NUEVA TECHNOLOGY ne vous demandera vos identifiants ou de faire un dépôt d'argent crypto."
                        else
                            "⚠️ Absolute Rule: NEVER reveal your secret recovery phrase (12/24-word seed phrase).\n⚠️ Beware of Telegram/WhatsApp channels advertising guaranteed yields (e.g., 'Double your FCFA in 24h').\n⚠️ No representative of NUEVA TECHNOLOGY will ever ask for credentials or private financial deposits.",
                        color = TextLight,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            // CEMAC / BEAC Regulation Module
            item {
                FuturisticCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Gavel, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (lang == AppLanguage.FR) "3. Cadre Réglementaire en Zone CEMAC" else "3. Regulatory Framework in CEMAC Region",
                            fontWeight = FontWeight.Bold,
                            color = TextWhite,
                            fontSize = 15.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (lang == AppLanguage.FR)
                            "La Banque des États de l'Afrique Centrale (BEAC) et la Commission Bancaire de l'Afrique Centrale (COBAC) interdisent aux établissements financiers d'effectuer des opérations spéculatives en cryptomonnaies. NUEVA TECHNOLOGY se conforme strictement à ces directives et n'opère aucun échange de devises virtuelles."
                        else
                            "The Bank of Central African States (BEAC) and COBAC prohibit banking institutions from speculative cryptocurrency transactions. NUEVA TECHNOLOGY strictly abides by these regional statutes and operates zero virtual currency exchange mechanisms.",
                        color = TextLight,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            // Interactive Knowledge Quiz
            item {
                FuturisticCard {
                    Text(
                        text = if (lang == AppLanguage.FR) "Quiz Éducatif Blockchain" else "Blockchain Educational Quiz",
                        fontWeight = FontWeight.Bold,
                        color = ElectricBlue,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (lang == AppLanguage.FR)
                            "Question : Que devez-vous faire si quelqu'un vous demande votre phrase secrète (Seed Phrase) pour 'vérifier votre compte' ?"
                        else
                            "Question: What should you do if someone asks for your secret seed phrase to 'verify your account'?",
                        color = TextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val options = if (lang == AppLanguage.FR) {
                        listOf(
                            "A) La lui donner s'il prétend être administrateur.",
                            "B) Refuser catégoriquement et le signaler comme tentative d'escroquerie.",
                            "C) Envoyer seulement les 6 premiers mots."
                        )
                    } else {
                        listOf(
                            "A) Send it if they claim to be a support agent.",
                            "B) Categorically refuse and report it as a phishing scam.",
                            "C) Send only the first 6 words."
                        )
                    }

                    options.forEachIndexed { index, opt ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    quizAnswerIndex = index
                                    quizSubmitted = true
                                },
                            shape = RoundedCornerShape(8.dp),
                            color = when {
                                quizSubmitted && index == 1 -> SuccessGreen.copy(alpha = 0.25f)
                                quizSubmitted && quizAnswerIndex == index && index != 1 -> ErrorRed.copy(alpha = 0.25f)
                                quizAnswerIndex == index -> CyberBlue.copy(alpha = 0.3f)
                                else -> MidnightSurface
                            },
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (quizSubmitted && index == 1) SuccessGreen else MidnightBorder
                            )
                        ) {
                            Text(
                                text = opt,
                                color = TextLight,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    if (quizSubmitted) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (quizAnswerIndex == 1)
                                (if (lang == AppLanguage.FR) "✅ Excellente réponse ! Une phrase secrète ne doit jamais être communiquée à qui que ce soit." else "✅ Correct! A secret recovery phrase must never be disclosed to anyone.")
                            else
                                (if (lang == AppLanguage.FR) "❌ Incorrect. La bonne réponse est B. Ne communiquez jamais vos clés secrètes." else "❌ Incorrect. The right answer is B. Never disclose secret keys."),
                            color = if (quizAnswerIndex == 1) SuccessGreen else ErrorRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
