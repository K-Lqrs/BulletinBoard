package net.rk4z.bulletinBoard.managers

import net.kyori.adventure.text.Component
import net.rk4z.bulletinBoard.utils.MessageKey
import org.bukkit.entity.Player

object LanguageManager {
    private val messages: MutableMap<String, MutableMap<MessageKey, String>> = mutableMapOf(
        "ja" to mutableMapOf(
            //region GUI
            MessageKey.MAIN_BOARD to "メインボード",
            MessageKey.POST_EDITOR to "投稿エディタ",
            MessageKey.ALL_POSTS to "投稿一覧",
            MessageKey.MY_POSTS to "自分の投稿",
            //endregion

            //region Button
            MessageKey.NEW_POST to "新規投稿",
            MessageKey.DELETED_POSTS to "削除済み投稿",
            MessageKey.ABOUT_PLUGIN to "このプラグインについて",
            MessageKey.SETTINGS to "設定",
            MessageKey.HELP to "ヘルプ",
            MessageKey.SAVE_POST to "投稿する",
            MessageKey.CANCEL_POST to "投稿をキャンセル",
            //endregion

            //region Messages
            MessageKey.PLEASE_ENTER_TITLE to "タイトルを入力して下さい: ",
            MessageKey.PLEASE_ENTER_CONTENT to "内容を入力してください: ",
            MessageKey.INPUT_SET to "{inputType}が{input}に設定されました",
            //endregion

            //region Others
            MessageKey.NO_TITLE to "タイトルがありません...",
            MessageKey.NO_CONTENT to "コンテンツがありません...",
            MessageKey.NO_POSTS to "ここには何もないようです...",
            //endregion

            //region Help
            MessageKey.USAGE_HEADER to "サブコマンド一覧",
            MessageKey.USAGE_OPENBOARD to "メインボードを開く",
            MessageKey.USAGE_NEWPOST to "投稿エディタを開く",
            MessageKey.USAGE_MYPOSTS to "自分の投稿を表示",
            MessageKey.USAGE_POSTS to "すべての投稿を表示",
            MessageKey.USAGE_PREVIEWCLOSE to "投稿のプレビューを閉じる(プレビューをしている場合のみ)",
            //endregion

            //region How to Use
            MessageKey.HTU_HEADER to "BulletinBoardの使い方",
            MessageKey.HTU_OPENBOARD to "メインボードを開くには /bb openboard を使用します。メインボードには、新規投稿、全投稿、自分の投稿を見ることができるアイコンがあります。",
            MessageKey.HTU_NEWPOST to "新しい投稿を作成するには /bb newpost を使用するか、メインボードからアイコンをクリックします。タイトル、コンテンツはチャット欄で入力します。GUIのアイテムをクリックすると一度GUIが閉じられ、入力を求められます。送信すると、それがタイトル、コンテンツに設定されます。すべて入力し終わったら、GUIから保存ができます。",
            MessageKey.HTU_MYPOSTS to "自分の投稿を見るには /bb myposts を使用するか、メインボードからアイコンをクリックします。自分の投稿が表示され、削除することもできます。また、投稿をクリックすると詳細を見ることができます。",
            MessageKey.HTU_POSTS to "すべての投稿を見るには /bb posts を使用するか、メインボードからアイコンをクリックします。すべての投稿が表示され、クリックすると詳細を見ることができます。",
            MessageKey.HTU_PREVIEW to "投稿のプレビューを見るには 投稿エディタで「プレビュー」をクリックします。プレビューを見ると、投稿の内容がチャット欄に表示されます。プレビューを閉じるには /bb previewclose を使用します。",
            MessageKey.HTU_PREVIEW_CLOSE to "投稿のプレビューを閉じるには /bb previewclose を使用します。このコマンドは、投稿エディタでプレビューをしている場合のみ使用できます。"
            //endregion
        ),
        "en" to mutableMapOf(
            //region GUI
            MessageKey.MAIN_BOARD to "Main Board",
            MessageKey.POST_EDITOR to "Post Editor",
            MessageKey.ALL_POSTS to "Posts",
            MessageKey.MY_POSTS to "My Posts",
            //endregion

            //region Button
            MessageKey.NEW_POST to "New Post",
            MessageKey.DELETED_POSTS to "Deleted Posts",
            MessageKey.ABOUT_PLUGIN to "About Plugin",
            MessageKey.SETTINGS to "Settings",
            MessageKey.HELP to "Help",

            MessageKey.SAVE_POST to "Save Post",
            MessageKey.CANCEL_POST to "Cancel Post",
            //endregion

            //region Others
            MessageKey.NO_TITLE to "No Title",
            MessageKey.NO_CONTENT to "No Content",
            MessageKey.NO_POSTS to "Nothing to see here...",
            //endregion

            //region Messages
            MessageKey.PLEASE_ENTER_TITLE to "Please enter a title: ",
            MessageKey.PLEASE_ENTER_CONTENT to "Please enter content: ",
            MessageKey.INPUT_SET to "{inputType} set to {input}",
            //endregion

            //region Help
            MessageKey.USAGE_HEADER to "Subcommands",
            MessageKey.USAGE_OPENBOARD to "Open the main board",
            MessageKey.USAGE_NEWPOST to "Open the post editor",
            MessageKey.USAGE_MYPOSTS to "View your own posts",
            MessageKey.USAGE_POSTS to "View all posts",
            MessageKey.USAGE_PREVIEWCLOSE to "Close the post preview (if previewing)",
            //endregion

            //region How to Use
            MessageKey.HTU_HEADER to "How to Use Bulletin Board",
            MessageKey.HTU_OPENBOARD to "To open the main board, use /bb openboard. The main board has icons for creating a new post, viewing all posts, and viewing your own posts.",
            MessageKey.HTU_NEWPOST to "To create a new post, use /bb newpost or click the icon on the main board. You will be prompted to enter a title and content in the chat. Clicking on the GUI items will close the GUI and prompt you to enter the information. Once you have entered all the information, you can save it from the GUI.",
            MessageKey.HTU_MYPOSTS to "To view your own posts, use /bb myposts or click the icon on the main board. Your posts will be displayed, and you can delete them. You can also click on a post to view more details.",
            MessageKey.HTU_POSTS to "To view all posts, use /bb posts or click the icon on the main board. All posts will be displayed, and you can click on them to view more details.",
            MessageKey.HTU_PREVIEW to "To preview a post, click on the \"Preview\" button in the post editor. The post content will be displayed in the chat. To close the preview, use /bb previewclose.",
            MessageKey.HTU_PREVIEW_CLOSE to "To close the post preview, use /bb previewclose. This command can only be used when previewing a post in the post editor."
            //endregion
        )
    )

    private fun getLanguage(player: Player): String {
        return player.locale.substring(0, 2)
    }

    // getMessage() returns a Component object
    fun getMessage(player: Player, key: MessageKey): Component {
        val language = getLanguage(player)
        val message = messages[language]?.get(key) ?: messages["en"]?.get(key) ?: key.name
        return Component.text(message)
    }

    // getContentFromMessage() returns a String object
    fun getContentFromMessage(player: Player, key: MessageKey): String {
        val language = getLanguage(player)
        val message = messages[language]?.get(key) ?: messages["en"]?.get(key) ?: key.name
        return message
    }
}
