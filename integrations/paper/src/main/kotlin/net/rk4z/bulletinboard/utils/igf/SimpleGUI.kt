package net.rk4z.bulletinboard.utils.igf

import org.bukkit.entity.Player

class SimpleGUI(
    player: Player
) : InventoryGUI(player) {
    override fun build(): InventoryGUI {
        create()
        applyBackground()
        displayItems()
        return this
    }
}