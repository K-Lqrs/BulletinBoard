package net.rk4z.bulletinboard.utils

import org.bukkit.Material

fun Map<String, Any>.getNullableString(key: String): String? =
    this[key]?.toString()?.takeIf { it.isNotBlank() }

fun Map<String, Any>.getNullableBoolean(key: String): Boolean? =
    this[key]?.toString()?.takeIf { it.isNotBlank() }?.toBooleanStrictOrNull()

fun Boolean?.isNullOrFalse(): Boolean {
    return this == null || this == false
}

fun Material.toItemStack(amount: Int = 1) = org.bukkit.inventory.ItemStack(this, amount)