package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.CostcoViewModel
import com.example.ui.theme.CostcoBlue
import com.example.ui.theme.CostcoNavy
import com.example.ui.theme.CostcoRed
import com.example.ui.theme.GoldReward

@Composable
fun MembershipScreen(viewModel: CostcoViewModel) {
    val membershipState by viewModel.membershipProfile.collectAsState()
    val membership = membershipState ?: return

    var showScanDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf(membership.memberName) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("membership_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- TITLE ---
        item {
            Text(
                text = "Digital Membership",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = CostcoNavy
                ),
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        // --- THE DIGITAL CARD ---
        item {
            Card(
                onClick = { showScanDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("digital_membership_card"),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(CostcoNavy, Color(0xFF112233))
                            )
                        )
                        .padding(18.dp)
                ) {
                    // Top Bar: Costco logo & card type
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "COSTCO",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 2.sp,
                                fontFamily = FontFamily.SansSerif
                            )
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(GoldReward.copy(alpha = 0.2f))
                                .border(1.dp, GoldReward, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = membership.memberType.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = GoldReward,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // QR/Barcode representation
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(12.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Represent a barcode with lines
                            Row(
                                modifier = Modifier
                                    .width(180.dp)
                                    .height(50.dp)
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                repeat(28) { index ->
                                    val barWidth = if (index % 3 == 0) 6.dp else if (index % 2 == 0) 3.dp else 1.dp
                                    val barColor = if (index % 5 == 1) Color.White else Color.Black
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .width(barWidth)
                                            .background(barColor)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                }
                            }
                            Text(
                                text = membership.memberNumber,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.Black,
                                    letterSpacing = 1.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Cardholder details
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "MEMBER NAME",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            )
                            Text(
                                text = membership.memberName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "STATUS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(Color(0xFF4CAF50), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = membership.status.uppercase(),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- TAP PROMPT ---
        item {
            Text(
                text = "Tap membership card above to expand for scanner",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        // --- EXECUTIVE MEMBERSHIP REWARDS ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("executive_rewards_card"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, GoldReward.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CardGiftcard,
                                contentDescription = "Savings",
                                tint = GoldReward,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "2% Annual Reward",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Text(
                            text = "ESTIMATED SAVINGS",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextGrey)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "Accumulated Reward:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextGrey
                            )
                            Text(
                                text = String.format("$%.2f", membership.executiveSavings),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = CostcoNavy
                                )
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFFFEE58).copy(alpha = 0.3f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Max Reward: $1,250",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = CostcoNavy,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Progress bar
                    val maxPossibleReward = 1250.0
                    val progress = (membership.executiveSavings / maxPossibleReward).coerceIn(0.0, 1.0).toFloat()
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = GoldReward,
                        trackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Estimate based on qualifying Costco purchases. Your reward certificate is mailed approximately 2 months prior to your renewal.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGrey,
                        lineHeight = 14.sp
                    )
                }
            }
        }

        // --- ACCOUNT METADATA & ACTIONS ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "MEMBERSHIP DETAILS",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = CostcoRed,
                            letterSpacing = 1.sp
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Renewal Date", style = MaterialTheme.typography.bodyMedium, color = TextGrey)
                        Text(
                            text = membership.renewalDate,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Divider(color = BorderLight.copy(alpha = 0.5f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Household Member", style = MaterialTheme.typography.bodyMedium, color = TextGrey)
                        Text(
                            text = membership.householdMember,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Divider(color = BorderLight.copy(alpha = 0.5f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Cardholder Profile", style = MaterialTheme.typography.bodyMedium, color = TextGrey)
                            Text(
                                text = "Name: ${membership.memberName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextGrey
                            )
                        }
                        Button(
                            onClick = {
                                tempName = membership.memberName
                                showEditDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CostcoBlue),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit Name", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    // --- DIALOGS ---

    // 1. Digital Membership Scan dialog
    if (showScanDialog) {
        Dialog(onDismissRequest = { showScanDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Costco Digital Card",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = CostcoNavy
                        )
                    )

                    Text(
                        text = "Present this barcode to the cashier or scan at the gas pump or food court terminal.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = TextGrey
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // QR Code simulation
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .border(1.dp, CostcoNavy.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Drawing a nice simulated QR code pattern
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            repeat(8) { row ->
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    repeat(8) { col ->
                                        val isFilled = (row == 0 && col < 3) || (row < 3 && col == 0) ||
                                                (row == 7 && col > 4) || (row > 4 && col == 7) ||
                                                (row == 0 && col == 7) || (row == 7 && col == 0) ||
                                                ((row + col) % 3 == 0 && row in 2..5 && col in 2..5)
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .background(if (isFilled) CostcoNavy else Color.White)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Text(
                        text = membership.memberNumber,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                    )

                    Button(
                        onClick = { showScanDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = CostcoRed),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Close Card")
                    }
                }
            }
        }
    }

    // 2. Edit Membership Profile Name dialog
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Member Profile") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Update primary cardholder's name:")
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("Cardholder Name") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateMembershipName(tempName)
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CostcoRed)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

val TextGrey = Color(0xFF727A82)
val BorderLight = Color(0xFFE1E5EB)
