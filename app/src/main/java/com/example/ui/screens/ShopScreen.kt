package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.CostcoOrder
import com.example.ui.CostcoViewModel
import com.example.ui.Product
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(viewModel: CostcoViewModel) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredProducts by viewModel.filteredProducts.collectAsState()
    val cart by viewModel.cart.collectAsState()
    val cartTotal by viewModel.cartTotal.collectAsState()
    val orders by viewModel.orderHistory.collectAsState()

    var activeCategory by remember { mutableStateOf("All") }
    var showCartDialog by remember { mutableStateOf(false) }
    var selectedProductForDetail by remember { mutableStateOf<Product?>(null) }
    var checkoutSuccessMessage by remember { mutableStateOf("") }
    var activeSubTab by remember { mutableStateOf("Catalog") } // "Catalog" or "Orders"

    val categories = listOf("All", "Groceries", "Kirkland Signature", "Electronics", "Furniture", "Appliances", "Clothing", "Home")

    val displayedProducts = if (activeCategory == "All") {
        filteredProducts
    } else {
        filteredProducts.filter { it.category.equals(activeCategory, ignoreCase = true) || (activeCategory == "Kirkland Signature" && it.brand == "Kirkland Signature") }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("shop_screen")
    ) {
        // --- Tab Header: Catalog vs Orders ---
        TabRow(
            selectedTabIndex = if (activeSubTab == "Catalog") 0 else 1,
            containerColor = CostcoNavy,
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[if (activeSubTab == "Catalog") 0 else 1]),
                    color = GoldReward
                )
            }
        ) {
            Tab(
                selected = activeSubTab == "Catalog",
                onClick = { activeSubTab = "Catalog" },
                text = { Text("Bulk Catalog", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = activeSubTab == "Orders",
                onClick = { activeSubTab = "Orders" },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("My Orders", fontWeight = FontWeight.Bold)
                        if (orders.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .background(CostcoRed, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = orders.size.toString(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            )
        }

        if (activeSubTab == "Catalog") {
            // --- SEARCH BAR ---
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .testTag("shop_search_input"),
                placeholder = { Text("Search 10,000+ warehouse items...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // --- CATEGORY ROW ---
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    val isSelected = activeCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { activeCategory = category },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CostcoRed,
                            selectedLabelColor = Color.White,
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = BorderLight,
                            selectedBorderColor = CostcoRed
                        )
                    )
                }
            }

            // --- PRODUCTS DISPLAY GRID/LIST ---
            if (displayedProducts.isEmpty()) {
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
                            Icons.Default.SearchOff,
                            contentDescription = "No items",
                            tint = TextGrey,
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "No bulk items found",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Try refining your query or resetting filters",
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
                        .testTag("products_list"),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(displayedProducts) { product ->
                        ProductCard(
                            product = product,
                            onClick = { selectedProductForDetail = product },
                            onAddToCart = {
                                viewModel.addProductToCart(product)
                            },
                            onAddToList = {
                                viewModel.addItemToShoppingList(product)
                            }
                        )
                    }
                }
            }

            // --- BOTTOM FLOATING CART SUMMARY ---
            AnimatedVisibility(
                visible = cart.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Card(
                    onClick = { showCartDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .testTag("floating_cart"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CostcoNavy),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(GoldReward, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cart.values.sum().toString(),
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = CostcoNavy,
                                        fontWeight = FontWeight.Black
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Simulated Register Cart",
                                    style = MaterialTheme.typography.titleSmall.copy(color = Color.White, fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Ready to checkout bulk savings",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.7f))
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = String.format("$%.2f", cartTotal),
                                style = MaterialTheme.typography.titleMedium.copy(color = GoldReward, fontWeight = FontWeight.Bold)
                            )
                            Icon(Icons.Default.ArrowForward, contentDescription = "Checkout", tint = Color.White)
                        }
                    }
                }
            }
        } else {
            // --- ORDERS SUBTAB VIEW ---
            if (orders.isEmpty()) {
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
                            Icons.Default.ReceiptLong,
                            contentDescription = "No orders",
                            tint = TextGrey,
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "No order history",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Try placing a simulated order by checking out items",
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
                        .testTag("orders_list"),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(orders) { order ->
                        OrderCard(order = order)
                    }
                }
            }
        }
    }

    // --- NOTIFICATION DIALOGS ---

    // 1. Product Detail Dialog
    if (selectedProductForDetail != null) {
        val prod = selectedProductForDetail!!
        Dialog(onDismissRequest = { selectedProductForDetail = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = prod.brand.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = CostcoRed,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                text = prod.name,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                            )
                        }
                        IconButton(onClick = { selectedProductForDetail = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(WarmLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (prod.brand == "Kirkland Signature") " Kirkland " else " Costco Bulk ",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (prod.brand == "Kirkland Signature") CostcoNavy else TextGrey
                                ),
                                modifier = Modifier
                                    .border(1.dp, if (prod.brand == "Kirkland Signature") CostcoNavy else BorderLight, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Pack Size: ${prod.packSize}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextGrey
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = String.format("$%.2f", prod.price),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = CostcoNavy
                                )
                            )
                            Text(
                                text = prod.pricePerUnit,
                                style = MaterialTheme.typography.labelMedium,
                                color = TextGrey
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (prod.inStock) StockGreen.copy(alpha = 0.1f) else StockRed.copy(alpha = 0.1f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (prod.inStock) "IN STOCK (${prod.aisle})" else "OUT OF STOCK",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (prod.inStock) StockGreen else StockRed,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Text(
                        text = prod.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextGrey,
                        lineHeight = 18.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.addItemToShoppingList(prod)
                                selectedProductForDetail = null
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, CostcoBlue)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add List", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add List", color = CostcoBlue)
                        }

                        Button(
                            onClick = {
                                viewModel.addProductToCart(prod)
                                selectedProductForDetail = null
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = CostcoRed),
                            shape = RoundedCornerShape(8.dp),
                            enabled = prod.inStock
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Add Cart", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Cart")
                        }
                    }
                }
            }
        }
    }

    // 2. Checkout Simulation Card Register Dialog
    if (showCartDialog) {
        Dialog(onDismissRequest = { showCartDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Register Checkout",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = CostcoNavy
                            )
                        )
                        IconButton(onClick = { showCartDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    // List of checkout items
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(cart.entries.toList()) { entry ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${entry.key.brand} ${entry.key.name}",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${entry.key.packSize} • Qty: ${entry.value}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextGrey
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    IconButton(
                                        onClick = { viewModel.removeProductFromCart(entry.key) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.RemoveCircleOutline, contentDescription = "Sub", tint = CostcoRed)
                                    }
                                    Text(
                                        text = String.format("$%.2f", entry.key.price * entry.value),
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    }

                    Divider(color = BorderLight.copy(alpha = 0.5f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TOTAL SAVINGS",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextGrey
                        )
                        Text(
                            text = String.format("$%.2f", cartTotal),
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = CostcoRed,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TextButton(
                            onClick = {
                                viewModel.clearCart()
                                showCartDialog = false
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Clear", color = TextGrey)
                        }

                        Button(
                            onClick = {
                                viewModel.simulateCheckout()
                                showCartDialog = false
                                checkoutSuccessMessage = "Order placed successfully! 2% Executive rewards savings accumulated."
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CostcoRed),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(2f)
                        ) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = "Complete")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Pay & Complete")
                        }
                    }
                }
            }
        }
    }

    if (checkoutSuccessMessage.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { checkoutSuccessMessage = "" },
            title = { Text("Checkout Successful") },
            text = { Text(checkoutSuccessMessage) },
            confirmButton = {
                Button(
                    onClick = {
                        checkoutSuccessMessage = ""
                        activeSubTab = "Orders" // Jump to see receipts!
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CostcoRed)
                ) {
                    Text("View Orders")
                }
            }
        )
    }
}

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
    onAddToList: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, BorderLight.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Simulated product image or badge
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(WarmLight),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (product.brand == "Kirkland Signature") "KIRKLAND" else "BULK",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (product.brand == "Kirkland Signature") CostcoRed else CostcoNavy
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Icon(
                        if (product.category == "Electronics") Icons.Default.Tv else Icons.Default.AllInbox,
                        contentDescription = product.name,
                        tint = CostcoBlue.copy(alpha = 0.5f),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.brand,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = CostcoBlue,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                )
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = product.packSize,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGrey
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = String.format("$%.2f", product.price),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = CostcoNavy
                        )
                    )
                    Text(
                        text = product.pricePerUnit,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextGrey
                    )
                }
            }

            // Quick add buttons
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onAddToList,
                    modifier = Modifier
                        .size(36.dp)
                        .background(WarmLight, CircleShape)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add List", tint = CostcoNavy, modifier = Modifier.size(18.dp))
                }

                IconButton(
                    onClick = onAddToCart,
                    modifier = Modifier
                        .size(36.dp)
                        .background(CostcoRed.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Add Cart", tint = CostcoRed, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun OrderCard(order: CostcoOrder) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, BorderLight.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: order date and ID
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ORDER ID: #${order.orderId}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = CostcoNavy
                        )
                    )
                    Text(
                        text = "Ordered on ${order.date}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGrey
                    )
                }

                // Delivery Status Badge
                val statusColor = when (order.trackingStatus) {
                    "Delivered" -> StockGreen
                    "In Transit" -> StockOrange
                    else -> CostcoBlue
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(statusColor.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = order.status.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = statusColor,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Body: description
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(WarmLight, RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.LocalShipping,
                        contentDescription = "Shipping icon",
                        tint = CostcoNavy,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.title,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${order.itemsCount} total items • Tracking: ${order.trackingStatus}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGrey
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Divider(color = BorderLight.copy(alpha = 0.5f))

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Receipt, contentDescription = "Receipt", tint = TextGrey, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Show Digital Receipt",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = CostcoBlue
                    )
                }

                Text(
                    text = String.format("$%.2f", order.totalAmount),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = CostcoNavy
                    )
                )
            }
        }
    }
}
