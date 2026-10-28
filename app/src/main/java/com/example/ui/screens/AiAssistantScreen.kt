package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.example.data.NuevaAiService
import com.example.data.NuevaRepository
import com.example.localization.NuevaStrings
import com.example.model.AppLanguage
import com.example.model.ChatMessage
import com.example.ui.components.FuturisticCard
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Composable
fun AiAssistantScreen(
    repository: NuevaRepository,
    aiService: NuevaAiService
) {
    val lang by repository.currentLanguage.collectAsState()
    val chatMessages by repository.chatMessages.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var inputText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val quickPrompts = if (lang == AppLanguage.FR) {
        listOf(
            "Comment intégrer Orange Money & MTN MoMo ?",
            "3 micro-startups rentables avec 100 000 FCFA",
            "Structure d'un pitch d'ascenseur pour investisseurs",
            "Stratégie de vente WhatsApp Business"
        )
    } else {
        listOf(
            "How to integrate Orange Money & MTN MoMo?",
            "3 profitable startup ideas under 100,000 FCFA",
            "Structure of an elevator pitch for investors",
            "WhatsApp Business sales growth strategies"
        )
    }

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("ai_assistant_screen")
    ) {
        // Top Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MidnightSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MidnightBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = ElectricBlue.copy(alpha = 0.2f),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.SmartToy,
                            contentDescription = null,
                            tint = ElectricBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = NuevaStrings.aiAdvisorTitle(lang),
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Google Gemini 3.5 Flash • Context: Africa Tech",
                        color = NeonCyan,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(chatMessages, key = { it.id }) { msg ->
                ChatBubble(message = msg)
            }

            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MidnightCardElevated,
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = ElectricBlue,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (lang == AppLanguage.FR) "L'IA analyse votre question..." else "AI analyzing your request...",
                                    color = TextLight,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick prompts suggestions
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickPrompts) { prompt ->
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(enabled = !isLoading) {
                            inputText = prompt
                        },
                    color = MidnightCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberBlue.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = prompt,
                        color = TextLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Input Field and Send Button
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MidnightSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MidnightBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text(NuevaStrings.askAiPlaceholder(lang), fontSize = 13.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_input_field"),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = MidnightBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        val textToSend = inputText.trim()
                        if (textToSend.isNotBlank() && !isLoading) {
                            val userMsg = ChatMessage(
                                id = "usr_${UUID.randomUUID()}",
                                text = textToSend,
                                isUser = true,
                                timestamp = SimpleDateFormat("HH:mm", Locale.FRANCE).format(Date())
                            )
                            repository.addChatMessage(userMsg)
                            inputText = ""
                            isLoading = true

                            coroutineScope.launch {
                                val reply = aiService.generateAdvice(textToSend, lang)
                                val aiMsg = ChatMessage(
                                    id = "ai_${UUID.randomUUID()}",
                                    text = reply,
                                    isUser = false,
                                    timestamp = SimpleDateFormat("HH:mm", Locale.FRANCE).format(Date())
                                )
                                repository.addChatMessage(aiMsg)
                                isLoading = false
                            }
                        }
                    },
                    enabled = inputText.isNotBlank() && !isLoading,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            if (inputText.isNotBlank() && !isLoading) ElectricBlue else MidnightBorder,
                            CircleShape
                        )
                        .testTag("send_ai_button")
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Envoyer",
                        tint = if (inputText.isNotBlank() && !isLoading) MidnightBase else TextMuted
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.isUser
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Surface(
                shape = CircleShape,
                color = ElectricBlue.copy(alpha = 0.2f),
                modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.Bottom)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.SmartToy, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            modifier = Modifier.widthIn(max = 300.dp),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            color = if (isUser) CyberBlue else MidnightCardElevated,
            border = if (!isUser) androidx.compose.foundation.BorderStroke(1.dp, MidnightBorder) else null
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.text,
                    color = TextWhite,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
                Text(
                    text = message.timestamp,
                    color = if (isUser) TextLight.copy(alpha = 0.7f) else TextMuted,
                    fontSize = 10.sp,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 4.dp)
                )
            }
        }
    }
}
