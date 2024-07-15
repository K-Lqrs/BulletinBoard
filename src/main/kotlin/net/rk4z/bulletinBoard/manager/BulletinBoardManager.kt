@file:Suppress("DEPRECATION")

package net.rk4z.bulletinBoard.manager

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
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
import org.bukkit.event.inventory.InventoryCloseEvent
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

@Suppress("unused", "MemberVisibilityCanBePrivate", "DuplicatedCode", "DEPRECATION")
class BulletinBoardManager : Listener {
    private val pendingInputs = ConcurrentHashMap<UUID, String>()
    private val pendingDrafts = ConcurrentHashMap<UUID, PostDraft>()
    private val pendingConfirmations = ConcurrentHashMap<UUID, String>()
    private val pendingPreview = ConcurrentHashMap<UUID, Pair<Component, Component>>()
    private val playerPreviewing = ConcurrentHashMap<UUID, Boolean>()
    private val playerOpeningConfirmation = ConcurrentHashMap<UUID, Boolean>()
    private val playerInputting = ConcurrentHashMap<UUID, Boolean>()

    private fun createCustomItem(
        material: Material,
        name: Component,
        customModelData: Int? = null,
        customId: String? = null
    ): ItemStack {
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
        val mainBoard: Inventory = Bukkit.createInventory(null, 27, LanguageManager.getMessage(player, "main_board"))

        setGlassPane(mainBoard, 0..8)
        setGlassPane(mainBoard, 18..26)

        setGlassPane(mainBoard, listOf(9, 10, 12, 14, 16, 17))

        mainBoard.setItem(
            11,
            createCustomItem(
                Material.WRITABLE_BOOK,
                LanguageManager.getMessage(player, "new_post"),
                customId = "new_post"
            )
        )
        mainBoard.setItem(
            13,
            createCustomItem(Material.BOOK, LanguageManager.getMessage(player, "all_posts"), customId = "all_posts")
        )
        mainBoard.setItem(
            15,
            createCustomItem(
                Material.ENCHANTED_BOOK,
                LanguageManager.getMessage(player, "my_posts"),
                customId = "my_posts"
            )
        )

        player.openInventory(mainBoard)
    }

    fun openPostEditor(player: Player)  {
        val postEditor: Inventory = Bukkit.createInventory(null, 27, LanguageManager.getMessage(player, "post_editor"))

        setGlassPane(postEditor, 0..8)
        setGlassPane(postEditor, 18..26)
        setGlassPane(postEditor, listOf(9, 10, 12, 13, 14, 16, 17))

        val draft = pendingDrafts.getOrDefault(player.uniqueId, PostDraft())
        val title = draft.title ?: LanguageManager.getMessage(player, "no_title")
        val content = draft.content ?: LanguageManager.getMessage(player, "no_content")

        postEditor.setItem(11, createCustomItem(Material.PAPER, title, customId = "post_title"))
        postEditor.setItem(15, createCustomItem(Material.BOOK, content, customId = "post_content"))

        postEditor.setItem(
            19,
            createCustomItem(
                Material.RED_WOOL,
                LanguageManager.getMessage(player, "cancel_post"),
                customId = "cancel_post"
            )
        )
        postEditor.setItem(
            25,
            createCustomItem(
                Material.GREEN_WOOL,
                LanguageManager.getMessage(player, "save_post"),
                customId = "save_post"
            )
        )

        player.openInventory(postEditor)
    }

    fun openMyPosts(player: Player, page: Int = 0) {
        val data = JsonUtil.loadFromFile(BulletinBoard.instance.dataFile)
        val playerData = data.players.find { it.uuid == player.uniqueId } ?: return
        val posts = playerData.posts.mapNotNull { postId -> data.posts.find { it.id == postId } }

        openPostsInventory(player, LanguageManager.getContentFromMessage(player, "my_posts"), posts, page)
    }

    fun openAllPosts(player: Player, page: Int = 0) {
        val data = JsonUtil.loadFromFile(BulletinBoard.instance.dataFile)
        val posts = data.posts

        openPostsInventory(player, LanguageManager.getContentFromMessage(player, "all_posts"), posts, page)
    }

    private fun openPostsInventory(player: Player, title: String, posts: List<Post>, page: Int) {
        val itemsPerPage = 4
        val totalPages = (posts.size + itemsPerPage - 1) / itemsPerPage
        val currentPage = page.coerceIn(0, if (totalPages == 0) 0 else totalPages - 1)
        val startIndex = currentPage * itemsPerPage
        val endIndex = (startIndex + itemsPerPage).coerceAtMost(posts.size)

        val inventory: Inventory = Bukkit.createInventory(null, 27, Component.text(title))

        setGlassPane(inventory, 0..26)

        val middleRowSlots = listOf(10, 12, 14, 16)

        if (posts.isEmpty()) {
            val noPostsItem = createCustomItem(Material.PAPER, LanguageManager.getMessage(player, "no_posts"), customId = "no_posts")
            inventory.setItem(13, noPostsItem)
        } else {
            posts.subList(startIndex, endIndex).forEachIndexed { index, post ->
                val postItem = createCustomItem(Material.WRITTEN_BOOK, post.title, customId = post.id)
                inventory.setItem(middleRowSlots[index], postItem)
            }

            if (currentPage > 0) {
                inventory.setItem(
                    18,
                    createCustomItem(
                        Material.ARROW,
                        LanguageManager.getMessage(player, "prev_page"),
                        customId = "prev_page:$currentPage"
                    )
                )
            }
            if (currentPage < totalPages - 1) {
                inventory.setItem(
                    26,
                    createCustomItem(
                        Material.ARROW,
                        LanguageManager.getMessage(player, "next_page"),
                        customId = "next_page:$currentPage"
                    )
                )
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

        inventory.setItem(
            21,
            createCustomItem(
                Material.BARRIER,
                LanguageManager.getMessage(player, "back_button"),
                customId = "back_button"
            )
        )

        if (title == LanguageManager.getContentFromMessage(player, "my_posts")) {
            inventory.setItem(
                23,
                createCustomItem(
                    Material.LAVA_BUCKET,
                    LanguageManager.getMessage(player, "delete_post"),
                    customId = "delete_post"
                )
            )
        }

        player.openInventory(inventory)
    }

    fun openDeletePostSelection(player: Player, posts: List<Post>, page: Int = 0) {
        val itemsPerPage = 4
        val totalPages = (posts.size + itemsPerPage - 1) / itemsPerPage
        val currentPage = page.coerceIn(0, if (totalPages == 0) 0 else totalPages - 1)
        val startIndex = currentPage * itemsPerPage
        val endIndex = (startIndex + itemsPerPage).coerceAtMost(posts.size)

        val inventory: Inventory =
            Bukkit.createInventory(null, 27, LanguageManager.getMessage(player, "select_post_to_delete"))

        setGlassPane(inventory, 0..26)

        val middleRowSlots = listOf(10, 12, 14, 16)

        if (posts.isEmpty()) {
            val noPostsItem =
                createCustomItem(Material.PAPER, LanguageManager.getMessage(player, "no_posts"), customId = "no_posts")
            inventory.setItem(13, noPostsItem)
        } else {
            posts.subList(startIndex, endIndex).forEachIndexed { index, post ->
                val postItem = createCustomItem(Material.WRITTEN_BOOK, post.title, customId = post.id)
                inventory.setItem(middleRowSlots[index], postItem)
            }

            if (currentPage > 0) {
                inventory.setItem(
                    18,
                    createCustomItem(
                        Material.ARROW,
                        LanguageManager.getMessage(player, "prev_page"),
                        customId = "prev_page:$currentPage"
                    )
                )
            }
            if (currentPage < totalPages - 1) {
                inventory.setItem(
                    26,
                    createCustomItem(
                        Material.ARROW,
                        LanguageManager.getMessage(player, "next_page"),
                        customId = "next_page:$currentPage"
                    )
                )
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

        inventory.setItem(
            21,
            createCustomItem(
                Material.BARRIER,
                LanguageManager.getMessage(player, "back_button"),
                customId = "back_button"
            )
        )

        player.openInventory(inventory)
    }

    fun openDeleteConfirmationScreen(player: Player, postId: String) {
        val confirmation: Inventory =
            Bukkit.createInventory(null, 27, LanguageManager.getMessage(player, "delete_confirmation"))

        setGlassPane(confirmation, 0..8)
        setGlassPane(confirmation, 18..26)

        confirmation.setItem(
            12,
            createCustomItem(
                Material.GREEN_WOOL,
                LanguageManager.getMessage(player, "confirm_yes"),
                customId = "delete_confirm_yes:$postId"
            )
        )
        confirmation.setItem(
            14,
            createCustomItem(
                Material.YELLOW_WOOL,
                LanguageManager.getMessage(player, "confirm_no"),
                customId = "delete_confirm_no"
            )
        )

        player.openInventory(confirmation)
    }

    fun openConfirmationScreen(player: Player, type: String) {
        playerOpeningConfirmation[player.uniqueId] = true
        val confirmation: Inventory =
            Bukkit.createInventory(null, 27, LanguageManager.getMessage(player, "confirmation"))

        setGlassPane(confirmation, 0..8)
        setGlassPane(confirmation, 18..26)
        setGlassPane(confirmation, listOf(9, 10, 12, 13, 14, 16, 17))

        confirmation.setItem(
            11,
            createCustomItem(
                Material.GREEN_WOOL,
                LanguageManager.getMessage(player, "confirm_yes"),
                customId = "confirm_yes"
            )
        )
        confirmation.setItem(
            15,
            createCustomItem(
                Material.YELLOW_WOOL,
                LanguageManager.getMessage(player, "confirm_no"),
                customId = "confirm_no"
            )
        )
        if (type == "submit") {
            confirmation.setItem(
                13,
                createCustomItem(
                    Material.BLUE_WOOL,
                    LanguageManager.getMessage(player, "preview_of_post"),
                    customId = "preview_of_post"
                )
            )
        }

        pendingConfirmations[player.uniqueId] = type
        player.openInventory(confirmation)
    }

    fun openPreview(player: Player, title: Component, content: Component) {
        player.sendMessage(LanguageManager.getMessage(player, "preview_message"))
        player.sendMessage(LanguageManager.getMessage(player, "title_label").append(title))
        player.sendMessage(LanguageManager.getMessage(player, "content_label").append(content))
        player.sendMessage(LanguageManager.getMessage(player, "type_preview_close"))
    }

    fun closePreview(player: Player) {
        playerPreviewing[player.uniqueId] = true
        val (_, _) = pendingPreview[player.uniqueId] ?: return
        pendingPreview.remove(player.uniqueId)
        playerPreviewing.remove(player.uniqueId)
        Bukkit.getScheduler().runTask(BulletinBoard.instance, Runnable {
            openConfirmationScreen(player, "submit")
        })
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val clickedItem = event.currentItem ?: return
        val player = event.whoClicked as Player
        val inventoryTitle = event.view.title()
        val itemMeta = clickedItem.itemMeta ?: return

        if (clickedItem.type.isAir) {
            return
        }
        val customId = itemMeta.persistentDataContainer.get(namespacedKey, PersistentDataType.STRING)

        if (clickedItem.type == Material.BLACK_STAINED_GLASS_PANE) {
            event.isCancelled = true
            return
        }

        if (customId == "no_posts") {
            event.isCancelled = true
            return
        }

        when (inventoryTitle) {
            LanguageManager.getMessage(player, "main_board") -> {
                event.isCancelled = true
                when (customId) {
                    "new_post" -> openPostEditor(player)
                    "all_posts" -> openAllPosts(player)
                    "my_posts" -> openMyPosts(player)
                }
            }

            LanguageManager.getMessage(player, "all_posts"), LanguageManager.getMessage(player, "my_posts") -> {
                event.isCancelled = true
                val currentPage = customId?.split(":")?.getOrNull(1)?.toIntOrNull() ?: 0

                when (customId?.split(":")?.getOrNull(0)) {
                    "prev_page" -> {
                        if (inventoryTitle == LanguageManager.getMessage(player, "all_posts")) {
                            openAllPosts(player, currentPage - 1)
                        } else {
                            openMyPosts(player, currentPage - 1)
                        }
                    }

                    "next_page" -> {
                        if (inventoryTitle == LanguageManager.getMessage(player, "all_posts")) {
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

            LanguageManager.getMessage(player, "post_editor") -> {
                event.isCancelled = true
                when (customId) {
                    "post_title" -> {
                        playerInputting[player.uniqueId] = true
                        player.closeInventory()
                        pendingInputs[player.uniqueId] = "title"
                        player.sendMessage(LanguageManager.getMessage(player, "please_enter_title"))
                    }

                    "post_content" -> {
                        playerInputting[player.uniqueId] = true
                        player.closeInventory()
                        pendingInputs[player.uniqueId] = "content"
                        player.sendMessage(LanguageManager.getMessage(player, "please_enter_content"))
                    }

                    "save_post" -> {
                        val draft = pendingDrafts[player.uniqueId] ?: PostDraft()
                        val title = draft.title ?: LanguageManager.getMessage(player, "no_title")
                        val content = draft.content ?: LanguageManager.getMessage(player, "no_content")
                        pendingPreview[player.uniqueId] = Pair(title, content)
                        openConfirmationScreen(player, "submit")
                    }

                    "cancel_post" -> openConfirmationScreen(player, "cancel")
                    "back_button" -> openMainBoard(player)
                }
            }

            LanguageManager.getMessage(player, "confirmation") -> {
                event.isCancelled = true
                when (customId) {
                    "confirm_yes" -> {
                        when (pendingConfirmations[player.uniqueId]) {
                            "submit" -> {
                                val draft = pendingDrafts[player.uniqueId] ?: PostDraft()

                                val formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd_HH:mm:ss")
                                val currentDate = LocalDateTime.now().format(formatter)

                                val title = draft.title ?: LanguageManager.getMessage(player, "no_title")
                                val content = draft.content ?: LanguageManager.getMessage(player, "no_content")

                                val newPost = Post(
                                    id = UUID.randomUUID().toString(),
                                    title = title,
                                    author = player.uniqueId,
                                    content = content,
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

                                player.sendMessage(
                                    LanguageManager.getMessage(
                                        player, "post_saved"
                                    )
                                        .append(title)
                                        .append(Component.text(", "))
                                        .append(content)
                                )

                                pendingDrafts.remove(player.uniqueId)
                            }

                            "cancel" -> {
                                pendingDrafts.remove(player.uniqueId)
                                player.closeInventory()
                                Bukkit.getScheduler().runTask(BulletinBoard.instance, Runnable {
                                    openMainBoard(player)
                                })
                            }
                        }
                        player.closeInventory()
                        pendingConfirmations.remove(player.uniqueId)
                    }

                    "confirm_no" -> {
                        player.closeInventory()
                        pendingConfirmations.remove(player.uniqueId)
                        playerOpeningConfirmation.remove(player.uniqueId)
                        openPostEditor(player)
                    }

                    "preview_of_post" -> {
                        val draft = pendingDrafts[player.uniqueId] ?: PostDraft()
                        val title = draft.title ?: LanguageManager.getMessage(player, "no_title")
                        val content = draft.content ?: LanguageManager.getMessage(player, "no_content")

                        player.closeInventory()
                        openPreview(player, title, content)
                    }
                }
            }

            LanguageManager.getMessage(player, "delete_confirmation") -> {
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

                        player.sendMessage(LanguageManager.getMessage(player, "post_deleted"))
                        player.closeInventory()
                        pendingDrafts.remove(player.uniqueId)
                        pendingConfirmations.remove(player.uniqueId)
                        openMyPosts(player)
                    }

                    "delete_confirm_no" -> {
                        player.closeInventory()
                        pendingConfirmations.remove(player.uniqueId)
                        playerOpeningConfirmation.remove(player.uniqueId)
                        openMyPosts(player)
                    }

                    "back_button" -> openMyPosts(player)
                }
            }

            LanguageManager.getMessage(player, "select_post_to_delete") -> {
                event.isCancelled = true
                val currentPage = customId?.split(":")?.getOrNull(1)?.toIntOrNull() ?: 0

                when (customId?.split(":")?.getOrNull(0)) {
                    "prev_page" -> {
                        val data = JsonUtil.loadFromFile(BulletinBoard.instance.dataFile)
                        val playerData = data.players.find { it.uuid == player.uniqueId } ?: return
                        val posts = playerData.posts.mapNotNull { postId -> data.posts.find { it.id == postId } }
                        openDeletePostSelection(player, posts, currentPage - 1)
                    }

                    "next_page" -> {
                        val data = JsonUtil.loadFromFile(BulletinBoard.instance.dataFile)
                        val playerData = data.players.find { it.uuid == player.uniqueId } ?: return
                        val posts = playerData.posts.mapNotNull { postId -> data.posts.find { it.id == postId } }
                        openDeletePostSelection(player, posts, currentPage + 1)
                    }

                    "back_button" -> openMyPosts(player)
                    else -> if (customId != null) {
                        openDeleteConfirmationScreen(player, customId)
                    }
                }
            }
        }
    }

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player as? Player ?: return
        val inventoryTitle = event.view.title()

        when (inventoryTitle) {
            LanguageManager.getMessage(player, "post_editor") -> {
                if (playerPreviewing[player.uniqueId] == true) {
                    playerPreviewing.remove(player.uniqueId)
                } else if (playerOpeningConfirmation[player.uniqueId] == true) {
                    playerOpeningConfirmation.remove(player.uniqueId)
                } else if (playerInputting[player.uniqueId] == true) {
                    playerInputting.remove(player.uniqueId)
                } else {
                    pendingDrafts.remove(player.uniqueId)
                }
            }

            LanguageManager.getMessage(player, "confirmation") -> {
                pendingConfirmations.remove(player.uniqueId)
                playerOpeningConfirmation.remove(player.uniqueId)
            }
        }
    }

    @EventHandler
    fun onPlayerChat(event: AsyncPlayerChatEvent) {
        val player = event.player

        if (pendingInputs.containsKey(player.uniqueId)) {
            event.isCancelled = true
            val inputType = pendingInputs[player.uniqueId]
            val input = event.message
            val draft = pendingDrafts.getOrDefault(player.uniqueId, PostDraft())

            val updatedDraft = if (inputType == "title") {
                draft.copy(title = Component.text(input))
            } else {
                draft.copy(content = Component.text(input))
            }

            pendingDrafts[player.uniqueId] = updatedDraft

            val uTitle = updatedDraft.title ?: LanguageManager.getMessage(player, "title")
            val uContent = updatedDraft.content ?: LanguageManager.getMessage(player, "content")

            Bukkit.getScheduler().runTask(BulletinBoard.instance, Runnable {
                val postEditor = Bukkit.createInventory(null, 27, LanguageManager.getMessage(player, "post_editor"))
                setGlassPane(postEditor, 0..8)
                setGlassPane(postEditor, 18..26)
                postEditor.setItem(11, createCustomItem(Material.PAPER, uTitle, customId = "post_title"))
                postEditor.setItem(15, createCustomItem(Material.BOOK, uContent, customId = "post_content"))
                postEditor.setItem(
                    18,
                    createCustomItem(
                        Material.RED_WOOL,
                        LanguageManager.getMessage(player, "cancel_post"),
                        customId = "cancel_post"
                    )
                )
                postEditor.setItem(
                    26,
                    createCustomItem(
                        Material.GREEN_WOOL,
                        LanguageManager.getMessage(player, "save_post"),
                        customId = "save_post"
                    )
                )
                player.openInventory(postEditor)
            })
            pendingInputs.remove(player.uniqueId)
            player.sendMessage(LanguageManager.getMessage(player, "input_set").replaceText { text ->
                if (inputType != null) {
                    text.matchLiteral("{inputType}").replacement(inputType)
                }
            }.replaceText { text -> text.matchLiteral("{input}").replacement(input) })
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
        val titleComponent = LanguageManager.getMessage(player, "title_label")
            .append(post.title)

        val contentComponent = LanguageManager.getMessage(player, "content_label")
            .append(post.content)

        val dateComponent = LanguageManager.getMessage(player, "date_label")
            .append(Component.text(post.date))

        val authorComponent = LanguageManager.getMessage(player, "author_label")
            .append(Component.text(Bukkit.getOfflinePlayer(post.author).name ?: "Unknown"))

        player.closeInventory()
        Bukkit.getScheduler().runTask(BulletinBoard.instance, Runnable {
            player.sendMessage(Component.text("---------------------------------", NamedTextColor.DARK_GRAY))
            player.sendMessage(titleComponent)
            player.sendMessage(contentComponent)
            player.sendMessage(authorComponent)
            player.sendMessage(dateComponent)
            player.sendMessage(Component.text("---------------------------------", NamedTextColor.DARK_GRAY))
        })
    }
}
