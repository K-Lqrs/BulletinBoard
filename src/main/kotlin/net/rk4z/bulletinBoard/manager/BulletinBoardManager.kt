package net.rk4z.bulletinBoard.manager

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.rk4z.bulletinBoard.BulletinBoard
import net.rk4z.bulletinBoard.BulletinBoard.Companion.namespacedKey
import net.rk4z.bulletinBoard.util.JsonUtil
import net.rk4z.bulletinBoard.util.Post
import net.rk4z.bulletinBoard.util.PostDraft
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
import org.bukkit.persistence.PersistentDataType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Suppress("unused", "MemberVisibilityCanBePrivate", "DuplicatedCode")
class BulletinBoardManager : Listener {
    private val pendingTitleInputs = ConcurrentHashMap<UUID, String>()
    private val pendingDrafts = ConcurrentHashMap<UUID, PostDraft>()
    private val pendingConfirmations = ConcurrentHashMap<UUID, String>()
    private val pendingPreview = ConcurrentHashMap<UUID, Pair<Component, Component>>()

    private fun createCustomItem(material: Material, name: Component, customModelData: Int? = null, customId: String? = null): ItemStack {
        val item = ItemStack(material)
        val meta: ItemMeta = item.itemMeta
        meta.displayName(name)
        if (customModelData != null) {
            meta.setCustomModelData(customModelData)
        }
        if (customId != null) {
            meta.persistentDataContainer.set(namespacedKey, PersistentDataType.STRING, customId)
        }
        item.itemMeta = meta
        return item
    }

    private fun setGlassPane(board: Inventory, slots: IntRange) {
        val glassPane = ItemStack(Material.BLACK_STAINED_GLASS_PANE)
        val meta: ItemMeta = glassPane.itemMeta
        meta.displayName(Component.text(""))
        glassPane.itemMeta = meta
        for (i in slots) {
            board.setItem(i, glassPane)
        }
    }

    private fun setGlassPane(board: Inventory, slots: List<Int>) {
        val glassPane = ItemStack(Material.BLACK_STAINED_GLASS_PANE)
        val meta: ItemMeta = glassPane.itemMeta
        meta.displayName(Component.text(""))
        glassPane.itemMeta = meta
        for (i in slots) {
            board.setItem(i, glassPane)
        }
    }

    fun openMainBoard(player: Player) {
        val mainBoard: Inventory = Bukkit.createInventory(null, 27, Component.text("Main Board"))

        setGlassPane(mainBoard, 0..8)
        setGlassPane(mainBoard, 18..26)

        setGlassPane(mainBoard, listOf(9, 10, 12, 14, 16, 17))

        mainBoard.setItem(11, createCustomItem(Material.WRITABLE_BOOK, Component.text("New Post"), customId = "new_post"))
        mainBoard.setItem(13, createCustomItem(Material.BOOK, Component.text("All Posts"), customId = "all_posts"))
        mainBoard.setItem(15, createCustomItem(Material.ENCHANTED_BOOK, Component.text("My Posts"), customId = "my_posts"))

        player.openInventory(mainBoard)
    }

    fun openPostEditor(player: Player) {
        val postEditor: Inventory = Bukkit.createInventory(null, 27, Component.text("Post Editor"))

        setGlassPane(postEditor, 0..8)
        setGlassPane(postEditor, 18..26)

        val draft = pendingDrafts.getOrDefault(player.uniqueId, PostDraft())

        postEditor.setItem(11, createCustomItem(Material.PAPER, draft.title, customId = "post_title"))
        postEditor.setItem(15, createCustomItem(Material.BOOK, draft.content, customId = "post_content"))

        postEditor.setItem(18, createCustomItem(Material.RED_WOOL, Component.text("Cancel"), customId = "cancel_post"))
        postEditor.setItem(26, createCustomItem(Material.GREEN_WOOL, Component.text("Save Post"), customId = "save_post"))
        postEditor.setItem(22, createCustomItem(Material.BARRIER, Component.text("Back"), customId = "back_button"))

        player.openInventory(postEditor)
    }

    fun openMyPosts(player: Player, page: Int = 0) {
        val data = JsonUtil.loadFromFile(BulletinBoard.instance.dataFile)
        val playerData = data.players.find { it.uuid == player.uniqueId } ?: return
        val posts = playerData.posts.mapNotNull { postId -> data.posts.find { it.id == postId } }

        openPostsInventory(player, "My Posts", posts, page)
    }

    fun openAllPosts(player: Player, page: Int = 0) {
        val data = JsonUtil.loadFromFile(BulletinBoard.instance.dataFile)
        val posts = data.posts

        openPostsInventory(player, "All Posts", posts, page)
    }

    private fun openPostsInventory(player: Player, title: String, posts: List<Post>, page: Int) {
        val itemsPerPage = 5 // 真ん中の行に一マスおきに配置する最大数
        val totalPages = (posts.size + itemsPerPage - 1) / itemsPerPage
        val currentPage = page.coerceIn(0, totalPages - 1)
        val startIndex = currentPage * itemsPerPage
        val endIndex = (startIndex + itemsPerPage).coerceAtMost(posts.size)

        val inventory: Inventory = Bukkit.createInventory(null, 27, Component.text(title))

        setGlassPane(inventory, 0..26)

        val middleRowSlots = listOf(10, 12, 14, 16, 18)

        if (posts.isEmpty()) {
            val noPostsItem = createCustomItem(Material.PAPER, Component.text("No posts available"), customId = "no_posts")
            inventory.setItem(13, noPostsItem)
        } else {
            posts.subList(startIndex, endIndex).forEachIndexed { index, post ->
                val postItem = createCustomItem(Material.WRITTEN_BOOK, post.title, customId = post.id)
                inventory.setItem(middleRowSlots[index], postItem)
            }

            if (currentPage > 0) {
                inventory.setItem(18, createCustomItem(Material.ARROW, Component.text("Previous Page"), customId = "prev_page"))
            }
            if (currentPage < totalPages - 1) {
                inventory.setItem(26, createCustomItem(Material.ARROW, Component.text("Next Page"), customId = "next_page"))
            }

            if (posts.size <= itemsPerPage) {
                if (inventory.getItem(18) == null) {
                    setGlassPane(inventory, listOf(18))
                }
                if (inventory.getItem(26) == null) {
                    setGlassPane(inventory, listOf(26))
                }
            }
        }

        inventory.setItem(22, createCustomItem(Material.BARRIER, Component.text("Back"), customId = "back_button"))

        if (title == "My Posts") {
            inventory.setItem(24, createCustomItem(Material.LAVA_BUCKET, Component.text("Delete Post"), customId = "delete_post"))
        }

        player.openInventory(inventory)
    }

    fun openDeletePostSelection(player: Player, posts: List<Post>) {
        val inventory: Inventory = Bukkit.createInventory(null, 27, Component.text("Select Post to Delete"))

        setGlassPane(inventory, 0..26)

        val middleRowSlots = listOf(10, 12, 14, 16, 18)

        posts.forEachIndexed { index, post ->
            if (index < middleRowSlots.size) {
                val postItem = createCustomItem(Material.WRITTEN_BOOK, post.title, customId = post.id)
                inventory.setItem(middleRowSlots[index], postItem)
            }
        }

        inventory.setItem(22, createCustomItem(Material.BARRIER, Component.text("Back"), customId = "back_button"))

        player.openInventory(inventory)
    }

    fun openDeleteConfirmationScreen(player: Player, postId: String) {
        val confirmation: Inventory = Bukkit.createInventory(null, 27, Component.text("Delete Confirmation"))

        setGlassPane(confirmation, 0..8)
        setGlassPane(confirmation, 18..26)

        confirmation.setItem(12, createCustomItem(Material.GREEN_WOOL, Component.text("Yes"), customId = "delete_confirm_yes:$postId"))
        confirmation.setItem(14, createCustomItem(Material.YELLOW_WOOL, Component.text("No"), customId = "delete_confirm_no"))

        player.openInventory(confirmation)
    }

    fun openConfirmationScreen(player: Player, type: String) {
        val confirmation: Inventory = Bukkit.createInventory(null, 27, Component.text("Confirmation"))

        setGlassPane(confirmation, 0..8)
        setGlassPane(confirmation, 18..26)

        confirmation.setItem(12, createCustomItem(Material.GREEN_WOOL, Component.text("Yes"), customId = "confirm_yes"))
        confirmation.setItem(14, createCustomItem(Material.YELLOW_WOOL, Component.text("No"), customId = "confirm_no"))
        if (type == "submit") {
            confirmation.setItem(15, createCustomItem(Material.BLUE_WOOL, Component.text("Preview"), customId = "confirm_preview"))
        }

        pendingConfirmations[player.uniqueId] = type
        player.openInventory(confirmation)
    }

    fun openPreview(player: Player, title: Component, content: Component) {
        player.sendMessage("Preview of your post:")
        player.sendMessage(Component.text("Title: ").append(title))
        player.sendMessage(Component.text("Content: ").append(content))
        player.sendMessage(Component.text("Type /bb previewclose to close the preview and continue."))
    }

    fun closePreview(player: Player) {
        val (_, _) = pendingPreview[player.uniqueId] ?: return
        pendingPreview.remove(player.uniqueId)
        Bukkit.getScheduler().runTask(BulletinBoard.instance, Runnable {
            openConfirmationScreen(player, "submit")
        })
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val clickedItem = event.currentItem ?: return
        val player = event.whoClicked as Player
        val inventoryTitle = event.view.title()
        val customId = clickedItem.itemMeta.persistentDataContainer.get(namespacedKey, PersistentDataType.STRING)

        if (clickedItem.type == Material.BLACK_STAINED_GLASS_PANE) {
            event.isCancelled = true
            return
        }

        if (customId == "no_posts") {
            event.isCancelled = true
            return
        }

        when (inventoryTitle) {
            Component.text("Main Board") -> {
                event.isCancelled = true
                when (customId) {
                    "new_post" -> openPostEditor(player)
                    "all_posts" -> openAllPosts(player)
                    "my_posts" -> openMyPosts(player)
                }
            }

            Component.text("All Posts"), Component.text("My Posts") -> {
                event.isCancelled = true
                val currentPage = event.view.getItem(22)?.itemMeta?.lore()?.firstOrNull()?.toString()?.toIntOrNull() ?: 0

                when (customId) {
                    "prev_page" -> {
                        if (inventoryTitle == Component.text("All Posts")) {
                            openAllPosts(player, currentPage - 1)
                        } else {
                            openMyPosts(player, currentPage - 1)
                        }
                    }
                    "next_page" -> {
                        if (inventoryTitle == Component.text("All Posts")) {
                            openAllPosts(player, currentPage + 1)
                        } else {
                            openMyPosts(player, currentPage + 1)
                        }
                    }
                    "back_button" -> openMainBoard(player)
                    "delete_post" -> {
                        val data = JsonUtil.loadFromFile(BulletinBoard.instance.dataFile)
                        val playerData = data.players.find { it.uuid == player.uniqueId } ?: return
                        val posts = playerData.posts.mapNotNull { postId -> data.posts.find { it.id == postId } }
                        openDeletePostSelection(player, posts)
                    }
                    else -> {
                        if (customId != null) {
                            val data = JsonUtil.loadFromFile(BulletinBoard.instance.dataFile)
                            val post = data.posts.find { it.id == customId }
                            if (post != null) {
                                player.closeInventory()
                                displayPost(player, post)
                            }
                        }
                    }
                }
            }

            Component.text("Post Editor") -> {
                event.isCancelled = true
                when (customId) {
                    "post_title" -> {
                        player.closeInventory()
                        pendingTitleInputs[player.uniqueId] = "title"
                        player.sendMessage("Please enter the title for your post:")
                    }
                    "post_content" -> {
                        player.closeInventory()
                        pendingTitleInputs[player.uniqueId] = "content"
                        player.sendMessage("Please enter the content for your post:")
                    }
                    "save_post" -> {
                        val draft = pendingDrafts[player.uniqueId] ?: PostDraft()
                        pendingPreview[player.uniqueId] = Pair(draft.title, draft.content)
                        openConfirmationScreen(player, "submit")
                    }
                    "cancel_post" -> openConfirmationScreen(player, "cancel")
                }
            }

            Component.text("Confirmation") -> {
                event.isCancelled = true
                when (customId) {
                    "confirm_yes" -> {
                        when (pendingConfirmations[player.uniqueId]) {
                            "submit" -> {
                                val draft = pendingDrafts[player.uniqueId] ?: PostDraft()

                                val formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd_HH:mm:ss")
                                val currentDate = LocalDateTime.now().format(formatter)

                                val newPost = Post(
                                    id = UUID.randomUUID().toString(),
                                    title = draft.title,
                                    author = player.uniqueId,
                                    content = draft.content,
                                    date = currentDate
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

                                player.sendMessage(Component.text("Post Saved: Title: ").append(draft.title).append(Component.text(", Content: ")).append(draft.content))
                                pendingDrafts.remove(player.uniqueId)
                            }
                            "cancel" -> player.sendMessage("Action cancelled.")
                        }
                        player.closeInventory()
                        pendingConfirmations.remove(player.uniqueId)
                    }
                    "confirm_no" -> {
                        player.closeInventory()
                        pendingConfirmations.remove(player.uniqueId)
                        openPostEditor(player)
                    }
                    "confirm_preview" -> {
                        val draft = pendingDrafts[player.uniqueId] ?: PostDraft()
                        player.closeInventory()
                        openPreview(player, draft.title, draft.content)
                    }
                }
            }

            Component.text("Delete Confirmation") -> {
                event.isCancelled = true
                when (customId?.split(":")?.get(0)) {
                    "delete_confirm_yes" -> {
                        val postId = customId.split(":")[1]
                        val data = JsonUtil.loadFromFile(BulletinBoard.instance.dataFile)
                        val updatedPosts = data.posts.filterNot { it.id == postId }
                        val updatedPlayers = data.players.map {
                            if (it.uuid == player.uniqueId) {
                                it.copy(posts = it.posts.filterNot { id -> id == postId })
                            } else {
                                it
                            }
                        }

                        val updatedData = data.copy(posts = updatedPosts, players = updatedPlayers)
                        JsonUtil.saveToFile(updatedData, BulletinBoard.instance.dataFile)

                        player.sendMessage("Post deleted successfully.")
                        player.closeInventory()
                        openMyPosts(player)
                    }
                    "delete_confirm_no" -> {
                        player.closeInventory()
                        openMyPosts(player)
                    }
                }
            }

            Component.text("Select Post to Delete") -> {
                event.isCancelled = true
                if (customId != null) {
                    openDeleteConfirmationScreen(player, customId)
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
            val draft = pendingDrafts.getOrDefault(player.uniqueId, PostDraft())

            val updatedDraft = if (inputType == "title") {
                draft.copy(title = Component.text(input))
            } else {
                draft.copy(content = Component.text(input))
            }

            pendingDrafts[player.uniqueId] = updatedDraft

            Bukkit.getScheduler().runTask(BulletinBoard.instance, Runnable {
                val postEditor = Bukkit.createInventory(null, 27, Component.text("Post Editor"))
                setGlassPane(postEditor, 0..8)
                setGlassPane(postEditor, 18..26)
                postEditor.setItem(11, createCustomItem(Material.PAPER, updatedDraft.title, customId = "post_title"))
                postEditor.setItem(15, createCustomItem(Material.BOOK, updatedDraft.content, customId = "post_content"))
                postEditor.setItem(18, createCustomItem(Material.RED_WOOL, Component.text("Cancel"), customId = "cancel_post"))
                postEditor.setItem(26, createCustomItem(Material.GREEN_WOOL, Component.text("Save Post"), customId = "save_post"))
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

    fun displayPost(player: Player, post: Post) {
        val titleComponent = Component.text("Title: ", NamedTextColor.GOLD, TextDecoration.BOLD).append(post.title)
        val contentComponent = Component.text("Content: ", NamedTextColor.WHITE).append(post.content)
        val dateComponent = Component.text("Date: ${post.date}", NamedTextColor.GRAY)

        player.closeInventory()

        player.sendMessage(Component.text("---------------------------------", NamedTextColor.DARK_GRAY))
        player.sendMessage(titleComponent)
        player.sendMessage(contentComponent)
        player.sendMessage(dateComponent)
        player.sendMessage(Component.text("---------------------------------", NamedTextColor.DARK_GRAY))
    }

}
