package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.ui.window.Dialog
import com.example.data.NuevaRepository
import com.example.localization.NuevaStrings
import com.example.model.AppLanguage
import com.example.model.Course
import com.example.model.Lesson
import com.example.ui.components.ElectricButton
import com.example.ui.components.FuturisticCard
import com.example.ui.theme.*

@Composable
fun CoursesScreen(
    repository: NuevaRepository,
    onNavigateToWallet: () -> Unit
) {
    val lang by repository.currentLanguage.collectAsState()
    val courses by repository.courses.collectAsState()
    val walletBalance by repository.walletBalance.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var viewingCourse by remember { mutableStateOf<Course?>(null) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    val tabs = if (lang == AppLanguage.FR) {
        listOf("Toutes les formations", "Conférences", "Mes Cours")
    } else {
        listOf("All Courses", "Conferences", "My Learning")
    }

    val filteredCourses = when (selectedTab) {
        1 -> courses.filter { it.isConference }
        2 -> courses.filter { it.isEnrolled }
        else -> courses
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("courses_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = if (lang == AppLanguage.FR) "Académie & Conférences NUEVA" else "NUEVA Academy & Conferences",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = TextWhite
            )
            Text(
                text = if (lang == AppLanguage.FR)
                    "Des programmes pratiques pour acquérir des compétences monétisables et booster vos projets."
                else
                    "Practical curricula to build monetizable skills and accelerate your venture.",
                color = TextLight,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Filter chips
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MidnightSurface,
                contentColor = ElectricBlue,
                edgePadding = 0.dp
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) ElectricBlue else TextMuted
                            )
                        }
                    )
                }
            }
        }

        if (feedbackMessage != null) {
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
                        Text(feedbackMessage!!, color = TextWhite, fontSize = 12.sp, modifier = Modifier.weight(1f))
                        IconButton(onClick = { feedbackMessage = null }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = TextMuted)
                        }
                    }
                }
            }
        }

        // Empty state
        if (filteredCourses.isEmpty()) {
            item {
                FuturisticCard {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.School, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (lang == AppLanguage.FR) "Aucun cours trouvé dans cette section" else "No courses found in this section",
                            color = TextLight,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        items(filteredCourses, key = { it.id }) { course ->
            CourseCard(
                course = course,
                lang = lang,
                onClick = { viewingCourse = course }
            )
        }
    }

    // Course Detail & Lessons Dialog
    if (viewingCourse != null) {
        val course = viewingCourse!!
        Dialog(onDismissRequest = { viewingCourse = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .padding(4.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
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
                                    text = if (course.isConference) "CONFÉRENCE DIRECT" else course.category,
                                    color = if (course.isConference) MtnMoMo else NeonCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (lang == AppLanguage.FR) course.titleFr else course.titleEn,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Text(
                                text = "Formateur : ${course.instructor}",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }

                        IconButton(onClick = { viewingCourse = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = TextMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (lang == AppLanguage.FR) course.descriptionFr else course.descriptionEn,
                        color = TextLight,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Enrollment Status & Actions
                    if (!course.isEnrolled) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = MidnightCardElevated,
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (course.price == 0L) (if (lang == AppLanguage.FR) "Accès Gratuit" else "Free Access") else "${course.price} FCFA",
                                        fontWeight = FontWeight.Bold,
                                        color = if (course.price == 0L) SuccessGreen else ElectricBlue,
                                        fontSize = 18.sp
                                    )
                                    ElectricButton(
                                        text = if (course.price == 0L) (if (lang == AppLanguage.FR) "S'inscrire" else "Enroll") else (if (lang == AppLanguage.FR) "Débloquer (${course.price} F)" else "Unlock (${course.price} F)"),
                                        onClick = {
                                            val result = repository.purchaseCourse(course.id)
                                            if (result.isSuccess) {
                                                viewingCourse = repository.courses.value.find { it.id == course.id }
                                                feedbackMessage = result.getOrNull()
                                            } else {
                                                feedbackMessage = result.exceptionOrNull()?.message
                                            }
                                        },
                                        testTag = "unlock_course_btn"
                                    )
                                }
                            }
                        }
                    } else {
                        // Progress
                        val progressRatio = if (course.totalLessons > 0) course.completedLessons.toFloat() / course.totalLessons else 0f
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (lang == AppLanguage.FR) "Progression de l'apprentissage" else "Learning Progress",
                                    fontSize = 12.sp,
                                    color = TextLight,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${course.completedLessons} / ${course.totalLessons} ${if (lang == AppLanguage.FR) "leçons" else "lessons"}",
                                    fontSize = 12.sp,
                                    color = ElectricBlue,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { progressRatio },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = ElectricBlue,
                                trackColor = MidnightBorder
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (lang == AppLanguage.FR) "Programme des Leçons & Résumés Clés" else "Curriculum & Key Takeaways",
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(course.lessons) { lesson ->
                            LessonItem(
                                lesson = lesson,
                                isEnrolled = course.isEnrolled,
                                lang = lang,
                                onToggleComplete = {
                                    if (course.isEnrolled) {
                                        repository.completeLesson(course.id, lesson.id)
                                        viewingCourse = repository.courses.value.find { it.id == course.id }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CourseCard(course: Course, lang: AppLanguage, onClick: () -> Unit) {
    FuturisticCard(modifier = Modifier.clickable(onClick = onClick)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
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

                    if (course.isEnrolled) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(shape = RoundedCornerShape(6.dp), color = SuccessGreen.copy(alpha = 0.2f)) {
                            Text(
                                text = if (lang == AppLanguage.FR) "INSCRIT" else "ENROLLED",
                                color = SuccessGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (lang == AppLanguage.FR) course.titleFr else course.titleEn,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextWhite
                )

                Text(
                    text = "Par ${course.instructor} • ${course.totalLessons} leçons",
                    color = TextMuted,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )

                if (course.conferenceDate != null) {
                    Text(
                        text = "📅 ${course.conferenceDate}",
                        color = WarningAmber,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (course.price == 0L) (if (lang == AppLanguage.FR) "GRATUIT" else "FREE") else "${course.price} FCFA",
                    color = if (course.price == 0L) SuccessGreen else ElectricBlue,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = MtnMoMo, modifier = Modifier.size(14.dp))
                    Text(course.rating.toString(), color = TextLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (course.isEnrolled) {
            Spacer(modifier = Modifier.height(10.dp))
            val progressRatio = if (course.totalLessons > 0) course.completedLessons.toFloat() / course.totalLessons else 0f
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LinearProgressIndicator(
                    progress = { progressRatio },
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = ElectricBlue,
                    trackColor = MidnightBorder
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("${(progressRatio * 100).toInt()}%", color = ElectricBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun LessonItem(lesson: Lesson, isEnrolled: Boolean, lang: AppLanguage, onToggleComplete: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = MidnightCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (lesson.isCompleted) SuccessGreen.copy(alpha = 0.5f) else MidnightBorder)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    IconButton(
                        onClick = onToggleComplete,
                        enabled = isEnrolled,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (lesson.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = if (lesson.isCompleted) SuccessGreen else TextMuted
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (lang == AppLanguage.FR) lesson.titleFr else lesson.titleEn,
                        color = TextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text("${lesson.durationMinutes} min", color = TextMuted, fontSize = 11.sp)
            }

            // Key Takeaway
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, start = 34.dp),
                shape = RoundedCornerShape(6.dp),
                color = MidnightSurface
            ) {
                Text(
                    text = "💡 ${if (lang == AppLanguage.FR) lesson.keyTakeawayFr else lesson.keyTakeawayEn}",
                    color = TextLight,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    modifier = Modifier.padding(6.dp)
                )
            }
        }
    }
}
