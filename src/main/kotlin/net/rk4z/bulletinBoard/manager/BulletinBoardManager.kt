package net.rk4z.bulletinBoard.manager

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.rk4z.bulletinBoard.BulletinBoard
import net.rk4z.bulletinBoard.util.BulletinBoardUtil.setGlassPane
import net.rk4z.bulletinBoard.util.BulletinBoardUtil.createCustomItem
import net.rk4z.bulletinBoard.util.EditPostData
import net.rk4z.bulletinBoard.util.JsonUtil
import net.rk4z.bulletinBoard.util.Post
import net.rk4z.bulletinBoard.util.PostDraft
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Suppress("unused", "MemberVisibilityCanBePrivate", "DuplicatedCode")
object BulletinBoardManager {
    val pendingInputs = ConcurrentHashMap<UUID, String>()
    val pendingDrafts = ConcurrentHashMap<UUID, PostDraft>()
    val pendingConfirmations = ConcurrentHashMap<UUID, String>()
    val pendingPreview = ConcurrentHashMap<UUID, Pair<Component, Component>>()
    val playerPreviewing = ConcurrentHashMap<UUID, Boolean>()
    val playerOpeningConfirmation = ConcurrentHashMap<UUID, Boolean>()
    val playerInputting = ConcurrentHashMap<UUID, Boolean>()

    val pendingEditInputs = ConcurrentHashMap<UUID, String>()
    val pendingEditDrafts = ConcurrentHashMap<UUID, EditPostData>()
    val playerEditInputting = ConcurrentHashMap<UUID, Boolean>()

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

    fun openPostEditor(player: Player) {
        val postEditor: Inventory = Bukkit.createInventory(null, 27, LanguageManager.getMessage(player, "post_editor"))

        setGlassPane(postEditor, 0..26)

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

    fun openPostEditorForEdit(player: Player, post: Post) {
        val postEditorFE: Inventory = Bukkit.createInventory(null, 27, LanguageManager.getMessage(player, "post_editor_for_edit"))

        setGlassPane(postEditorFE, 0..26)

        val bId = post.id
        val bTitle = post.title
        val bContent = post.content

        // Insert edit draft to pendingEditDrafts
        pendingEditDrafts[player.uniqueId] = EditPostData(bId, bTitle, bContent)

        postEditorFE.setItem(11, createCustomItem(Material.PAPER, bTitle, customId = "edit_post_title"))
        postEditorFE.setItem(15, createCustomItem(Material.BOOK, bContent, customId = "edit_post_content"))

        postEditorFE.setItem(
            19,
            createCustomItem(
                Material.RED_WOOL,
                LanguageManager.getMessage(player, "cancel_edit"),
                customId = "cancel_edit"
            )
        )

        postEditorFE.setItem(
            25,
            createCustomItem(
                Material.GREEN_WOOL,
                LanguageManager.getMessage(player, "save_edit"),
                customId = "save_edit"
            )
        )

        player.openInventory(postEditorFE)
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

    object Confirmations {
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

            if (type == "edit_submit") {
                confirmation.setItem(
                    13,
                    createCustomItem(
                        Material.BLUE_WOOL,
                        LanguageManager.getMessage(player, "preview_of_edit"),
                        customId = "preview_of_edit"
                    )
                )
            }

            pendingConfirmations[player.uniqueId] = type
            player.openInventory(confirmation)
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
    }

    object Previews {
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
                Confirmations.openConfirmationScreen(player, "submit")
            })
        }
    }

    object Selections {
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

        fun openEditPostSelection(player: Player, posts: List<Post>, page: Int = 0) {
            val itemsPerPage = 4
            val totalPages = (posts.size + itemsPerPage - 1) / itemsPerPage
            val currentPage = page.coerceIn(0, if (totalPages == 0) 0 else totalPages - 1)
            val startIndex = currentPage * itemsPerPage
            val endIndex = (startIndex + itemsPerPage).coerceAtMost(posts.size)

            val inventory: Inventory =
                Bukkit.createInventory(null, 27, LanguageManager.getMessage(player, "select_post_to_edit"))

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

        if (title == LanguageManager.getContentFromMessage(player, "my_posts")) {
            inventory.setItem(
                20,
                createCustomItem(
                    Material.WRITABLE_BOOK,
                    LanguageManager.getMessage(player, "edit_post"),
                    customId = "edit_post"
                )
            )
            inventory.setItem(
                22,
                createCustomItem(
                    Material.BARRIER,
                    LanguageManager.getMessage(player, "back_button"),
                    customId = "back_button"
                )
            )

            inventory.setItem(
                24,
                createCustomItem(
                    Material.LAVA_BUCKET,
                    LanguageManager.getMessage(player, "delete_post"),
                    customId = "delete_post"
                )
            )
        }

        player.openInventory(inventory)
    }
}
