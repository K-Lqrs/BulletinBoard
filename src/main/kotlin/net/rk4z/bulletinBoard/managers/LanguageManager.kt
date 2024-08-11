@file:Suppress("DEPRECATION")

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
            MessageKey.CANCEL_POST_CONFIRMATION to "投稿をキャンセルしますか？",
            MessageKey.SAVE_POST_CONFIRMATION to "投稿を保存しますか？",
            MessageKey.DELETE_POST_SELECTION to "削除する投稿を選択してください",
            MessageKey.DELETE_POST_PERMANENTLY_SELECTION to "完全に削除する投稿を選択してください",
            MessageKey.DELETE_POST_CONFIRMATION to "投稿を削除しますか？",
            //endregion

            //region Button
            MessageKey.NEW_POST to "新規投稿",
            MessageKey.DELETED_POSTS to "削除済み投稿",
            MessageKey.ABOUT_PLUGIN to "このプラグインについて",
            MessageKey.SETTINGS to "設定",
            MessageKey.HELP to "ヘルプ",
            MessageKey.SAVE_POST to "投稿する",
            MessageKey.CANCEL_POST to "投稿をキャンセル",
            MessageKey.BACK_BUTTON to "戻る",
            MessageKey.EDIT_POST to "投稿を編集",
            MessageKey.CONTINUE_POST to "投稿を続ける",
            MessageKey.CONFIRM_CANCEL_POST to "投稿をキャンセル",
            MessageKey.CONFIRM_SAVE_POST to "投稿を保存",
            MessageKey.CANCEL_CONFIRM_SAVE_POST to "投稿をやめる",
            MessageKey.PREVIEW_POST to "投稿をプレビュー",
            MessageKey.DELETE_POST to "投稿を削除",
            MessageKey.DELETE_POST_PERMANENTLY to "投稿を完全に削除",
            MessageKey.PREV_PAGE to "前のページ",
            MessageKey.NEXT_PAGE to "次のページ",
            MessageKey.CONFIRM_DELETE_POST to "投稿を削除",
            MessageKey.CANCEL_DELETE_POST to "削除をキャンセル",
            //endregion

            //region Messages
            MessageKey.PLEASE_ENTER_TITLE to "タイトルを入力して下さい: ",
            MessageKey.PLEASE_ENTER_CONTENT to "内容を入力してください: ",
            MessageKey.INPUT_SET to "{inputType}が{input}に設定されました",
            MessageKey.WHEN_POST_DRAFT_NULL to "内部データから投稿のドラフトを見つけられませんでした。もう一度お試しください : タイトル、コンテンツの両方共を空にできません。",
            MessageKey.RESTORE_POST to "投稿を復元",
            MessageKey.WHEN_DELETE_POST_NULL to "内部データから、削除しようとしている投稿を見つけられませんでした。もう一度お試しください。",
            MessageKey.POST_SAVED to "投稿が保存されました。",
            MessageKey.POST_DELETED to "投稿が削除されました。",
            MessageKey.CANCELLED_POST to "投稿がキャンセルされました。",
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
            MessageKey.USAGE_SETTINGS to "設定を開く",
            MessageKey.USAGE_DELETED_POSTS to "削除済み投稿を表示",
            MessageKey.USAGE_POSTS to "すべての投稿を表示",
            MessageKey.USAGE_PREVIEWCLOSE to "投稿のプレビューを閉じる(プレビューをしている場合のみ)",
            //endregion

            //region Label
            MessageKey.TITLE_LABEL to "タイトル: ",
            MessageKey.CONTENT_LABEL to "コンテンツ: ",
            MessageKey.AUTHOR_LABEL to "投稿者: ",
            MessageKey.DATE_LABEL to "投稿日時: ",
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
            MessageKey.BACK_BUTTON to "Back",
            MessageKey.EDIT_POST to "Edit Post",
            MessageKey.RESTORE_POST to "Restore Post",
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
            MessageKey.WHEN_POST_DRAFT_NULL to "Could not find the draft from internal data. Please try again.",
            //endregion

            //region Help
            MessageKey.USAGE_HEADER to "Subcommands",
            MessageKey.USAGE_OPENBOARD to "Open the main board",
            MessageKey.USAGE_NEWPOST to "Open the post editor",
            MessageKey.USAGE_MYPOSTS to "View your own posts",
            MessageKey.USAGE_POSTS to "View all posts",
            MessageKey.USAGE_PREVIEWCLOSE to "Close the post preview (if previewing)",
            //endregion

            //region Label
            MessageKey.TITLE_LABEL to "Title: ",
            MessageKey.CONTENT_LABEL to "Content: ",
            MessageKey.AUTHOR_LABEL to "Author: ",
            MessageKey.DATE_LABEL to "Date: ",
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
