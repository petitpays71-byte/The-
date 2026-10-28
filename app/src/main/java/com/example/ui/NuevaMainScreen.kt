package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NuevaAiService
import com.example.data.NuevaRepository
import com.example.localization.NuevaStrings
import com.example.model.AppLanguage
import com.example.model.UserRole
import com.example.ui.screens.*
import com.example.ui.theme.*

sealed class ScreenDestination {
    data class MainTab(val tabIndex: Int) : ScreenDestination()
    object Referral : ScreenDestination()
    object Crypto : ScreenDestination()
    object Admin : ScreenDestination()
    object Profile : ScreenDestination()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaMainScreen(
    repository: NuevaRepository,
    aiService: NuevaAiService
) {
    val lang by repository.currentLanguage.collectAsState()
    val user by repository.currentUser.collectAsState()
    val notifications by repository.adminNotifications.collectAsState()
    val withdrawalRequests by repository.withdrawalRequests.collectAsState()
    val paymentRequests by repository.paymentRequests.collectAsState()

    val pendingCount = (if (user.role == UserRole.ADMIN) {
        withdrawalRequests.count { it.status == com.example.model.Status.PENDING } +
                paymentRequests.count { it.status == com.example.model.Status.PENDING }
    } else {
        0
    })

    var currentScreen by remember { mutableStateOf<ScreenDestination>(ScreenDestination.MainTab(0)) }

    // BackHandler to navigate back to Home if in subscreen or other tab
    BackHandler(enabled = currentScreen !is ScreenDestination.MainTab || (currentScreen as ScreenDestination.MainTab).tabIndex != 0) {
        currentScreen = when (currentScreen) {
            is ScreenDestination.MainTab -> {
                val idx = (currentScreen as ScreenDestination.MainTab).tabIndex
                if (idx != 0) ScreenDestination.MainTab(0) else ScreenDestination.MainTab(0)
            }
            else -> ScreenDestination.MainTab(0)
        }
    }

    Scaffold(
        containerColor = MidnightBase,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { currentScreen = ScreenDestination.MainTab(0) }
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = ElectricBlue.copy(alpha = 0.2f),
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "N",
                                    color = ElectricBlue,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "NUEVA",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = TextWhite,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "TECHNOLOGY",
                                color = ElectricBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                letterSpacing = 1.5.sp
                            )
                        }
                    }
                },
                actions = {
                    // Language Switcher Chip
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { repository.toggleLanguage() }
                            .testTag("language_toggle_btn"),
                        color = MidnightCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MidnightBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Language,
                                contentDescription = null,
                                tint = ElectricBlue,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (lang == AppLanguage.FR) "FR" else "EN",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Admin Quick Switch Chip
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                if (user.role == UserRole.ADMIN) {
                                    repository.switchUserRole(UserRole.ENTREPRENEUR)
                                } else {
                                    repository.switchUserRole(UserRole.ADMIN)
                                }
                            }
                            .testTag("admin_role_toggle_chip"),
                        color = if (user.role == UserRole.ADMIN) SuccessGreen.copy(alpha = 0.2f) else MidnightCard,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (user.role == UserRole.ADMIN) SuccessGreen else MidnightBorder
                        )
                    ) {
                        Text(
                            text = if (user.role == UserRole.ADMIN) "ADMIN" else "USER",
                            color = if (user.role == UserRole.ADMIN) SuccessGreen else TextMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Admin Dashboard Shortcut (if admin) or Profile
                    IconButton(
                        onClick = { currentScreen = ScreenDestination.Admin },
                        modifier = Modifier.testTag("top_admin_btn")
                    ) {
                        BadgedBox(
                            badge = {
                                if (pendingCount > 0) {
                                    Badge(containerColor = WarningAmber) {
                                        Text("$pendingCount", color = MidnightBase, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.AdminPanelSettings,
                                contentDescription = "Admin Dashboard",
                                tint = if (user.role == UserRole.ADMIN) SuccessGreen else TextLight
                            )
                        }
                    }

                    // Profile button
                    IconButton(
                        onClick = { currentScreen = ScreenDestination.Profile },
                        modifier = Modifier.testTag("top_profile_btn")
                    ) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = "Mon Profil",
                            tint = ElectricBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MidnightSurface)
            )
        },
        bottomBar = {
            if (currentScreen is ScreenDestination.MainTab) {
                val currentTab = (currentScreen as ScreenDestination.MainTab).tabIndex
                NavigationBar(
                    containerColor = MidnightSurface,
                    contentColor = TextLight,
                    tonalElevation = 8.dp,
                    windowInsets = WindowInsets.navigationBars,
                    modifier = Modifier.testTag("main_bottom_nav")
                ) {
                    val navItems = listOf(
                        NavTabItem(
                            label = NuevaStrings.tabHome(lang),
                            selectedIcon = Icons.Filled.Home,
                            unselectedIcon = Icons.Outlined.Home,
                            tag = "nav_tab_home"
                        ),
                        NavTabItem(
                            label = NuevaStrings.tabCourses(lang),
                            selectedIcon = Icons.Filled.School,
                            unselectedIcon = Icons.Outlined.School,
                            tag = "nav_tab_courses"
                        ),
                        NavTabItem(
                            label = NuevaStrings.tabMarketplace(lang),
                            selectedIcon = Icons.Filled.Storefront,
                            unselectedIcon = Icons.Outlined.Storefront,
                            tag = "nav_tab_market"
                        ),
                        NavTabItem(
                            label = NuevaStrings.tabWallet(lang),
                            selectedIcon = Icons.Filled.AccountBalanceWallet,
                            unselectedIcon = Icons.Outlined.AccountBalanceWallet,
                            tag = "nav_tab_wallet"
                        ),
                        NavTabItem(
                            label = NuevaStrings.tabAi(lang),
                            selectedIcon = Icons.Filled.SmartToy,
                            unselectedIcon = Icons.Outlined.SmartToy,
                            tag = "nav_tab_ai"
                        )
                    )

                    navItems.forEachIndexed { index, item ->
                        val selected = currentTab == index
                        NavigationBarItem(
                            selected = selected,
                            onClick = { currentScreen = ScreenDestination.MainTab(index) },
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    fontSize = 10.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MidnightBase,
                                selectedTextColor = ElectricBlue,
                                indicatorColor = ElectricBlue,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            ),
                            modifier = Modifier.testTag(item.tag)
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val screen = currentScreen) {
                is ScreenDestination.MainTab -> {
                    when (screen.tabIndex) {
                        0 -> HomeScreen(
                            repository = repository,
                            onNavigateToTab = { currentScreen = ScreenDestination.MainTab(it) },
                            onOpenReferral = { currentScreen = ScreenDestination.Referral },
                            onOpenCrypto = { currentScreen = ScreenDestination.Crypto },
                            onOpenAdmin = { currentScreen = ScreenDestination.Admin }
                        )
                        1 -> CoursesScreen(
                            repository = repository,
                            onNavigateToWallet = { currentScreen = ScreenDestination.MainTab(3) }
                        )
                        2 -> MarketplaceScreen(
                            repository = repository
                        )
                        3 -> WalletScreen(
                            repository = repository
                        )
                        4 -> AiAssistantScreen(
                            repository = repository,
                            aiService = aiService
                        )
                    }
                }
                is ScreenDestination.Referral -> {
                    ReferralScreen(
                        repository = repository,
                        onBack = { currentScreen = ScreenDestination.MainTab(0) }
                    )
                }
                is ScreenDestination.Crypto -> {
                    CryptoInfoScreen(
                        repository = repository,
                        onBack = { currentScreen = ScreenDestination.MainTab(0) }
                    )
                }
                is ScreenDestination.Admin -> {
                    AdminDashboardScreen(
                        repository = repository,
                        onBack = { currentScreen = ScreenDestination.MainTab(0) }
                    )
                }
                is ScreenDestination.Profile -> {
                    ProfileAndHelpScreen(
                        repository = repository,
                        onBack = { currentScreen = ScreenDestination.MainTab(0) }
                    )
                }
            }
        }
    }
}

data class NavTabItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
)
