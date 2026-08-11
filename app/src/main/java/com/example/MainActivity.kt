package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.CostcoRepository
import com.example.ui.CostcoViewModel
import com.example.ui.CostcoViewModelFactory
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                val database = remember { AppDatabase.getDatabase(context) }
                val repository = remember {
                    CostcoRepository(
                        database.shoppingItemDao(),
                        database.costcoOrderDao(),
                        database.savedMembershipDao()
                    )
                }
                val costcoViewModel: CostcoViewModel = viewModel(
                    factory = CostcoViewModelFactory(repository)
                )

                CostcoSuperAppContent(costcoViewModel)
            }
        }
    }
}

@Composable
fun CostcoSuperAppContent(viewModel: CostcoViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var activeServiceSection by remember { mutableStateOf("FoodCourt") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home", fontSize = 10.sp) },
                    modifier = Modifier.testTag("tab_home")
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.CreditCard, contentDescription = "Membership") },
                    label = { Text("Membership", fontSize = 10.sp) },
                    modifier = Modifier.testTag("tab_membership")
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Storefront, contentDescription = "Shop") },
                    label = { Text("Shop", fontSize = 10.sp) },
                    modifier = Modifier.testTag("tab_shop")
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.List, contentDescription = "List") },
                    label = { Text("List & Mode", fontSize = 10.sp) },
                    modifier = Modifier.testTag("tab_list")
                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = { Icon(Icons.Default.Star, contentDescription = "Services") },
                    label = { Text("Services", fontSize = 10.sp) },
                    modifier = Modifier.testTag("tab_services")
                )
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(top = 24.dp) // status bar safe margin
        ) {
            when (selectedTab) {
                0 -> HomeScreen(
                    viewModel = viewModel,
                    onNavigateToTab = { tabIndex ->
                        selectedTab = tabIndex
                    },
                    onNavigateToService = { serviceId ->
                        activeServiceSection = serviceId
                        selectedTab = 4 // Switch to services tab
                    }
                )
                1 -> MembershipScreen(viewModel = viewModel)
                2 -> ShopScreen(viewModel = viewModel)
                3 -> ListAndModeScreen(viewModel = viewModel)
                4 -> ServicesScreen(viewModel = viewModel, initialService = activeServiceSection)
            }
        }
    }
}
