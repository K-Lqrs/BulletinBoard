package net.rk4z.bulletinBoard.managers

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.rk4z.bulletinBoard.BulletinBoard
import net.rk4z.bulletinBoard.BulletinBoard.Companion.runTask
import net.rk4z.bulletinBoard.BulletinBoard.Companion.runTaskAsynchronous
import net.rk4z.bulletinBoard.utils.*
import net.rk4z.bulletinBoard.utils.BBUtil.createCustomItem
import net.rk4z.bulletinBoard.utils.BBUtil.getPlayerTimeZone
import net.rk4z.bulletinBoard.utils.BBUtil.setGlassPane
import net.rk4z.bulletinBoard.utils.ConfirmationType.*
import net.rk4z.bulletinBoard.utils.TitleType.*
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object BulletinBoardManager {
    private val playerState = ConcurrentHashMap<UUID, PlayerState>()
    private val p = BulletinBoard.instance

    fun getPlayerState(uuid: UUID): PlayerState {
        return playerState.getOrPut(uuid) { PlayerState() }
    }

    fun openMainBoard(player: Player) {
        val mainBoard: Inventory = Bukkit.createInventory(null, 45, LanguageManager.getMessage(player, MessageKey.MAIN_BOARD))

        setGlassPane(mainBoard, 0..44)

        val buttons = listOf(
            Button(10, Material.WRITABLE_BOOK, MessageKey.NEW_POST, CustomID.NEW_POST),
            Button(12, Material.BOOK, MessageKey.ALL_POSTS, CustomID.ALL_POSTS),
            Button(14, Material.WRITTEN_BOOK, MessageKey.MY_POSTS, CustomID.MY_POSTS),
            Button(16, Material.CAULDRON, MessageKey.DELETED_POSTS, CustomID.DELETED_POSTS),
            Button(29, Material.LECTERN, MessageKey.ABOUT_PLUGIN, CustomID.ABOUT_PLUGIN),
            Button(31, Material.COMPARATOR, MessageKey.SETTINGS, CustomID.SETTINGS),
            Button(33, Material.OAK_SIGN, MessageKey.HELP, CustomID.HELP)
        )

        addButtonsToInventory(mainBoard, buttons, player)

        runTask(p) {
            player.openInventory(mainBoard)
        }
    }

    fun openPostEditor(player: Player) {
        val state = getPlayerState(player.uniqueId)
        val draft = state.draft ?: PostDraft()
        val title = draft.title ?: LanguageManager.getMessage(player, MessageKey.NO_TITLE)
        val content = draft.content ?: LanguageManager.getMessage(player, MessageKey.NO_CONTENT)

        val postEditor = createPostEditorInventory(
            player,
            title,
            content,
            LanguageManager.getMessage(player, MessageKey.POST_EDITOR),
            CustomID.POST_TITLE,
            CustomID.POST_CONTENT,
            CustomID.ANONYMOUS,
            CustomID.CANCEL_POST,
            CustomID.SAVE_POST
        )

        runTask(p) {
            player.openInventory(postEditor)
        }
    }

    fun openPostEditorForEdit(player: Player, post: Post) {
        val state = getPlayerState(player.uniqueId)
        state.editDraft = EditPostData(
            id = post.id,
            title = post.title,
            content = post.content
        )

        val postEditor = createPostEditorInventory(
            player,
            post.title,
            post.content,
            LanguageManager.getMessage(player, MessageKey.POST_EDITOR_FOR_EDIT),
            CustomID.EDIT_POST_TITLE,
            CustomID.EDIT_POST_CONTENT,
            CustomID.ANONYMOUS,
            CustomID.CANCEL_EDIT,
            CustomID.SAVE_EDIT
        )

        runTask(p) {
            player.openInventory(postEditor)
        }
    }

    fun openMyPosts(player: Player, page: Int = 0) {
        runTaskAsynchronous(p) {
            val playerPosts = BulletinBoard.database.getPostsByAuthor(player.uniqueId)
            openPostsInventory(player, MY_POSTS, playerPosts, page)
        }
    }

    fun openAllPosts(player: Player, page: Int = 0) {
        runTaskAsynchronous(p) {
            val posts = BulletinBoard.database.getAllPosts()
            openPostsInventory(player, ALL_POSTS, posts, page)
        }
    }

    fun openDeletedPosts(player: Player, page: Int = 0) {
        runTaskAsynchronous(p) {
            val deletedPosts = BulletinBoard.database.getDeletedPostsByAuthor(player.uniqueId)
            openPostsInventory(player, DELETED_POSTS, deletedPosts, page)
        }
    }

    fun openConfirmation(player: Player, type: ConfirmationType) {
        val state = getPlayerState(player.uniqueId)
        state.isOpeningConfirmation = true
        state.confirmationType = type

        val title = when (type) {
            SAVE_POST -> MessageKey.SAVE_POST_CONFIRMATION
            CANCEL_POST -> MessageKey.CANCEL_POST_CONFIRMATION
            DELETING_POST -> MessageKey.DELETE_POST_CONFIRMATION
            DELETING_POST_PERMANENTLY -> MessageKey.DELETE_POST_PERMANENTLY_CONFIRMATION
            RESTORING_POST -> MessageKey.RESTORE_POST_CONFIRMATION
        }

        val confirmation = Bukkit.createInventory(null, 27, LanguageManager.getMessage(player, title))

        setGlassPane(confirmation, 0..26)

        val buttons = when (type) {
            SAVE_POST -> listOf(
                Button(11, Material.RED_WOOL, MessageKey.CANCEL_CONFIRM_SAVE_POST, CustomID.CANCEL_CONFIRM_SAVE_POST),
                Button(13, Material.BLUE_WOOL, MessageKey.PREVIEW_POST, CustomID.PREVIEW_POST),
                Button(15, Material.GREEN_WOOL, MessageKey.CONFIRM_SAVE_POST, CustomID.CONFIRM_SAVE_POST)
            )
            CANCEL_POST -> listOf(
                Button(11, Material.RED_WOOL, MessageKey.CONTINUE_POST, CustomID.CONTINUE_POST),
                Button(15, Material.GREEN_WOOL, MessageKey.CONFIRM_CANCEL_POST, CustomID.CONFIRM_CANCEL_POST)
            )
            DELETING_POST -> listOf(
                Button(11, Material.RED_WOOL, MessageKey.CANCEL_DELETE_POST, CustomID.CANCEL_DELETE_POST),
                Button(15, Material.GREEN_WOOL, MessageKey.CONFIRM_DELETE_POST, CustomID.CONFIRM_DELETE_POST)
            )
            DELETING_POST_PERMANENTLY -> listOf(
                Button(11, Material.RED_WOOL, MessageKey.CANCEL_DELETE_POST_PERMANENTLY, CustomID.CANCEL_DELETE_POST_PERMANENTLY),
                Button(15, Material.GREEN_WOOL, MessageKey.CONFIRM_DELETE_POST_PERMANENTLY, CustomID.CONFIRM_DELETE_POST_PERMANENTLY)
            )
            RESTORING_POST -> listOf(
                Button(11, Material.RED_WOOL, MessageKey.CANCEL_RESTORE_POST, CustomID.CANCEL_RESTORE_POST),
                Button(15, Material.GREEN_WOOL, MessageKey.CONFIRM_RESTORE_POST, CustomID.CONFIRM_RESTORE_POST)
            )
        }

        addButtonsToInventory(confirmation, buttons, player)

        runTask(p) {
            player.openInventory(confirmation)
        }
        state.isChoosingConfirmationAnswer = true
    }

    fun openEditPostSelection(player: Player, page: Int = 0) {
        runTaskAsynchronous(p) {
            val playerPosts = BulletinBoard.database.getPostsByAuthor(player.uniqueId)
            openPostsInventory(player, EDIT_POST_SELECTION, playerPosts, page)
        }
    }

    fun openDeletePostSelection(player: Player, page: Int = 0) {
        runTaskAsynchronous(p) {
            val playerPosts = BulletinBoard.database.getPostsByAuthor(player.uniqueId)
            openPostsInventory(player, DELETE_POST_SELECTION, playerPosts, page)
        }
    }

    fun openRestorePostSelection(player: Player, page: Int = 0) {
        runTaskAsynchronous(p) {
            val deletedPosts = BulletinBoard.database.getDeletedPostsByAuthor(player.uniqueId)
            openPostsInventory(player, RESTORE_POST_SELECTION, deletedPosts, page)
        }
    }

    fun openDeletePostPermanentlySelection(player: Player, page: Int = 0) {
        runTaskAsynchronous(p) {
            val deletedPosts = BulletinBoard.database.getDeletedPostsByAuthor(player.uniqueId)
            openPostsInventory(player, DELETE_POST_PERMANENTLY_SELECTION, deletedPosts, page)
        }
    }

    private fun addButtonsToInventory(
        inventory: Inventory,
        buttons: List<Button>,
        player: Player
    ) {
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

    fun createPostEditorInventory(
        player: Player,
        title: Component,
        content: Component,
        editorTitle: Component,
        titleCustomId: CustomID,
        contentCustomId: CustomID,
        anonymousCustomId: CustomID,
        cancelCustomId: CustomID,
        saveCustomId: CustomID
    ): Inventory {
        val postEditor = Bukkit.createInventory(null, 27, editorTitle)
        val state = getPlayerState(player.uniqueId)
        val isAnonymous = state.draft?.isAnonymous ?: false
        val anonymousItem = if (isAnonymous) Material.LIME_WOOL else Material.RED_WOOL
        setGlassPane(postEditor, 0..26)
        postEditor.setItem(11, createCustomItem(Material.PAPER, title, customId = titleCustomId))
        postEditor.setItem(15, createCustomItem(Material.BOOK, content, customId = contentCustomId))
        postEditor.setItem(
            19,
            createCustomItem(Material.RED_WOOL, LanguageManager.getMessage(player, MessageKey.CANCEL_POST), customId = cancelCustomId)
        )
        postEditor.setItem(
            21,
            createCustomItem(anonymousItem, LanguageManager.getMessage(player, MessageKey.ANONYMOUS), customId = anonymousCustomId)
        )
        postEditor.setItem(
            25,
            createCustomItem(Material.GREEN_WOOL, LanguageManager.getMessage(player, MessageKey.SAVE_POST), customId = saveCustomId)
        )
        return postEditor
    }

    fun displayPost(player: Player, post: Post) {
        val playerTimeZone = getPlayerTimeZone(player)
        val zonedDateTime = ZonedDateTime.ofInstant(post.date.toInstant(), playerTimeZone.toZoneId())

        val titleComponent = LanguageManager.getMessage(player, MessageKey.TITLE_LABEL)
            .append(post.title)

        val contentComponent = LanguageManager.getMessage(player, MessageKey.CONTENT_LABEL)
            .append(post.content)

        val dateComponent = LanguageManager.getMessage(player, MessageKey.DATE_LABEL)
            .append(Component.text(zonedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z"))))

        val authorComponent = if (post.isAnonymous) {
            LanguageManager.getMessage(player, MessageKey.AUTHOR_LABEL)
                .append(LanguageManager.getMessage(player, MessageKey.ANONYMOUS_USER))
        } else {
            LanguageManager.getMessage(player, MessageKey.AUTHOR_LABEL)
                .append(Component.text(Bukkit.getOfflinePlayer(post.author).name ?: "Unknown"))
        }

        runTask(p) {
            player.closeInventory()
            player.sendMessage(Component.text("---------------------------------", NamedTextColor.DARK_GRAY))
            player.sendMessage(titleComponent)
            player.sendMessage(contentComponent)
            player.sendMessage(authorComponent)
            player.sendMessage(dateComponent)
            player.sendMessage(Component.text("---------------------------------", NamedTextColor.DARK_GRAY))
        }
    }


    private fun openPostsInventory(player: Player, titleType: TitleType, posts: List<Post>, page: Int) {
        val middleRowSlots = listOf(10, 12, 14, 16)
        val itemsPerPage = middleRowSlots.size
        val totalPages = (posts.size + itemsPerPage - 1) / itemsPerPage
        val currentPage = page.coerceIn(0, if (totalPages == 0) 0 else totalPages - 1)
        val startIndex = currentPage * itemsPerPage
        val endIndex = (startIndex + itemsPerPage).coerceAtMost(posts.size)
        val title = titleType.key

        val inventory = Bukkit.createInventory(null, 27, LanguageManager.getMessage(player, title))

        setGlassPane(inventory, 0..26)

        if (posts.isEmpty()) {
            val noPostsItem = createCustomItem(Material.PAPER, LanguageManager.getMessage(player, MessageKey.NO_POSTS), customId = CustomID.NO_POSTS)
            inventory.setItem(13, noPostsItem)
        } else {
            posts.subList(startIndex, endIndex).forEachIndexed { index, (postId, _, title, _, _, _) ->
                val postItem = createCustomItem(Material.WRITTEN_BOOK, title, customId = postId.toString())
                inventory.setItem(middleRowSlots[index], postItem)
            }
        }

        if (currentPage > 0) {
            inventory.setItem(
                18,
                createCustomItem(
                    Material.ARROW,
                    LanguageManager.getMessage(player, MessageKey.PREV_PAGE),
                    customId = "${CustomID.PREV_PAGE.name}:$currentPage"
                )
            )
        }

        if (currentPage < totalPages - 1) {
            inventory.setItem(
                26,
                createCustomItem(
                    Material.ARROW,
                    LanguageManager.getMessage(player, MessageKey.NEXT_PAGE),
                    customId = "${CustomID.NEXT_PAGE.name}:$currentPage"
                )
            )
        }

        inventory.setItem(
            22,
            createCustomItem(
                Material.BARRIER,
                LanguageManager.getMessage(player, MessageKey.BACK_BUTTON),
                customId = CustomID.BACK_BUTTON
            )
        )

        when (titleType) {
            MY_POSTS -> {
                val buttons = listOf(
                    Button(20, Material.WRITABLE_BOOK, MessageKey.EDIT_POST, CustomID.EDIT_POST),
                    Button(24, Material.CAULDRON, MessageKey.DELETE_POST, CustomID.DELETE_POST)
                )
                addButtonsToInventory(inventory, buttons, player)
            }
            DELETED_POSTS -> {
                val buttons = listOf(
                    Button(20, Material.RESPAWN_ANCHOR, MessageKey.RESTORE_POST, CustomID.RESTORE_POST),
                    Button(24, Material.LAVA_BUCKET, MessageKey.DELETE_POST_PERMANENTLY, CustomID.DELETE_POST_PERMANENTLY)
                )
                addButtonsToInventory(inventory, buttons, player)
            }
            ALL_POSTS -> {
                val hasPermission = PermissionManager.hasPermission(player, "deleteOthers")
                if (hasPermission) {
                    val buttons = listOf(
                        Button(24, Material.CAULDRON, MessageKey.DELETE_POST_OTHERS, CustomID.DELETE_POST_OTHERS)
                    )
                    addButtonsToInventory(inventory, buttons, player)
                }
            }
            DELETE_POST_SELECTION,
            DELETE_POST_PERMANENTLY_SELECTION,
            EDIT_POST_SELECTION,
            RESTORE_POST_SELECTION -> {
                // no additional buttons
            }
        }

        runTask(p) {
            player.openInventory(inventory)
        }
    }
}
