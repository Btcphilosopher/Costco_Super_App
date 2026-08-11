package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class CostcoRepository(
    private val shoppingItemDao: ShoppingItemDao,
    private val costcoOrderDao: CostcoOrderDao,
    private val savedMembershipDao: SavedMembershipDao
) {
    val allShoppingItems: Flow<List<ShoppingItem>> = shoppingItemDao.getAllItems()
    val allOrders: Flow<List<CostcoOrder>> = costcoOrderDao.getAllOrders()
    val membership: Flow<SavedMembership?> = savedMembershipDao.getMembership()

    suspend fun insertShoppingItem(item: ShoppingItem) {
        shoppingItemDao.insertItem(item)
    }

    suspend fun updateShoppingItem(item: ShoppingItem) {
        shoppingItemDao.updateItem(item)
    }

    suspend fun deleteShoppingItem(item: ShoppingItem) {
        shoppingItemDao.deleteItem(item)
    }

    suspend fun clearShoppingList() {
        shoppingItemDao.clearAll()
    }

    suspend fun insertOrder(order: CostcoOrder) {
        costcoOrderDao.insertOrder(order)
    }

    suspend fun saveMembership(membership: SavedMembership) {
        savedMembershipDao.insertMembership(membership)
    }

    suspend fun prepopulateIfEmpty() {
        // Prepopulate Membership if not present
        val existingMembership = savedMembershipDao.getMembership().firstOrNull()
        if (existingMembership == null) {
            savedMembershipDao.insertMembership(
                SavedMembership(
                    id = 1,
                    memberName = "Tom Harrison",
                    memberNumber = "111928374650",
                    memberType = "Executive Member",
                    renewalDate = "December 31, 2026",
                    status = "Active",
                    executiveSavings = 143.50,
                    householdMember = "Sarah Harrison"
                )
            )
        }

        // Prepopulate Orders if not present
        val existingOrders = costcoOrderDao.getAllOrders().firstOrNull()
        if (existingOrders.isNullOrEmpty()) {
            costcoOrderDao.insertOrder(
                CostcoOrder(
                    orderId = "987654321",
                    title = "75\" Class - QLED Series TV",
                    status = "Delivered",
                    date = "August 09, 2026",
                    totalAmount = 899.99,
                    trackingStatus = "Delivered",
                    itemsCount = 1
                )
            )
            costcoOrderDao.insertOrder(
                CostcoOrder(
                    orderId = "987654320",
                    title = "Kirkland Paper Towels, Toilet Paper & Trash Bags Bulk",
                    status = "Arriving Friday",
                    date = "August 10, 2026",
                    totalAmount = 74.50,
                    trackingStatus = "In Transit",
                    itemsCount = 3
                )
            )
            costcoOrderDao.insertOrder(
                CostcoOrder(
                    orderId = "987654319",
                    title = "Thomasville Fabric Sectional Sofa",
                    status = "Scheduled delivery",
                    date = "August 08, 2026",
                    totalAmount = 1499.99,
                    trackingStatus = "Processing",
                    itemsCount = 1
                )
            )
        }

        // Prepopulate shopping items if not present
        val existingItems = shoppingItemDao.getAllItems().firstOrNull()
        if (existingItems.isNullOrEmpty()) {
            shoppingItemDao.insertItem(
                ShoppingItem(
                    name = "Kirkland Signature Paper Towels",
                    quantity = 1,
                    price = 24.99,
                    aisle = "Aisle 17",
                    category = "Kirkland Signature",
                    isChecked = false,
                    packSize = "30 Rolls",
                    isKirkland = true
                )
            )
            shoppingItemDao.insertItem(
                ShoppingItem(
                    name = "Kirkland Signature Bath Tissue",
                    quantity = 2,
                    price = 22.99,
                    aisle = "Aisle 18",
                    category = "Kirkland Signature",
                    isChecked = true,
                    packSize = "30 Rolls",
                    isKirkland = true
                )
            )
            shoppingItemDao.insertItem(
                ShoppingItem(
                    name = "Organic Strawberries Bulk Pack",
                    quantity = 1,
                    price = 8.49,
                    aisle = "Aisle A (Produce)",
                    category = "Groceries",
                    isChecked = false,
                    packSize = "4 lbs",
                    isKirkland = false
                )
            )
        }
    }
}
