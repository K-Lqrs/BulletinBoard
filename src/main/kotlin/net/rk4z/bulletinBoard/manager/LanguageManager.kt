package net.rk4z.bulletinBoard.manager

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

@Suppress("unused", "DEPRECATION")
object LanguageManager {
    private val messages = mapOf(
        "ja" to mapOf(
            //region GUI
            "main_board" to "メインボード",
            "post_editor" to "投稿エディタ",
            "select_post_to_edit" to "編集する投稿を選択してください",
            "select_post_to_delete" to "削除する投稿を選択してください",
            "post_editor_for_edit" to "編集用投稿エディタ",
            "my_posts" to "私の投稿",
            "all_posts" to "すべての投稿",
            //endregion

            //region Confirmation
            "delete_confirmation" to "本当に削除しますか？",
            "confirmation" to "本当に実行しますか？",
            //endregion

            //region Buttons
            "new_post" to "新しい投稿",
            "edit_post" to "投稿を編集",
            "cancel" to "キャンセル",
            "cancel_post" to "投稿をキャンセル",
            "cancel_edit" to "編集をキャンセル",
            "save_post" to "投稿を保存",
            "save_edit" to "編集を保存",
            "prev_page" to "前のページ",
            "next_page" to "次のページ",
            "back_button" to "戻る",
            "delete_post" to "投稿を削除",
            "confirm_yes" to "はい",
            "confirm_no" to "いいえ",
            //endregion

            //region Labels
            "preview_of_post" to "プレビュー",
            "preview_message" to "投稿のプレビュー：",
            "title_label" to "タイトル：",
            "content_label" to "内容：",
            "author_label" to "投稿者：",
            //endregion

            //region Messages
            "type_preview_close" to "プレビューを閉じるには /bb previewclose を入力してください。",
            "post_saved" to "投稿が保存されました：",
            "post_deleted" to "投稿が削除されました。",
            "please_enter_title" to "タイトルを入力してください：",
            "please_enter_content" to "内容を入力してください：",
            "no_title" to "タイトルがありません",
            "no_content" to "内容がありません",
            "date_label" to "日付：",
            "input_set" to "{inputType}が{input}に設定されました",
            //endregion

            //region Usage
            "usage_openboard" to "メインボードを開く",
            "usage_newpost" to "投稿エディタを開く",
            "usage_myposts" to "自分の投稿を表示",
            "usage_posts" to "すべての投稿を表示",
            "usage_previewclose" to "投稿のプレビューを閉じる(プレビューをしている場合のみ)",
            "please_use_help" to "サブコマンドのリストを表示するには /bb help を使用してください",
            //endregion

            //region others
            "no_posts" to "投稿はありません",
            //endregion

            //region How to Use
            "htu_header" to "BulletinBoardの使い方",
            "htu_title" to "ここでは、BulletinBoardの使い方を説明します。",
            "htu_openboard" to "メインボードを開くには /bb openboard を使用します。" +
                    "メインボードには、新規投稿、全投稿、自分の投稿を見ることができるアイコンがあります。",
            "htu_newpost" to "新しい投稿を作成するには /bb newpost を使用するか、メインボードからアイコンをクリックします。" +
                    "タイトル、コンテンツはチャット欄で入力します。GUIのアイテムをクリックすると一度GUIが閉じられ、" +
                    "入力を求められます。送信すると、それがタイトル、コンテンツに設定されます。" +
                    "すべて入力し終わったら、GUIから保存ができます。",
            "htu_myposts" to "自分の投稿を見るには /bb myposts を使用するか、メインボードからアイコンをクリックします。" +
                    "自分の投稿が表示され、削除することもできます。また、投稿をクリックすると詳細を見ることができます。",
            "htu_posts" to "すべての投稿を見るには /bb posts を使用するか、メインボードからアイコンをクリックします。" +
                    "すべての投稿が表示され、クリックすると詳細を見ることができます。",
            "htu_preview" to "投稿のプレビューを見るには 投稿エディタで「プレビュー」をクリックします。" +
                    "プレビューを見ると、投稿の内容がチャット欄に表示されます。" +
                    "プレビューを閉じるには /bb previewclose を使用します。",
            "htu_previewclose" to "投稿のプレビューを閉じるには /bb previewclose を使用します。" +
                    "このコマンドは、投稿エディタでプレビューをしている場合のみ使用できます。"
            //endregion
        ),
        "en" to mapOf(
            //region GUI
            "main_board" to "Main Board",
            "post_editor" to "Post Editor",
            "select_post_to_edit" to "Select a post to edit",
            "post_editor_for_edit" to "Post Editor for Edit",
            "my_posts" to "My Posts",
            "all_posts" to "All Posts",
            //endregion

            //region Confirmation
            "select_post_to_delete" to "Select a post to delete",
            "delete_confirmation" to "Are you sure you want to delete?",
            "confirmation" to "Are you sure you want to execute?",
            //endregion

            //region Buttons
            "new_post" to "New Post",
            "cancel" to "Cancel",
            "cancel_post" to "Cancel Post",
            "cancel_edit" to "Cancel Edit",
            "save_post" to "Save Post",
            "save_edit" to "Save Edit",
            "prev_page" to "Previous Page",
            "next_page" to "Next Page",
            "back_button" to "Back",
            "delete_post" to "Delete Post",
            "confirm_yes" to "Yes",
            "confirm_no" to "No",
            //endregion

            //region Labels
            "preview_of_post" to "Preview",
            "preview_message" to "Preview of the post:",
            "title_label" to "Title:",
            "content_label" to "Content:",
            "author_label" to "Author:",
            //endregion

            //region Messages
            "type_preview_close" to "To close the preview, type /bb previewclose.",
            "post_saved" to "Post saved:",
            "post_deleted" to "Post deleted.",
            "please_enter_title" to "Please enter a title:",
            "please_enter_content" to "Please enter content:",
            "no_title" to "No title",
            "no_content" to "No content",
            "date_label" to "Date:",
            "input_set" to "{inputType} has been set to {input}",
            //endregion

            //region Usage
            "usage_openboard" to "Open the main board",
            "usage_newpost" to "Open the post editor",
            "usage_myposts" to "Show my posts",
            "usage_posts" to "Show all posts",
            "usage_previewclose" to "Close the post preview (only when previewing)",
            "please_use_help" to "Use /bb help to show the list of subcommands",
            //endregion

            //region others
            "no_posts" to "No posts",
            //endregion

            //region How to Use
            "htu_header" to "How to Use BulletinBoard",
            "htu_title" to "Here, we explain how to use BulletinBoard.",
            "htu_openboard" to "To open the main board, use /bb openboard. " +
                    "The main board has icons for creating a new post, viewing all posts, and viewing your own posts.",
            "htu_newpost" to "To create a new post, use /bb newpost or click the icon on the main board. " +
                    "Enter the title and content in the chat box. " +
                    "Click the GUI item to close the GUI once, and you will be prompted to enter it. " +
                    "When you send it, it will be set as the title and content. " +
                    "When you have finished entering everything, you can save it from the GUI.",
            "htu_myposts" to "To view your own posts, use /bb myposts or click the icon on the main board. " +
                    "Your posts will be displayed, and you can also delete them. " +
                    "You can also click on a post to see more details.",
            "htu_posts" to "To view all posts, use /bb posts or click the icon on the main board. " +
                    "All posts will be displayed, and you can click on them to see more details.",
            "htu_preview" to "To view a preview of the post, click \"Preview\" in the post editor. " +
                    "When you view the preview, the content of the post will be displayed in the chat box. " +
                    "To close the preview, use /bb previewclose.",
            "htu_previewclose" to "To close the preview of the post, use /bb previewclose. " +
                    "This command can only be used when previewing in the post editor."
            //endregion
        )
    )

    private fun getLanguage(player: Player): String {
        return player.locale.substring(0, 2)
    }

    // getMessage() returns a Component object
    fun getMessage(player: Player, key: String): Component {
        val languageCode = getLanguage(player)
        val message = messages[languageCode]?.get(key) ?: messages["en"]?.get(key) ?: key
        return Component.text(message)
    }

    // But, getContentFromMessage() returns a String object
    fun getContentFromMessage(player: Player, key: String): String {
        val languageCode = getLanguage(player)
        val message = messages[languageCode]?.get(key) ?: messages["en"]?.get(key) ?: key
        return message
    }
}
