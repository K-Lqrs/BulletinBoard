package net.rk4z.bulletinBoard.manager

import net.kyori.adventure.text.Component
import net.rk4z.bulletinBoard.BulletinBoard
import net.rk4z.bulletinBoard.util.JsonUtil
import net.rk4z.bulletinBoard.util.Post
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Suppress("unused", "MemberVisibilityCanBePrivate")
class BulletinBoardManager : Listener {
    private val pendingTitleInputs = ConcurrentHashMap<UUID, String>()
    private val pendingConfirmations = ConcurrentHashMap<UUID, String>()
    private val pendingPreview = ConcurrentHashMap<UUID, Pair<String, String>>()


    private fun createCustomItem(material: Material, name: Component, customModelData: Int? = null): ItemStack {
        val item = ItemStack(material)
        val meta: ItemMeta = item.itemMeta
        meta.displayName(name)
        if (customModelData != null) {
            meta.setCustomModelData(customModelData)
        }
        item.itemMeta = meta
        return item
    }

    private fun setGlassPane(board: Inventory, slots: IntRange) {
        val glassPane = ItemStack(Material.BLACK_STAINED_GLASS_PANE)
        for (i in slots) {
            board.setItem(i, glassPane)
        }
    }

    private fun setGlassPane(board: Inventory, slots: List<Int>) {
        val glassPane = ItemStack(Material.BLACK_STAINED_GLASS_PANE)
        for (i in slots) {
            board.setItem(i, glassPane)
        }
    }

    fun openMainBoard(player: Player) {
        val mainBoard: Inventory = Bukkit.createInventory(null, 27, Component.text("Main Board"))

        setGlassPane(mainBoard, 0..8)
        setGlassPane(mainBoard, 18..26)

        setGlassPane(mainBoard, listOf(9, 10, 12, 14, 16, 17))

        mainBoard.setItem(11, createCustomItem(Material.WRITABLE_BOOK, Component.text("New Post")))
        mainBoard.setItem(13, createCustomItem(Material.BOOK, Component.text("All Posts")))
        mainBoard.setItem(15, createCustomItem(Material.ENCHANTED_BOOK, Component.text("My Posts")))

        player.openInventory(mainBoard)
    }

    fun openPostEditor(player: Player) {
        val postEditor: Inventory = Bukkit.createInventory(null, 27, Component.text("Post Editor"))

        setGlassPane(postEditor, 0..8)
        setGlassPane(postEditor, 18..26)

        // Set title, content, and tag in the middle rows
        postEditor.setItem(11, createCustomItem(Material.PAPER, Component.text("Title")))
        postEditor.setItem(13, createCustomItem(Material.BOOK, Component.text("Content")))
        postEditor.setItem(15, createCustomItem(Material.NAME_TAG, Component.text("Tag")))

        // Set cancel and submit buttons at the corners
        postEditor.setItem(18, createCustomItem(Material.RED_WOOL, Component.text("Cancel")))
        postEditor.setItem(26, createCustomItem(Material.GREEN_WOOL, Component.text("Save Post")))

        player.openInventory(postEditor)
    }

    fun openMyPosts(player: Player) {
        val data = JsonUtil.loadFromFile(BulletinBoard.instance.dataFile)
        val myPosts: Inventory = Bukkit.createInventory(null, 54, Component.text("My Posts"))

        val playerData = data.players.find { it.uuid == player.uniqueId }

        playerData?.posts?.forEach { postId ->
            val post = data.posts.find { it.id == postId }
            if (post != null) {
                val postItem = createCustomItem(Material.WRITTEN_BOOK, Component.text(post.title))
                setItemInVerticalList(myPosts, 0, postItem)
            }
        }

        player.openInventory(myPosts)
    }

    fun openAllPosts(player: Player) {
        val data = JsonUtil.loadFromFile(BulletinBoard.instance.dataFile)
        val allPosts: Inventory = Bukkit.createInventory(null, 54, Component.text("All Posts"))

        data.posts.forEach { post ->
            val postItem = createCustomItem(Material.WRITTEN_BOOK, Component.text(post.title))
            setItemInVerticalList(allPosts, 0, postItem)
        }

        player.openInventory(allPosts)
    }

    fun openConfirmationScreen(player: Player, type: String) {
        val confirmation: Inventory = Bukkit.createInventory(null, 27, Component.text("Confirmation"))

        setGlassPane(confirmation, 0..8)
        setGlassPane(confirmation, 18..26)

        confirmation.setItem(11, createCustomItem(Material.GREEN_WOOL, Component.text("Yes")))
        confirmation.setItem(13, createCustomItem(Material.YELLOW_WOOL, Component.text("No")))
        if (type == "submit") {
            confirmation.setItem(15, createCustomItem(Material.BLUE_WOOL, Component.text("Preview")))
        }

        pendingConfirmations[player.uniqueId] = type
        player.openInventory(confirmation)
    }

    fun openPreview(player: Player, title: String, content: String) {
        player.sendMessage("Preview of your post:")
        player.sendMessage("Title: $title")
        player.sendMessage("Content: $content")
        player.sendMessage("Type /bb previewclose to close the preview and continue.")
    }

    fun closePreview(player: Player) {
        player.sendMessage("Closing preview...")
        val (title, content) = pendingPreview[player.uniqueId] ?: return
        Bukkit.getScheduler().runTask(BulletinBoard.instance, Runnable {
            val postEditor = Bukkit.createInventory(null, 27, Component.text("Post Editor"))
            setGlassPane(postEditor, 0..8)
            setGlassPane(postEditor, 18..26)
            postEditor.setItem(11, createCustomItem(Material.PAPER, Component.text(title)))
            postEditor.setItem(13, createCustomItem(Material.BOOK, Component.text(content)))
            postEditor.setItem(15, createCustomItem(Material.NAME_TAG, Component.text("Tag")))
            postEditor.setItem(18, createCustomItem(Material.RED_WOOL, Component.text("Cancel")))
            postEditor.setItem(26, createCustomItem(Material.GREEN_WOOL, Component.text("Save Post")))
            player.openInventory(postEditor)
        })
        pendingPreview.remove(player.uniqueId)
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val clickedItem = event.currentItem ?: return
        val player = event.whoClicked as Player
        val inventoryTitle = event.view.title()

        if (clickedItem.type == Material.BLACK_STAINED_GLASS_PANE) {
            event.isCancelled = true
            return
        }

        when (inventoryTitle) {
            Component.text("Main Board") -> {
                event.isCancelled = true

                if (clickedItem.hasItemMeta()) {
                    val itemMeta = clickedItem.itemMeta
                    when (itemMeta.displayName()) {
                        Component.text("New Post") -> {
                            event.isCancelled = true
                            openPostEditor(player)
                        }

                        Component.text("All Posts") -> {
                            event.isCancelled = true
                            openAllPosts(player)
                        }

                        Component.text("My Posts") -> {
                            event.isCancelled = true
                            openMyPosts(player)
                        }
                    }
                }
            }

            Component.text("Post Editor") -> {
                event.isCancelled = true

                if (clickedItem.hasItemMeta()) {
                    val itemMeta = clickedItem.itemMeta
                    when (itemMeta.displayName()) {
                        Component.text("Title") -> {
                            player.closeInventory()
                            pendingTitleInputs[player.uniqueId] = "title"
                            player.sendMessage("Please enter the title for your post:")
                        }

                        Component.text("Content") -> {
                            player.closeInventory()
                            pendingTitleInputs[player.uniqueId] = "content"
                            player.sendMessage("Please enter the content for your post:")
                        }

                        Component.text("Save Post") -> {
                            event.isCancelled = true
                            openConfirmationScreen(player, "submit")
                        }

                        Component.text("Cancel") -> {
                            event.isCancelled = true
                            openConfirmationScreen(player, "cancel")
                        }
                    }
                }
            }

            Component.text("Confirmation") -> {
                event.isCancelled = true

                if (clickedItem.hasItemMeta()) {
                    val itemMeta = clickedItem.itemMeta
                    when (itemMeta.displayName()) {
                        Component.text("Yes") -> {
                            event.isCancelled = true
                            when (pendingConfirmations[player.uniqueId]) {
                                "submit" -> {
                                    val titleItem = event.view.getItem(11)
                                    val contentItem = event.view.getItem(13)

                                    val title = titleItem?.itemMeta?.displayName()?.toString() ?: "No Title"
                                    val content = contentItem?.itemMeta?.displayName()?.toString() ?: "No Content"

                                    val newPost = Post(
                                        id = UUID.randomUUID().toString(),
                                        title = title,
                                        author = player.uniqueId,
                                        content = content,
                                        date = System.currentTimeMillis()
                                    )

                                    val data = JsonUtil.loadFromFile(BulletinBoard.instance.dataFile)

                                    val updatedPosts = data.posts + newPost
                                    val updatedPlayers = data.players.map {
                                        if (it.uuid == player.uniqueId) {
                                            it.copy(posts = it.posts + newPost.id)
                                        } else {
                                            it
                                        }
                                    }

                                    val updatedData = data.copy(posts = updatedPosts, players = updatedPlayers)
                                    JsonUtil.saveToFile(updatedData, BulletinBoard.instance.dataFile)

                                    player.sendMessage("Post Saved: Title: $title, Content: $content")
                                }
                                "cancel" -> {
                                    player.sendMessage("Action cancelled.")
                                }
                            }
                            player.closeInventory()
                            pendingConfirmations.remove(player.uniqueId)
                        }

                        Component.text("No") -> {
                            event.isCancelled = true
                            player.closeInventory()
                            pendingConfirmations.remove(player.uniqueId)
                            openPostEditor(player)
                        }

                        Component.text("Preview") -> {
                            event.isCancelled = true
                            val titleItem = event.view.getItem(11)
                            val contentItem = event.view.getItem(13)

                            val title = titleItem?.itemMeta?.displayName()?.toString() ?: "No Title"
                            val content = contentItem?.itemMeta?.displayName()?.toString() ?: "No Content"

                            pendingPreview[player.uniqueId] = Pair(title, content)
                            player.closeInventory()
                            openPreview(player, title, content)
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    fun onPlayerChat(event: AsyncPlayerChatEvent) {
        val player = event.player

        if (pendingTitleInputs.containsKey(player.uniqueId)) {
            event.isCancelled = true
            val inputType = pendingTitleInputs[player.uniqueId]
            val input = event.message
            Bukkit.getScheduler().runTask(BulletinBoard.instance, Runnable {
                val postEditor = Bukkit.createInventory(null, 27, Component.text("Post Editor"))
                setGlassPane(postEditor, 0..8)
                setGlassPane(postEditor, 18..26)
                if (inputType == "title") {
                    postEditor.setItem(11, createCustomItem(Material.PAPER, Component.text(input)))
                    postEditor.setItem(13, createCustomItem(Material.BOOK, Component.text("Content")))
                    postEditor.setItem(15, createCustomItem(Material.NAME_TAG, Component.text("Tag")))
                } else if (inputType == "content") {
                    postEditor.setItem(11, createCustomItem(Material.PAPER, Component.text("Title")))
                    postEditor.setItem(13, createCustomItem(Material.BOOK, Component.text(input)))
                    postEditor.setItem(15, createCustomItem(Material.NAME_TAG, Component.text("Tag")))
                }
                postEditor.setItem(18, createCustomItem(Material.RED_WOOL, Component.text("Cancel")))
                postEditor.setItem(26, createCustomItem(Material.GREEN_WOOL, Component.text("Save Post")))
                player.openInventory(postEditor)
            })
            pendingTitleInputs.remove(player.uniqueId)
            player.sendMessage("Your $inputType has been set to: $input")
        }
    }

    @EventHandler
    fun onPlayerCommandPreprocess(event: PlayerCommandPreprocessEvent) {
        val player = event.player
        val command = event.message

        if (command.equals("/bb previewclose", ignoreCase = true)) {
            event.isCancelled = true
            closePreview(player)
        }
    }

    private fun setItemInVerticalList(inventory: Inventory, startIndex: Int, item: ItemStack) {
        var index = startIndex
        while (index < inventory.size) {
            if (inventory.getItem(index) == null) {
                inventory.setItem(index, item)
                break
            }
            index += 9
        }
    }
}
