package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class Product(
    val id: String,
    val name: String,
    val brand: String,
    val category: String,
    val price: Double,
    val packSize: String,
    val pricePerUnit: String,
    val aisle: String,
    val inStock: Boolean = true,
    val stockCount: Int = 45,
    val description: String = ""
)

data class Warehouse(
    val id: String,
    val name: String,
    val address: String,
    val distance: Double,
    val openingHours: String,
    val status: String, // "OPEN", "CLOSED"
    val fuelRegular: Double,
    val fuelPremium: Double,
    val hasGas: Boolean = true,
    val hasFoodCourt: Boolean = true,
    val hasPharmacy: Boolean = true,
    val hasOptical: Boolean = true,
    val hasTires: Boolean = true,
    val hasHearing: Boolean = true
)

data class FoodMenuItem(
    val id: String,
    val name: String,
    val price: Double,
    val calories: String,
    val imageEmoji: String
)

class CostcoViewModel(private val repository: CostcoRepository) : ViewModel() {

    // --- Static Catalog Data ---
    val products = listOf(
        Product("P-01", "Paper Towels", "Kirkland Signature", "Groceries", 24.99, "30 Rolls", "$0.83 / roll", "Aisle 17", true, 120, "Thick, absorbent, and premium quality bulk rolls."),
        Product("P-02", "Bath Tissue", "Kirkland Signature", "Groceries", 22.99, "30 Rolls", "$0.77 / roll", "Aisle 18", true, 150, "Soft, strong, and highly rated 2-ply toilet paper."),
        Product("P-03", "Organic Strawberries Bulk", "Organic Fresh", "Groceries", 8.49, "4 lbs", "$2.12 / lb", "Produce A", true, 30, "Juicy and freshly picked organic strawberries."),
        Product("P-04", "Organic Whole Milk", "Kirkland Signature", "Groceries", 11.49, "3-Pack (1/2 gal)", "$3.83 / unit", "Dairy Fridge", true, 45, "USDA certified organic fresh pasteurized milk."),
        Product("P-05", "Rotisserie Chicken (Hot)", "Kirkland Signature", "Groceries", 4.99, "3 lbs", "$1.66 / lb", "Deli Rotisserie", true, 8, "Our famous fully cooked rotisserie chicken, kept hot."),
        Product("P-06", "75\" Class QLED 4K TV", "Samsung", "Electronics", 899.99, "1 Unit", "$899.99 / unit", "Aisle 2 (Electronics)", true, 12, "Brilliant colors, dual LED backlight, and high-smart features."),
        Product("P-07", "Organic Roasted Cashews", "Kirkland Signature", "Groceries", 14.99, "2.5 lbs", "$6.00 / lb", "Aisle 12", true, 60, "Salted, dry-roasted, crunchy whole cashews."),
        Product("P-08", "Fabric Sectional Sofa", "Thomasville", "Furniture", 1499.99, "1 Unit", "$1499.99 / unit", "Center Court F", true, 4, "Includes matching pillows, soft durable woven grey fabric."),
        Product("P-09", "V15 Cordless Vacuum", "Dyson", "Appliances", 649.99, "1 Unit", "$649.99 / unit", "Aisle 5", true, 15, "Laser reveals microscopic dust, high-torque cleaner head."),
        Product("P-10", "Men's Crewneck Tee", "Kirkland Signature", "Clothing", 19.99, "6-Pack", "$3.33 / shirt", "Clothing Tables", true, 110, "100% heavyweight combed ringspun cotton."),
        Product("P-11", "1 oz Gold Bar (Veriscan)", "PAMP Suisse", "Home", 2450.00, "1 Unit", "$2450.00 / unit", "Vault Cabinet", true, 2, "999.9 fine gold bar, registered and secured in assay card."),
        Product("P-12", "Premium Dog Food", "Kirkland Signature", "Groceries", 45.99, "40 lbs", "$1.15 / lb", "Aisle 24", true, 35, "Chicken, rice, and vegetable formula for active adult dogs.")
    )

    val warehouses = listOf(
        Warehouse("W-01", "Seattle Downtown", "4401 4th Ave S, Seattle, WA 98134", 3.2, "10:00 AM - 8:30 PM", "OPEN", 3.19, 3.79, hasHearing = true),
        Warehouse("W-02", "Bellevue", "1111 116th Ave NE, Bellevue, WA 98004", 8.5, "10:00 AM - 8:30 PM", "OPEN", 3.29, 3.89, hasHearing = false),
        Warehouse("W-03", "Shoreline", "1375 N 205th St, Shoreline, WA 98133", 11.1, "10:00 AM - 8:30 PM", "OPEN", 3.15, 3.75, hasHearing = true)
    )

    val foodCourtMenu = listOf(
        FoodMenuItem("F-01", "Quarter Pound Hot Dog + 20 oz Soda", 1.50, "580 Cal", "🌭"),
        FoodMenuItem("F-02", "18\" Whole Cheese Pizza", 9.95, "4500 Cal", "🍕"),
        FoodMenuItem("F-03", "Cheese Pizza Slice", 1.99, "750 Cal", "🍕"),
        FoodMenuItem("F-04", "Pepperoni Pizza Slice", 1.99, "780 Cal", "🍕"),
        FoodMenuItem("F-05", "Chicken Bake", 3.99, "840 Cal", "🥖"),
        FoodMenuItem("F-06", "Double Chocolate Chunk Cookie", 2.49, "750 Cal", "🍪"),
        FoodMenuItem("F-07", "Fruit Smoothie", 2.99, "290 Cal", "🥤"),
        FoodMenuItem("F-08", "Vanilla Ice Cream Cup", 1.99, "550 Cal", "🍨")
    )

    // --- Selected States ---
    private val _selectedWarehouse = MutableStateFlow(warehouses[0])
    val selectedWarehouse: StateFlow<Warehouse> = _selectedWarehouse.asStateFlow()

    private val _isWarehouseMode = MutableStateFlow(false)
    val isWarehouseMode: StateFlow<Boolean> = _isWarehouseMode.asStateFlow()

    // --- Search State ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredProducts: StateFlow<List<Product>> = _searchQuery
        .combine(flowOf(products)) { query, prodList ->
            if (query.isBlank()) {
                prodList
            } else {
                prodList.filter {
                    it.name.contains(query, ignoreCase = true) ||
                    it.brand.contains(query, ignoreCase = true) ||
                    it.category.contains(query, ignoreCase = true) ||
                    it.aisle.contains(query, ignoreCase = true)
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), products)

    // --- Cart & Checkout State ---
    // Pair of Product to Quantity
    private val _cart = MutableStateFlow<Map<Product, Int>>(emptyMap())
    val cart: StateFlow<Map<Product, Int>> = _cart.asStateFlow()

    val cartTotal: StateFlow<Double> = _cart
        .map { map -> map.entries.sumOf { it.key.price * it.value } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // --- Food Court Order State ---
    private val _foodCart = MutableStateFlow<Map<FoodMenuItem, Int>>(emptyMap())
    val foodCart: StateFlow<Map<FoodMenuItem, Int>> = _foodCart.asStateFlow()

    val foodCartTotal: StateFlow<Double> = _foodCart
        .map { map -> map.entries.sumOf { it.key.price * it.value } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    private val _foodOrderStatus = MutableStateFlow("Idle") // "Idle", "Placing", "Preparing", "Ready"
    val foodOrderStatus: StateFlow<String> = _foodOrderStatus.asStateFlow()

    private val _foodOrderNumber = MutableStateFlow("")
    val foodOrderNumber: StateFlow<String> = _foodOrderNumber.asStateFlow()

    // --- Room Reactive Flows ---
    val shoppingList: StateFlow<List<ShoppingItem>> = repository.allShoppingItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orderHistory: StateFlow<List<CostcoOrder>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val membershipProfile: StateFlow<SavedMembership?> = repository.membership
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        viewModelScope.launch {
            repository.prepopulateIfEmpty()
        }
    }

    // --- Actions & Methods ---

    fun selectWarehouse(warehouse: Warehouse) {
        _selectedWarehouse.value = warehouse
    }

    fun toggleWarehouseMode(enabled: Boolean) {
        _isWarehouseMode.value = enabled
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // --- Cart Management ---
    fun addProductToCart(product: Product) {
        val current = _cart.value.toMutableMap()
        current[product] = (current[product] ?: 0) + 1
        _cart.value = current
    }

    fun removeProductFromCart(product: Product) {
        val current = _cart.value.toMutableMap()
        val count = current[product] ?: 0
        if (count > 1) {
            current[product] = count - 1
        } else {
            current.remove(product)
        }
        _cart.value = current
    }

    fun clearCart() {
        _cart.value = emptyMap()
    }

    // --- Shopping List (Database backed) ---
    fun addItemToShoppingList(product: Product) {
        viewModelScope.launch {
            val existing = shoppingList.value.firstOrNull { it.name.equals(product.name, ignoreCase = true) }
            if (existing != null) {
                repository.insertShoppingItem(
                    existing.copy(quantity = existing.quantity + 1)
                )
            } else {
                repository.insertShoppingItem(
                    ShoppingItem(
                        name = product.name,
                        price = product.price,
                        aisle = product.aisle,
                        category = product.category,
                        packSize = product.packSize,
                        isKirkland = product.brand.equals("Kirkland Signature", ignoreCase = true)
                    )
                )
            }
        }
    }

    fun addManualItemToList(name: String, aisle: String = "Aisle 1") {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.insertShoppingItem(
                ShoppingItem(
                    name = name,
                    price = 9.99,
                    aisle = aisle,
                    category = "Groceries",
                    packSize = "Bulk Pack"
                )
            )
        }
    }

    fun toggleShoppingItemChecked(item: ShoppingItem) {
        viewModelScope.launch {
            repository.updateShoppingItem(item.copy(isChecked = !item.isChecked))
        }
    }

    fun removeShoppingItem(item: ShoppingItem) {
        viewModelScope.launch {
            repository.deleteShoppingItem(item)
        }
    }

    fun clearShoppingList() {
        viewModelScope.launch {
            repository.clearShoppingList()
        }
    }

    // --- Food Court Order Manager ---
    fun addFoodToCart(foodItem: FoodMenuItem) {
        val current = _foodCart.value.toMutableMap()
        current[foodItem] = (current[foodItem] ?: 0) + 1
        _foodCart.value = current
    }

    fun removeFoodFromCart(foodItem: FoodMenuItem) {
        val current = _foodCart.value.toMutableMap()
        val count = current[foodItem] ?: 0
        if (count > 1) {
            current[foodItem] = count - 1
        } else {
            current.remove(foodItem)
        }
        _foodCart.value = current
    }

    fun placeFoodCourtOrder() {
        if (_foodCart.value.isEmpty()) return
        viewModelScope.launch {
            _foodOrderStatus.value = "Placing"
            delay(1500)
            val orderNum = (100..999).random().toString()
            _foodOrderNumber.value = orderNum
            _foodOrderStatus.value = "Preparing"
            _foodCart.value = emptyMap() // Clear cart on place
            
            // Wait 10 seconds to simulate food pickup preparation
            delay(10000)
            _foodOrderStatus.value = "Ready"
        }
    }

    fun resetFoodOrderStatus() {
        _foodOrderStatus.value = "Idle"
        _foodOrderNumber.value = ""
    }

    // --- Order Checkout Simulation ---
    fun simulateCheckout() {
        val cartItems = _cart.value
        if (cartItems.isEmpty()) return
        val total = cartItems.entries.sumOf { it.key.price * it.value }
        val count = cartItems.values.sum()
        val titleSummary = cartItems.entries.joinToString(", ") { "${it.key.brand} ${it.key.name}" }.take(60) + (if (cartItems.size > 1) "..." else "")

        viewModelScope.launch {
            val newOrder = CostcoOrder(
                orderId = (100000000..999999999).random().toString(),
                title = titleSummary,
                status = "Arriving Friday",
                date = "Today",
                totalAmount = total,
                trackingStatus = "Processing",
                itemsCount = count
            )
            repository.insertOrder(newOrder)
            clearCart()

            // Update membership reward savings estimate
            val currentMembership = membershipProfile.value
            if (currentMembership != null) {
                val rewardedAmount = total * 0.02 // 2% reward for Executive
                repository.saveMembership(
                    currentMembership.copy(
                        executiveSavings = currentMembership.executiveSavings + rewardedAmount
                    )
                )
            }
        }
    }

    // --- Update Membership Profile ---
    fun updateMembershipName(newName: String) {
        val current = membershipProfile.value ?: return
        viewModelScope.launch {
            repository.saveMembership(current.copy(memberName = newName))
        }
    }
}

class CostcoViewModelFactory(private val repository: CostcoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CostcoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CostcoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
