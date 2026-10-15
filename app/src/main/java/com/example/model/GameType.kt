package com.example.model

enum class GameType(
    val title: String,
    val subtitle: String,
    val minPoints: Int,
    val maxPoints: Int,
    val badge: String
) {
    SPIN_WHEEL(
        title = "Lucky Spin Wheel",
        subtitle = "Spin for instant points up to 1,000 & 2x multiplier boosts",
        minPoints = 50,
        maxPoints = 1000,
        badge = "HOT"
    ),
    SCRATCH_CARD(
        title = "Golden Scratchers",
        subtitle = "Scratch 6 lucky tiles to match 3 identical cash symbols",
        minPoints = 100,
        maxPoints = 1500,
        badge = "POPULAR"
    ),
    MATH_BLITZ(
        title = "Math Blitz Rush",
        subtitle = "30-second speed math challenge. Chain combos for big bonuses",
        minPoints = 80,
        maxPoints = 1200,
        badge = "SKILL"
    ),
    COIN_DROP(
        title = "Coin Drop Reflex",
        subtitle = "Fast reflexes! Catch falling gold coins & cash bags, dodge bombs",
        minPoints = 100,
        maxPoints = 1400,
        badge = "FAST"
    ),
    MEMORY_VAULT(
        title = "Memory Cash Vault",
        subtitle = "Match vault security cards before time runs out",
        minPoints = 120,
        maxPoints = 1000,
        badge = "PUZZLE"
    )
}

enum class PayoutMethod(
    val id: String,
    val displayName: String,
    val iconName: String,
    val fieldLabel: String,
    val placeholder: String
) {
    PAYPAL("paypal", "PayPal", "ic_paypal", "PayPal Email Address", "yourname@example.com"),
    VENMO("venmo", "Venmo", "ic_venmo", "Venmo @Handle", "@YourVenmoTag"),
    CASHAPP("cashapp", "Cash App", "ic_cashapp", "Cash App \$Cashtag", "\$YourCashtag"),
    BANK("bank", "Direct Bank Deposit", "ic_bank", "Routing & Account Number", "Routing: 9 digits / Acct: 10 digits"),
    AMAZON("amazon", "Amazon Gift Card", "ic_amazon", "Delivery Email Address", "recipient@example.com"),
    USDC("usdc", "Crypto USDC (Base / Polygon)", "ic_crypto", "USDC Wallet Address", "0x...")
}

data class LeaderboardItem(
    val rank: Int,
    val username: String,
    val avatarEmoji: String,
    val totalPoints: Int,
    val cashedOutDollars: Double,
    val isUser: Boolean = false
)
