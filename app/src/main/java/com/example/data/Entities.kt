package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_items")
data class ShoppingItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val quantity: Int = 1,
    val price: Double,
    val aisle: String = "Aisle 1",
    val category: String,
    val isChecked: Boolean = false,
    val warehouseId: String = "W-01",
    val packSize: String = "1 Pack",
    val isKirkland: Boolean = false
)

@Entity(tableName = "costco_orders")
data class CostcoOrder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val orderId: String,
    val title: String,
    val status: String, // "Delivered", "Arriving Friday", "Scheduled delivery"
    val date: String,
    val totalAmount: Double,
    val trackingStatus: String, // "Shipped", "In Transit", "Delivered"
    val itemsCount: Int
)

@Entity(tableName = "saved_memberships")
data class SavedMembership(
    @PrimaryKey val id: Int = 1,
    val memberName: String = "Tom",
    val memberNumber: String = "111928374650",
    val memberType: String = "Executive Member", // "Executive" or "Gold Star"
    val renewalDate: String = "12/31/2026",
    val status: String = "Active",
    val executiveSavings: Double = 143.50,
    val householdMember: String = "Sarah"
)
