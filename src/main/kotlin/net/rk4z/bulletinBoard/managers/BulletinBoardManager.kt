package net.rk4z.bulletinBoard.managers

import net.kyori.adventure.text.Component
import net.rk4z.bulletinBoard.BulletinBoard
import net.rk4z.bulletinBoard.utils.*
import net.rk4z.bulletinBoard.utils.BulletinBoardUtil.createCustomItem
import net.rk4z.bulletinBoard.utils.BulletinBoardUtil.setGlassPane
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object BulletinBoardManager {
    private val playerStates = ConcurrentHashMap<UUID, PlayerState>()

    fun getPlayerState(playerId: UUID): PlayerState {
        return playerStates.getOrPut(playerId) { PlayerState() }
    }

    fun openMainBoard(player: Player) {
        val mainBoard: Inventory = Bukkit.createInventory(null, 45, LanguageManager.getMessage(player, "mainBoard"))

        setGlassPane(mainBoard, 0..44)

        val buttons = listOf(
            // The "Key" is used to get the message from the LanguageManager
            // Quadruple(slot, material, key, customId)
            Quadruple(10, Material.WRITABLE_BOOK, "newPost", "newPost"),
            Quadruple(12, Material.BOOK, "allPosts", "allPosts"),
            Quadruple(14, Material.WRITTEN_BOOK, "myPosts", "myPosts"),
            Quadruple(16, Material.FLINT_AND_STEEL, "deletedPosts", "deletedPosts"),
            Quadruple(29, Material.LECTERN, "aboutPlugin", "about"),
            Quadruple(31, Material.COMPARATOR, "settings", "settings"),
            Quadruple(33, Material.OAK_SIGN, "help", "help")
        )

        buttons.forEach { (slot, material, key, customId) ->
            mainBoard.setItem(
                slot,
                createCustomItem(
                    material,
                    LanguageManager.getMessage(player, key),
                    customId = customId
                )
            )
        }

        Bukkit.getScheduler().runTask(BulletinBoard.instance, Runnable {
            player.openInventory(mainBoard)
        })
    }

    fun openPostEditor(player: Player) {
        val postEditor: Inventory = Bukkit.createInventory(null, 27, LanguageManager.getMessage(player, "post_editor"))

        setGlassPane(postEditor, 0..26)

        val state = getPlayerState(player.uniqueId)
        val draft = state.draft ?: PostDraft()
        val title = draft.title ?: LanguageManager.getMessage(player, "notTitle")
        val content = draft.content ?: LanguageManager.getMessage(player, "noContent")

        val postButtons = listOf(
            Quadruple(11, Material.PAPER, title, "postTitle"),
            Quadruple(15, Material.BOOK, content, "postContent"),
        )

        val buttons = listOf(
            Quadruple(19, Material.RED_WOOL, "cancelPost", "cancelPost"),
            Quadruple(25, Material.GREEN_WOOL, "savePost", "savePost")
        )

        buttons.forEach { (slot, material, key, customId) ->
            postEditor.setItem(
                slot,
                createCustomItem(
                    material,
                    LanguageManager.getMessage(player, key),
                    customId = customId
                )
            )
        }

        postButtons.forEach { (slot, material, name, customId) ->
            postEditor.setItem(
                slot,
                createCustomItem(
                    material,
                    name,
                    customId = customId
                )
            )
        }

        Bukkit.getScheduler().runTask(BulletinBoard.instance, Runnable {
            player.openInventory(postEditor)
        })
    }

    fun openMyPosts(player: Player, page: Int = 0) {
        val data = JsonUtil.loadFromFile(BulletinBoard.instance.dataFile)
        val playerPosts = data.posts.filter { it.author == player.uniqueId }

        openPostsInventory(player, LanguageManager.getContentFromMessage(player, "myPosts"), playerPosts, page)

    }

    fun openAllPosts(player: Player, page: Int = 0) {
        val data = JsonUtil.loadFromFile(BulletinBoard.instance.dataFile)
        val posts = data.posts

        openPostsInventory(player, LanguageManager.getContentFromMessage(player, "allPosts"), posts, page)
    }

    fun openDeletedPosts(player: Player, page: Int = 0) {
        val data = JsonUtil.loadFromFile(BulletinBoard.instance.dataFile)
        val deletedPosts = data.deleted.filter { it.author == player.uniqueId }

        openPostsInventory(player, LanguageManager.getContentFromMessage(player, "deletedPosts"), deletedPosts, page)
    }

    fun performAbout(player: Player) {
        player.closeInventory()
        player.performCommand("bb about")
    }

    fun openSettings(player: Player) {
        val settings: Inventory = Bukkit.createInventory(null, 27, LanguageManager.getMessage(player, "settings"))

        setGlassPane(settings, 0..26)

        val data = JsonUtil.loadFromFile(BulletinBoard.instance.dataFile)
        val playerData = data.permission.find { it.uuid == player.uniqueId }?: Permission(player.uniqueId, emptyList())

        if (playerData.acquiredPermission.contains("openSettings")) {
            val buttons = listOf(
                Quadruple(11, Material.BOOK, "openSettings", "openSettings"),
                Quadruple(15, Material.BOOK, "closeSettings", "closeSettings")
            )

            buttons.forEach { (slot, material, key, customId) ->
                settings.setItem(
                    slot,
                    createCustomItem(
                        material,
                        LanguageManager.getMessage(player, key),
                        customId = customId
                    )
                )
            }

            Bukkit.getScheduler().runTask(BulletinBoard.instance, Runnable {
                player.openInventory(settings)
            })
        } else {
            player.sendMessage(LanguageManager.getMessage(player, "noPermission"))
        }
    }

    fun performHelp(player: Player) {
        player.closeInventory()
        player.performCommand("bb help")
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
            val noPostsItem =
                createCustomItem(Material.PAPER, LanguageManager.getMessage(player, "noPosts"), customId = "noPosts")
            inventory.setItem(13, noPostsItem)
        } else {
            posts.subList(startIndex, endIndex).forEachIndexed { index, post ->
                val postItem = createCustomItem(Material.WRITTEN_BOOK, post.title, customId = post.id.toString())
                inventory.setItem(middleRowSlots[index], postItem)
            }

            val buttons = mutableListOf<Quadruple<Int, Material, String, String>>()

            if (currentPage > 0) {
                buttons.add(Quadruple(18, Material.ARROW, "prevPage", "prevPage:$currentPage"))
            }
            if (currentPage < totalPages - 1) {
                buttons.add(Quadruple(26, Material.ARROW, "nextPage", "nextPage:$currentPage"))
            }

            if (posts.size <= itemsPerPage) {
                if (inventory.getItem(18) == null) {
                    setGlassPane(inventory, listOf(18))
                }
                if (inventory.getItem(26) == null) {
                    setGlassPane(inventory, listOf(26))
                }
            }

            if (title == LanguageManager.getContentFromMessage(player, "allPosts")) {
                buttons.add(Quadruple(22, Material.BARRIER, "backButton", "backButton"))
            }

            if (title == LanguageManager.getContentFromMessage(player, "myPosts")) {
                buttons.add(Quadruple(20, Material.WRITABLE_BOOK, "editPost", "editPost"))
                buttons.add(Quadruple(22, Material.BARRIER, "backButton", "backButton"))
                buttons.add(Quadruple(24, Material.LAVA_BUCKET, "deletePost", "deletePost"))
            }

            buttons.forEach { (slot, material, key, customId) ->
                inventory.setItem(
                    slot,
                    createCustomItem(
                        material,
                        LanguageManager.getMessage(player, key),
                        customId = customId
                    )
                )
            }
        }

        Bukkit.getScheduler().runTask(BulletinBoard.instance, Runnable {
            player.openInventory(inventory)
        })
    }

}