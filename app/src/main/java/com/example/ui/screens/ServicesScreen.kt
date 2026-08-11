package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.CostcoViewModel
import com.example.ui.FoodMenuItem
import com.example.ui.theme.*

@Composable
fun ServicesScreen(
    viewModel: CostcoViewModel,
    initialService: String = "FoodCourt"
) {
    var selectedService by remember { mutableStateOf(initialService) }
    val services = listOf(
        "FoodCourt" to "Food Court",
        "Fuel" to "Costco Gas",
        "Pharmacy" to "Pharmacy",
        "Optical" to "Optical Center",
        "Travel" to "Costco Travel"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("services_screen")
    ) {
        // --- SERVICES SELECTION ROW ---
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.background(CostcoNavy)
        ) {
            items(services) { (id, label) ->
                val isSelected = selectedService == id
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedService = id },
                    label = { Text(label, fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldReward,
                        selectedLabelColor = CostcoNavy,
                        containerColor = CostcoNavy.copy(alpha = 0.5f),
                        labelColor = Color.White
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = Color.White.copy(alpha = 0.3f),
                        selectedBorderColor = GoldReward
                    )
                )
            }
        }

        // --- ACTIVE SERVICE VIEW ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(WarmLight)
        ) {
            when (selectedService) {
                "FoodCourt" -> FoodCourtView(viewModel)
                "Fuel" -> FuelView(viewModel)
                "Pharmacy" -> PharmacyView()
                "Optical" -> OpticalView()
                "Travel" -> TravelView()
            }
        }
    }
}

// ==========================================
// 1. FOOD COURT VIEW (With Order Simulation)
// ==========================================
@Composable
fun FoodCourtView(viewModel: CostcoViewModel) {
    val foodCart by viewModel.foodCart.collectAsState()
    val foodCartTotal by viewModel.foodCartTotal.collectAsState()
    val foodOrderStatus by viewModel.foodOrderStatus.collectAsState()
    val foodOrderNumber by viewModel.foodOrderNumber.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("food_court_view"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tracker Panel if Order is active
        if (foodOrderStatus != "Idle") {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CostcoNavy),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ACTIVE FOOD COURT ORDER",
                                style = MaterialTheme.typography.labelSmall.copy(color = GoldReward, fontWeight = FontWeight.Bold)
                            )

                            if (foodOrderStatus == "Ready") {
                                IconButton(
                                    onClick = { viewModel.resetFoodOrderStatus() },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.White)
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                val statusText = when (foodOrderStatus) {
                                    "Placing" -> "Submitting order to register..."
                                    "Preparing" -> "Preparing your order now!"
                                    "Ready" -> "Order ready for pickup!"
                                    else -> "Processing..."
                                }
                                Text(
                                    text = statusText,
                                    style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
                                )
                                if (foodOrderNumber.isNotEmpty()) {
                                    Text(
                                        text = "Pickup Ticket: #$foodOrderNumber",
                                        style = MaterialTheme.typography.titleLarge.copy(color = GoldReward, fontWeight = FontWeight.Black)
                                    )
                                }
                            }

                            // Dynamic Icon
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(Color.White.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                when (foodOrderStatus) {
                                    "Placing" -> CircularProgressIndicator(color = GoldReward, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                    "Preparing" -> Icon(Icons.Default.HourglassEmpty, contentDescription = "Prep", tint = GoldReward)
                                    "Ready" -> Icon(Icons.Default.CheckCircle, contentDescription = "Ready", tint = Color.Green)
                                }
                            }
                        }

                        // Progress Indicator
                        val progressValue = when (foodOrderStatus) {
                            "Placing" -> 0.2f
                            "Preparing" -> 0.6f
                            "Ready" -> 1.0f
                            else -> 0f
                        }
                        LinearProgressIndicator(
                            progress = progressValue,
                            modifier = Modifier.fillMaxWidth(),
                            color = GoldReward,
                            trackColor = Color.White.copy(alpha = 0.2f)
                        )
                    }
                }
            }
        }

        // Title and Info
        item {
            Column {
                Text(
                    text = "Costco Food Court Menu",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = CostcoNavy)
                )
                Text(
                    text = "Classic members' value pricing. Order ahead and pick up fresh in-store.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGrey
                )
            }
        }

        // Menu items
        items(viewModel.foodCourtMenu) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
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
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(WarmLight, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = item.imageEmoji, fontSize = 24.sp)
                        }

                        Column {
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = item.calories,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextGrey
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = String.format("$%.2f", item.price),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = CostcoNavy)
                        )

                        IconButton(
                            onClick = { viewModel.addFoodToCart(item) },
                            modifier = Modifier
                                .size(32.dp)
                                .background(CostcoRed.copy(alpha = 0.1f), CircleShape)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = CostcoRed, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // Cart Drawer
        if (foodCart.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, GoldReward)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "YOUR FOOD COURT BASKET",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = CostcoRed)
                        )

                        // Items list
                        foodCart.entries.forEach { entry ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${entry.key.name} x ${entry.value}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.weight(1f)
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    IconButton(
                                        onClick = { viewModel.removeFoodFromCart(entry.key) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.RemoveCircleOutline, contentDescription = "Sub", tint = CostcoRed)
                                    }
                                    Text(
                                        text = String.format("$%.2f", entry.key.price * entry.value),
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }

                        Divider(color = BorderLight.copy(alpha = 0.5f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total Amount", style = MaterialTheme.typography.bodyMedium, color = TextGrey)
                            Text(
                                text = String.format("$%.2f", foodCartTotal),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = CostcoNavy)
                            )
                        }

                        Button(
                            onClick = { viewModel.placeFoodCourtOrder() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = CostcoRed),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Payment, contentDescription = "Pay")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Submit & Pay Member Price")
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 2. FUEL VIEW
// ==========================================
@Composable
fun FuelView(viewModel: CostcoViewModel) {
    val selectedWarehouse by viewModel.selectedWarehouse.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CostcoNavy)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "COSTCO GAS STATION",
                            style = MaterialTheme.typography.labelSmall.copy(color = GoldReward, fontWeight = FontWeight.Bold)
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF4CAF50).copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "OPEN",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color.Green, fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    Text(
                        text = "${selectedWarehouse.name} Gas",
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.White, fontWeight = FontWeight.Bold)
                    )

                    Text(
                        text = "Address: ${selectedWarehouse.address}",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.7f))
                    )
                }
            }
        }

        item {
            Text(
                text = "TODAY'S FUEL PRICES",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = CostcoNavy)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FuelPriceCard(
                    fuelType = "Regular Unleaded",
                    octane = "87 Octane",
                    price = selectedWarehouse.fuelRegular,
                    modifier = Modifier.weight(1f)
                )

                FuelPriceCard(
                    fuelType = "Premium Unleaded",
                    octane = "91 Octane Premium",
                    price = selectedWarehouse.fuelPremium,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "KIRKLAND SIGNATURE FUEL",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = CostcoRed)
                    )
                    Text(
                        text = "Formulated with high-performance deposit control additives to clean valves and fuel injectors, improving mileage and restoring engine performance. Certified Top Tier.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGrey,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun FuelPriceCard(
    fuelType: String,
    octane: String,
    price: Double,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderLight.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = fuelType, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
            Text(text = octane, style = MaterialTheme.typography.labelSmall, color = TextGrey)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = String.format("$%.2f", price),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black, color = CostcoNavy)
            )
            Text(text = "per gallon", style = MaterialTheme.typography.labelSmall, color = TextGrey)
        }
    }
}

// ==========================================
// 3. PHARMACY VIEW
// ==========================================
@Composable
fun PharmacyView() {
    var rxStatus by remember { mutableStateOf("Rx #98721 - Ready for Pickup") }
    var actionClicked by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(text = "Costco Pharmacy", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = CostcoNavy))
                Text(text = "Refill, track, and transfer prescriptions at low Costco member prices.", style = MaterialTheme.typography.bodySmall, color = TextGrey)
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BorderLight)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "ACTIVE PRESCRIPTIONS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = CostcoRed)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Lisinopril 10mg", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                            Text(text = rxStatus, style = MaterialTheme.typography.bodySmall, color = StockGreen)
                        }

                        if (!actionClicked) {
                            Button(
                                onClick = {
                                    rxStatus = "Rx #98721 - Refill Requested (Processing)"
                                    actionClicked = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CostcoBlue),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Refill Rx", fontSize = 12.sp)
                            }
                        } else {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Requested", tint = StockGreen)
                        }
                    }
                }
            }
        }

        item {
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = CostcoNavy),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.SwapHoriz, contentDescription = "Transfer")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Transfer Existing Prescription")
            }
        }
    }
}

// ==========================================
// 4. OPTICAL VIEW
// ==========================================
@Composable
fun OpticalView() {
    var examBooked by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(text = "Costco Optical", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = CostcoNavy))
                Text(text = "Licensed optometrists & premium glasses frame collections.", style = MaterialTheme.typography.bodySmall, color = TextGrey)
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BorderLight)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "EYE EXAM SCHEDULER",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = CostcoRed)
                    )

                    if (!examBooked) {
                        Text(
                            text = "Next Available Appointment: Tomorrow at 2:00 PM",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Button(
                            onClick = { examBooked = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = CostcoRed),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Book Exam Appointment")
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Event, contentDescription = "Booked", tint = StockGreen)
                            Text(
                                text = "Exam Booked! Tomorrow at 2:00 PM.",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = StockGreen)
                            )
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "BROWSE DESIGNER FRAMES",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = CostcoNavy)
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OpticalFrameCard(
                    name = "Kirkland Classic",
                    price = "$59.99",
                    desc = "Titanium frame with anti-reflective coating.",
                    modifier = Modifier.weight(1f)
                )
                OpticalFrameCard(
                    name = "Wayfarer Sleek",
                    price = "$89.99",
                    desc = "Modern tortoise shell frames, impact resistant.",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun OpticalFrameCard(
    name: String,
    price: String,
    desc: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderLight.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
            Text(text = price, style = MaterialTheme.typography.titleMedium.copy(color = CostcoRed, fontWeight = FontWeight.Black))
            Text(text = desc, style = MaterialTheme.typography.bodySmall, color = TextGrey, lineHeight = 14.sp)
        }
    }
}

// ==========================================
// 5. TRAVEL VIEW
// ==========================================
@Composable
fun TravelView() {
    val vacations = listOf(
        Triple("Maui Beach Resort Escape", "5 Nights, Flights, Rental SUV included", "$1,499"),
        Triple("7-Night Caribbean Cruise", "All meals, deluxe suite & balcony", "$899"),
        Triple("European Heritage Tour", "London & Paris, hotel + fast train tickets", "$1,999")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(text = "Costco Travel", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = CostcoNavy))
                Text(text = "Unbelievable value on flights, hotels, cruises, and rental packages for members.", style = MaterialTheme.typography.bodySmall, color = TextGrey)
            }
        }

        item {
            Text(
                text = "EXCLUSIVE VACATION PACKAGES",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = CostcoRed)
            )
        }

        items(vacations) { (title, subtitle, price) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BorderLight.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = title, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = CostcoNavy))
                        Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = TextGrey)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = price, style = MaterialTheme.typography.titleLarge.copy(color = CostcoRed, fontWeight = FontWeight.Black))
                        Text(text = "per person", style = MaterialTheme.typography.labelSmall, color = TextGrey)
                    }
                }
            }
        }
    }
}
