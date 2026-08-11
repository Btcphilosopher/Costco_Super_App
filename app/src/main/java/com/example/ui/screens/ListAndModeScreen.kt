package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ShoppingItem
import com.example.ui.CostcoViewModel
import com.example.ui.theme.*

@Composable
fun ListAndModeScreen(viewModel: CostcoViewModel) {
    val shoppingList by viewModel.shoppingList.collectAsState()
    val isWarehouseMode by viewModel.isWarehouseMode.collectAsState()
    val selectedWarehouse by viewModel.selectedWarehouse.collectAsState()

    var manualItemName by remember { mutableStateOf("") }
    var manualItemAisle by remember { mutableStateOf("Aisle 1") }
    var selectedAisleToHighlight by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("list_and_mode_screen")
    ) {
        // --- HEADER WITH TOGGLE ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
            colors = CardDefaults.cardColors(containerColor = CostcoNavy),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Shopping Assistant",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        )
                        Text(
                            text = selectedWarehouse.name,
                            style = MaterialTheme.typography.bodySmall.copy(color = GoldReward)
                        )
                    }

                    // Warehouse Mode Switch
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Warehouse Mode",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isWarehouseMode) GoldReward else Color.White.copy(alpha = 0.6f)
                            )
                        )
                        Switch(
                            checked = isWarehouseMode,
                            onCheckedChange = { viewModel.toggleWarehouseMode(it) },
                            modifier = Modifier.testTag("warehouse_mode_switch"),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = CostcoNavy,
                                checkedTrackColor = GoldReward,
                                uncheckedThumbColor = Color.LightGray,
                                uncheckedTrackColor = Color.DarkGray
                            )
                        )
                    }
                }
            }
        }

        AnimatedContent(
            targetState = isWarehouseMode,
            transitionSpec = {
                slideInVertically { height -> height } + fadeIn() togetherWith
                        slideOutVertically { height -> -height } + fadeOut()
            },
            label = "mode_transition"
        ) { mode ->
            if (mode) {
                // --- WAREHOUSE MODE ACTIVE ---
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Warehouse Mode Status Panel
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = StockGreen.copy(alpha = 0.08f)),
                        border = BorderStroke(1.dp, StockGreen.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(StockGreen, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Navigation, contentDescription = "Nav", tint = Color.White)
                            }
                            Column {
                                Text(
                                    text = "Connected to ${selectedWarehouse.name} Wi-Fi",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = StockGreen)
                                )
                                Text(
                                    text = "Indoor location enabled. Finding your aisles.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextGrey
                                )
                            }
                        }
                    }

                    // 2. Custom Warehouse Map Canvas
                    Text(
                        text = "INDOOR WAREHOUSE MAP",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = CostcoNavy)
                    )

                    WarehouseIndoorMap(
                        highlightedAisle = selectedAisleToHighlight,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                    )

                    // 3. Mini Checklist with Aisle buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TAP ITEM TO HIGHLIGHT ON MAP",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = CostcoRed)
                        )
                        if (selectedAisleToHighlight != null) {
                            TextButton(onClick = { selectedAisleToHighlight = null }) {
                                Text("Clear pin", color = TextGrey)
                            }
                        }
                    }

                    if (shoppingList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Your shopping list is empty. Add bulk catalog items to trigger map indicators.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextGrey,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(shoppingList) { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (selectedAisleToHighlight == item.aisle) CostcoBlue.copy(
                                                alpha = 0.08f
                                            ) else MaterialTheme.colorScheme.surface
                                        )
                                        .border(
                                            1.dp,
                                            if (selectedAisleToHighlight == item.aisle) CostcoBlue else BorderLight.copy(alpha = 0.5f),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            selectedAisleToHighlight = item.aisle
                                        }
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        IconButton(
                                            onClick = { viewModel.toggleShoppingItemChecked(item) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                if (item.isChecked) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                                                contentDescription = "Check",
                                                tint = if (item.isChecked) StockGreen else TextGrey
                                            )
                                        }

                                        Column {
                                            Text(
                                                text = item.name,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None
                                                ),
                                                color = if (item.isChecked) TextGrey else TextDark
                                            )
                                            if (item.isKirkland) {
                                                Text(
                                                    text = "Kirkland Signature Value",
                                                    style = MaterialTheme.typography.labelSmall.copy(color = CostcoRed, fontWeight = FontWeight.Bold)
                                                )
                                            }
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(CostcoBlue.copy(alpha = 0.1f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = item.aisle,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = CostcoBlue,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // --- REGULAR SHOPPING LIST MODE ---
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Manual Add Input Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, BorderLight)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "QUICK ADD BULK ITEMS",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = CostcoRed)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = manualItemName,
                                    onValueChange = { manualItemName = it },
                                    modifier = Modifier
                                        .weight(2f)
                                        .testTag("manual_item_input"),
                                    placeholder = { Text("E.g., Eggs, Milk, Towels") },
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = manualItemAisle,
                                    onValueChange = { manualItemAisle = it },
                                    modifier = Modifier.weight(1f),
                                    placeholder = { Text("Aisle #") },
                                    singleLine = true
                                )
                            }
                            Button(
                                onClick = {
                                    viewModel.addManualItemToList(manualItemName, manualItemAisle)
                                    manualItemName = ""
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("add_item_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = CostcoBlue),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.AddShoppingCart, contentDescription = "Add")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Add Custom List Item")
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "MY SHOPPING LIST (${shoppingList.size} items)",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = CostcoNavy)
                        )
                        if (shoppingList.isNotEmpty()) {
                            TextButton(onClick = { viewModel.clearShoppingList() }) {
                                Text("Clear All", color = CostcoRed, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (shoppingList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.PlaylistAdd,
                                    contentDescription = "Empty",
                                    tint = TextGrey,
                                    modifier = Modifier.size(64.dp)
                                )
                                Text(
                                    text = "Your list is empty",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Text(
                                    text = "Search our bulk catalog and add items directly here",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextGrey
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .testTag("shopping_list_items"),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(shoppingList) { item ->
                                ShoppingListItemCard(
                                    item = item,
                                    onCheckToggle = { viewModel.toggleShoppingItemChecked(item) },
                                    onDelete = { viewModel.removeShoppingItem(item) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShoppingListItemCard(
    item: ShoppingItem,
    onCheckToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, BorderLight.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                IconButton(onClick = onCheckToggle) {
                    Icon(
                        if (item.isChecked) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = "Check",
                        tint = if (item.isChecked) StockGreen else CostcoBlue
                    )
                }

                Column {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None
                        ),
                        color = if (item.isChecked) TextGrey else TextDark
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = item.packSize,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGrey
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(WarmLight)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = item.aisle,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    color = CostcoNavy,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = CostcoRed)
            }
        }
    }
}

@Composable
fun WarehouseIndoorMap(
    highlightedAisle: String?,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Outer warehouse outline
        drawRect(
            color = BorderLight,
            topLeft = Offset(0f, 0f),
            size = size,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
        )

        // General labels & grid division
        // Department Areas (Produce, Deli, Electronics, Registers)
        // Electronics (Top Left)
        drawRect(
            color = CostcoNavy.copy(alpha = 0.05f),
            topLeft = Offset(10f, 10f),
            size = Size(w * 0.3f, h * 0.3f)
        )
        // Produce Fridge (Bottom Right)
        drawRect(
            color = StockGreen.copy(alpha = 0.05f),
            topLeft = Offset(w * 0.7f, h * 0.5f),
            size = Size(w * 0.25f, h * 0.45f)
        )
        // Deli (Top Right)
        drawRect(
            color = StockOrange.copy(alpha = 0.05f),
            topLeft = Offset(w * 0.7f, 10f),
            size = Size(w * 0.25f, h * 0.35f)
        )
        // Registers (Bottom Center)
        drawRect(
            color = CostcoRed.copy(alpha = 0.05f),
            topLeft = Offset(w * 0.2f, h * 0.8f),
            size = Size(w * 0.4f, h * 0.15f)
        )

        // Grid of Aisles: representing Aisles 1 to 20
        // Aisle 12 (Groceries)
        val isA12High = highlightedAisle?.contains("12") == true
        drawRect(
            color = if (isA12High) CostcoRed else CostcoBlue.copy(alpha = 0.2f),
            topLeft = Offset(w * 0.4f, h * 0.15f),
            size = Size(16f, h * 0.2f)
        )

        // Aisle 17 (Groceries / Towels)
        val isA17High = highlightedAisle?.contains("17") == true
        drawRect(
            color = if (isA17High) CostcoRed else CostcoBlue.copy(alpha = 0.2f),
            topLeft = Offset(w * 0.5f, h * 0.15f),
            size = Size(16f, h * 0.2f)
        )

        // Aisle 18 (Bath Tissue)
        val isA18High = highlightedAisle?.contains("18") == true
        drawRect(
            color = if (isA18High) CostcoRed else CostcoBlue.copy(alpha = 0.2f),
            topLeft = Offset(w * 0.58f, h * 0.15f),
            size = Size(16f, h * 0.2f)
        )

        // Aisle 24 (Dog Food)
        val isA24High = highlightedAisle?.contains("24") == true
        drawRect(
            color = if (isA24High) CostcoRed else CostcoBlue.copy(alpha = 0.2f),
            topLeft = Offset(w * 0.45f, h * 0.45f),
            size = Size(16f, h * 0.2f)
        )

        // Produce A (Highlight)
        val isProdHigh = highlightedAisle?.contains("Produce") == true || highlightedAisle?.contains("Dairy") == true
        if (isProdHigh) {
            drawCircle(
                color = CostcoRed,
                radius = 16f,
                center = Offset(w * 0.8f, h * 0.7f)
            )
        }
    }
}
