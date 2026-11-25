package com.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.R
import com.example.data.AiTutorRepository
import com.example.data.FirestoreRepository
import com.example.firebase.authStateFlow
import com.example.firebase.signOutUser
import com.example.model.UiState
import com.example.model.UserProfile
import com.example.ui.components.ErrorBannerCard
import com.example.ui.components.LoadingStateCard
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.PlannerAnalyticsScreen
import com.example.ui.screens.PracticeExamScreen
import com.example.ui.screens.SyllabusScreen
import com.example.ui.screens.TutorScreen
import com.example.ui.theme.IcmaM3Theme
import com.example.viewmodel.PortalViewModel
import com.example.voice.VoiceTutorManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

object Routes {
    const val DASHBOARD = "dashboard"
    const val STUDY = "study"
    const val JARVIS = "jarvis"
    const val EXAMS = "exams"
    const val PLANNER = "planner"
}

private data class NavTab(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private val portalTabs = listOf(
    NavTab(Routes.DASHBOARD, "Home", Icons.Filled.Dashboard),
    NavTab(Routes.STUDY, "Study", Icons.AutoMirrored.Filled.MenuBook),
    NavTab(Routes.JARVIS, "Jarvis", Icons.Filled.AutoAwesome),
    NavTab(Routes.EXAMS, "Exams", Icons.Filled.Quiz),
    NavTab(Routes.PLANNER, "Plan", Icons.Filled.Analytics)
)

@Composable
fun AppNavigation(auth: FirebaseAuth = Firebase.auth) {
    val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)
    val user = currentUser

    if (user == null) {
        IcmaM3Theme(darkTheme = true) {
            Surface(modifier = Modifier.fillMaxSize()) {
                AuthScreen(onAuthSuccess = { })
            }
        }
    } else {
        key(user.uid) {
            AuthenticatedPortal(
                currentUserId = user.uid,
                userDisplayName = user.displayName ?: "ICMA Candidate",
                userEmail = user.email
            )
        }
    }
}

@Composable
private fun AuthenticatedPortal(
    currentUserId: String,
    userDisplayName: String,
    userEmail: String?
) {
    val context = LocalContext.current
    val appContext = context.applicationContext
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }
    val voiceManager = remember(appContext) { VoiceTutorManager(appContext) }

    DisposableEffect(voiceManager) {
        onDispose { voiceManager.shutdown() }
    }

    val viewModel: PortalViewModel = viewModel(
        key = currentUserId,
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                val databaseId = app.getString(R.string.firestore_database_id)
                val db = FirebaseFirestore.getInstance(databaseId)
                PortalViewModel(
                    repository = FirestoreRepository(db),
                    aiRepository = AiTutorRepository(),
                    voiceManager = voiceManager,
                    currentUserId = currentUserId,
                    initialDisplayName = userDisplayName
                )
            }
        }
    )

    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    val progressState by viewModel.topicProgressState.collectAsStateWithLifecycle()
    val examHistoryState by viewModel.examHistoryState.collectAsStateWithLifecycle()
    val leaderboardState by viewModel.leaderboardState.collectAsStateWithLifecycle()
    val studyPlan by viewModel.studyPlanState.collectAsStateWithLifecycle()
    val selectedPart by viewModel.selectedPart.collectAsStateWithLifecycle()
    val selectedTopicId by viewModel.selectedTopicId.collectAsStateWithLifecycle()
    val srsSessionActive by viewModel.srsSessionActive.collectAsStateWithLifecycle()
    val srsQueue by viewModel.srsQueue.collectAsStateWithLifecycle()
    val tutorState by viewModel.tutorState.collectAsStateWithLifecycle()
    val mcqState by viewModel.mcqPracticeState.collectAsStateWithLifecycle()
    val mockState by viewModel.mockExamState.collectAsStateWithLifecycle()
    val crashPreview by viewModel.crashPreview.collectAsStateWithLifecycle()
    val voiceState by voiceManager.state.collectAsStateWithLifecycle()
    val bannerMessage by viewModel.bannerMessage.collectAsStateWithLifecycle()
    val backupBusy by viewModel.backupBusy.collectAsStateWithLifecycle()

    val profile = (profileState as? UiState.Success)?.data ?: UserProfile(userId = currentUserId, displayName = userDisplayName)
    val progressMap = (progressState as? UiState.Success)?.data ?: emptyMap()
    val examHistory = (examHistoryState as? UiState.Success)?.data ?: emptyList()
    val leaderboard = (leaderboardState as? UiState.Success)?.data ?: emptyList()

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: Routes.DASHBOARD
    var examInitialSubTab by remember { mutableIntStateOf(0) }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(bannerMessage) {
        val msg = bannerMessage
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissBanner()
        }
    }

    fun navigateToTab(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    IcmaM3Theme(darkTheme = profile.darkModeEnabled) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val useRail = maxWidth >= 720.dp

            Scaffold(
                contentWindowInsets = WindowInsets.safeDrawing,
                snackbarHost = { SnackbarHost(snackbarHostState) },
                bottomBar = {
                    if (!useRail) {
                        NavigationBar {
                            portalTabs.forEach { tab ->
                                NavigationBarItem(
                                    selected = currentRoute == tab.route,
                                    onClick = { navigateToTab(tab.route) },
                                    icon = { Icon(tab.icon, contentDescription = tab.label) },
                                    label = { Text(tab.label, maxLines = 1) },
                                    modifier = Modifier.testTag("nav_${tab.route}")
                                )
                            }
                        }
                    }
                }
            ) { innerPadding ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (useRail) {
                        NavigationRail {
                            portalTabs.forEach { tab ->
                                NavigationRailItem(
                                    selected = currentRoute == tab.route,
                                    onClick = { navigateToTab(tab.route) },
                                    icon = { Icon(tab.icon, contentDescription = tab.label) },
                                    label = { Text(tab.label) },
                                    modifier = Modifier.testTag("rail_${tab.route}")
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .widthIn(max = 860.dp)
                        ) {
                            when {
                                profileState is UiState.Error -> {
                                    Box(modifier = Modifier.padding(16.dp)) {
                                        ErrorBannerCard(message = (profileState as UiState.Error).message)
                                    }
                                }
                                profileState is UiState.Loading && progressState is UiState.Loading -> {
                                    LoadingStateCard()
                                }
                                else -> {
                                    NavHost(
                                        navController = navController,
                                        startDestination = Routes.DASHBOARD,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        composable(Routes.DASHBOARD) {
                                            DashboardScreen(
                                                profile = profile,
                                                userEmail = userEmail,
                                                progressMap = progressMap,
                                                examHistory = examHistory,
                                                studyPlan = studyPlan,
                                                onStartSrsSession = {
                                                    viewModel.startSrsReviewSession()
                                                    navigateToTab(Routes.STUDY)
                                                },
                                                onOpenPart = { part ->
                                                    viewModel.selectPart(part)
                                                    navigateToTab(Routes.STUDY)
                                                },
                                                onOpenTopic = { topicId ->
                                                    viewModel.selectTopic(topicId)
                                                    navigateToTab(Routes.STUDY)
                                                },
                                                onOpenMockExam = {
                                                    examInitialSubTab = 1
                                                    navigateToTab(Routes.EXAMS)
                                                },
                                                onOpenJarvis = {
                                                    navigateToTab(Routes.JARVIS)
                                                },
                                                onOpenPlanner = {
                                                    navigateToTab(Routes.PLANNER)
                                                }
                                            )
                                        }

                                        composable(Routes.STUDY) {
                                            SyllabusScreen(
                                                progressMap = progressMap,
                                                selectedPart = selectedPart,
                                                selectedTopicId = selectedTopicId,
                                                srsSessionActive = srsSessionActive,
                                                srsQueue = srsQueue,
                                                voiceState = voiceState,
                                                onSelectPart = viewModel::selectPart,
                                                onSelectTopic = viewModel::selectTopic,
                                                onToggleBookmark = viewModel::toggleTopicBookmark,
                                                onToggleComplete = viewModel::toggleTopicCompleted,
                                                onSaveNotes = viewModel::saveTopicUserNotes,
                                                onRateSrs = viewModel::rateTopicSrs,
                                                onStartSrs = viewModel::startSrsReviewSession,
                                                onCloseSrs = viewModel::closeSrsReviewSession,
                                                onAskJarvisTopic = { topic ->
                                                    viewModel.focusTutorOnTopic(topic.id)
                                                    viewModel.sendTutorMessage("Teach me '${topic.title}' for ICMA M3 using ${profile.preferredTeachingMode} style.")
                                                    navigateToTab(Routes.JARVIS)
                                                },
                                                onAskJarvisPart = { part ->
                                                    viewModel.focusTutorOnTopic(null)
                                                    viewModel.sendTutorMessage("Give me a structured ICMA M3 exam overview of ${part.code} (${part.title}, ${part.weightPercent}% weightage) using ${profile.preferredTeachingMode} style.")
                                                    navigateToTab(Routes.JARVIS)
                                                },
                                                onPracticeTopicMcq = { topic ->
                                                    viewModel.loadOfflineMcqs(topic.part, topic.id, null, 6)
                                                    examInitialSubTab = 0
                                                    navigateToTab(Routes.EXAMS)
                                                },
                                                onPracticePartMcq = { part ->
                                                    viewModel.loadOfflineMcqs(part, null, null, 8)
                                                    examInitialSubTab = 0
                                                    navigateToTab(Routes.EXAMS)
                                                },
                                                onSpeakToggle = { label, text ->
                                                    if (profile.voiceEnabled) {
                                                        voiceManager.toggle(label, text)
                                                    }
                                                }
                                            )
                                        }

                                        composable(Routes.JARVIS) {
                                            TutorScreen(
                                                profile = profile,
                                                tutorState = tutorState,
                                                voiceState = voiceState,
                                                onSelectMode = viewModel::setTutorMode,
                                                onSelectLanguage = viewModel::setTutorLanguage,
                                                onToggleOffline = viewModel::setTutorOfflineMode,
                                                onSelectFocusedTopic = viewModel::focusTutorOnTopic,
                                                onSendMessage = viewModel::sendTutorMessage,
                                                onRetryLastMessage = viewModel::retryLastTutorMessage,
                                                onClearChat = viewModel::clearTutorChat,
                                                onSpeakToggle = { label, text ->
                                                    if (profile.voiceEnabled) {
                                                        voiceManager.toggle(label, text)
                                                    }
                                                }
                                            )
                                        }

                                        composable(Routes.EXAMS) {
                                            PracticeExamScreen(
                                                mcqState = mcqState,
                                                mockState = mockState,
                                                voiceState = voiceState,
                                                initialSubTab = examInitialSubTab,
                                                onLoadOfflineMcqs = viewModel::loadOfflineMcqs,
                                                onGenerateAiMcqs = viewModel::generateAiMcqs,
                                                onAnswerPracticeMcq = viewModel::answerPracticeMcq,
                                                onStartMockExam = viewModel::startMockExam,
                                                onSelectMockAnswer = viewModel::selectMockAnswer,
                                                onToggleMockFlag = viewModel::toggleMockFlag,
                                                onJumpMockQuestion = viewModel::jumpToMockQuestion,
                                                onSubmitMockExam = viewModel::submitMockExam,
                                                onExitMockExam = viewModel::exitMockExam,
                                                onOpenTopicFromResults = { topicId ->
                                                    viewModel.selectTopic(topicId)
                                                    navigateToTab(Routes.STUDY)
                                                },
                                                onSpeakToggle = { label, text ->
                                                    if (profile.voiceEnabled) {
                                                        voiceManager.toggle(label, text)
                                                    }
                                                }
                                            )
                                        }

                                        composable(Routes.PLANNER) {
                                            PlannerAnalyticsScreen(
                                                profile = profile,
                                                userEmail = userEmail,
                                                progressMap = progressMap,
                                                examHistory = examHistory,
                                                studyPlan = studyPlan,
                                                leaderboard = leaderboard,
                                                crashPreview = crashPreview,
                                                backupBusy = backupBusy,
                                                onToggleCrashPreview = viewModel::toggleCrashPreview,
                                                onUpdatePlanner = viewModel::updatePlannerSettings,
                                                onLogBlockDone = viewModel::logPlannerBlockCompleted,
                                                onOpenTopic = { topicId ->
                                                    viewModel.selectTopic(topicId)
                                                    navigateToTab(Routes.STUDY)
                                                },
                                                onSaveAccountPrefs = viewModel::updateProfilePreferences,
                                                onToggleDarkMode = viewModel::toggleDarkMode,
                                                onBuildBackupJson = viewModel::buildBackupJson,
                                                onImportBackupJson = viewModel::importBackupJson,
                                                onSignOut = {
                                                    signOutUser(
                                                        credentialManager = credentialManager,
                                                        scope = scope,
                                                        onBeforeSignOut = { viewModel.cleanupForSignOut() }
                                                    )
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
