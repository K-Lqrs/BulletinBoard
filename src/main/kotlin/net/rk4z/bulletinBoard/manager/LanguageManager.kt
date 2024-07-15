package net.rk4z.bulletinBoard.manager

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

@Suppress("unused", "DEPRECATION")
object LanguageManager {
    private val messages = mapOf(
        "ja" to mapOf(
            "main_board" to "メインボード",
            "post_editor" to "投稿エディタ",
            "my_posts" to "私の投稿",
            "all_posts" to "すべての投稿",
            "select_post_to_delete" to "削除する投稿を選択してください",
            "delete_confirmation" to "本当に削除しますか？",
            "confirmation" to "本当に実行しますか？",
            "new_post" to "新しい投稿",
            "cancel_post" to "キャンセル",
            "save_post" to "投稿を保存",
            "no_posts" to "投稿はありません",
            "prev_page" to "前のページ",
            "next_page" to "次のページ",
            "back_button" to "戻る",
            "delete_post" to "投稿を削除",
            "confirm_yes" to "はい",
            "confirm_no" to "いいえ",
            "preview_of_post" to "プレビュー",
            "preview_message" to "投稿のプレビュー：",
            "title_label" to "タイトル：",
            "content_label" to "内容：",
            "author_label" to "投稿者：",
            "type_preview_close" to "プレビューを閉じるには /bb previewclose を入力してください。",
            "post_saved" to "投稿が保存されました：",
            "post_deleted" to "投稿が削除されました。",
            "please_enter_title" to "タイトルを入力してください：",
            "please_enter_content" to "内容を入力してください：",
            "no_title" to "タイトルがありません",
            "no_content" to "内容がありません",
            "date_label" to "日付：",
            "input_set" to "{inputType}が{input}に設定されました",
            "usage_openboard" to "メインボードを開く",
            "usage_newpost" to "投稿エディタを開く",
            "usage_myposts" to "自分の投稿を表示",
            "usage_posts" to "すべての投稿を表示",
            "usage_previewclose" to "投稿のプレビューを閉じる(プレビューをしている場合のみ)",
            "please_use_help" to "サブコマンドのリストを表示するには /bb help を使用してください",
            "htu_header" to "BulletinBoardの使い方",
            "htu_title" to "ここでは、BulletinBoardの使い方を説明します。",
            "htu_mainboard" to "メインボードを開くには /bb openboard を使用します。" +
                    "メインボードには、新規投稿、全投稿、自分の投稿を見ることができるアイコンがあります。",
            "htu_newpost" to "新しい投稿を作成するには /bb newpost を使用します。" +
                    "タイトルと内容を入力し、/bb savepost で保存します。" +
                    "タイトル、コンテンツはチャット欄で入力します。GUIのアイテムをクリックすると一度GUIが閉じられ、" +
                    "入力を求められます。送信すると、それがタイトル、コンテンツに設定されます。",
            "htu_myposts" to "自分の投稿を見るには /bb myposts を使用するか、メインボードからアイコンをクリックします。" +
                    "自分の投稿が表示され、削除することもできます。また、投稿をクリックすると詳細を見ることができます。",
            "htu_posts" to "すべての投稿を見るには /bb posts を使用するか、メインボードからアイコンをクリックします。" +
                    "すべての投稿が表示され、クリックすると詳細を見ることができます。",
            "htu_preview" to "投稿のプレビューを見るには 投稿エディタで「プレビュー」をクリックします。" +
                    "プレビューを見ると、投稿の内容がチャット欄に表示されます。" +
                    "プレビューを閉じるには /bb previewclose を使用します。",
            "htu_previewclose" to "投稿のプレビューを閉じるには /bb previewclose を使用します。" +
                    "このコマンドは、投稿エディタでプレビューをしている場合のみ使用できます。"
        ),
        "en" to mapOf(
            "main_board" to "Main Board",
            "post_editor" to "Post Editor",
            "my_posts" to "My Posts",
            "all_posts" to "All Posts",
            "select_post_to_delete" to "Select Post to Delete",
            "delete_confirmation" to "Are you sure you want to delete?",
            "confirmation" to "Confirmation",
            "new_post" to "New Post",
            "cancel_post" to "Cancel",
            "save_post" to "Save Post",
            "no_posts" to "No posts available",
            "prev_page" to "Previous Page",
            "next_page" to "Next Page",
            "back_button" to "Back",
            "delete_post" to "Delete Post",
            "confirm_yes" to "Yes",
            "confirm_no" to "No",
            "preview_of_post" to "Preview",
            "preview_message" to "Preview of your post:",
            "title_label" to "Title: ",
            "content_label" to "Content: ",
            "author_label" to "Author:",
            "type_preview_close" to "Type /bb previewclose to close the preview and continue.",
            "post_saved" to "Post Saved: ",
            "post_deleted" to "Post deleted successfully.",
            "please_enter_title" to "Please enter the title for your post:",
            "please_enter_content" to "Please enter the content for your post:",
            "no_title" to "No title",
            "no_content" to "No content",
            "date_label" to "Date: ",
            "input_set" to "{inputType} has been set to {input}",
            "usage_openboard" to "Open the main board",
            "usage_newpost" to "Open the post editor",
            "usage_myposts" to "View your posts",
            "usage_posts" to "View all posts",
            "usage_previewclose" to "Close the post preview",
            "please_use_help" to "Please use /bb help for a list of subcommands",
            "htu_header" to "How to Use BulletinBoard",
            "htu_title" to "Here you will find instructions on how to use BulletinBoard.",
            "htu_mainboard" to "To open the main board, use /bb openboard. " +
                    "The main board has icons for creating a new post, viewing all posts, and viewing your own posts.",
            "htu_newpost" to "To create a new post, use /bb newpost. " +
                    "Enter a title and content, and save it with /bb savepost. " +
                    "The title and content are entered in the chat. " +
                    "Clicking on the GUI items will close the GUI once and prompt you to enter the input. " +
                    "When you submit, it will be set as the title or content.",
            "htu_myposts" to "To view your posts, use /bb myposts or click the icon on the main board. " +
                    "Your posts will be displayed, and you can delete them. " +
                    "You can also click on a post to view more details.",
            "htu_posts" to "To view all posts, use /bb posts or click the icon on the main board. " +
                    "All posts will be displayed, and you can click on them to view more details.",
            "htu_preview" to "To view a preview of your post, click on 'Preview' in the post editor. " +
                    "The preview will show the content of your post in the chat. " +
                    "To close the preview, use /bb previewclose.",
            "htu_previewclose" to "To close the preview of your post, use /bb previewclose. " +
                    "This command can only be used when you are previewing a post."
        )
    )


    private fun getLanguage(player: Player): String {
        return player.locale.substring(0, 2)
    }

    fun getMessage(player: Player, key: String): Component {
        val languageCode = getLanguage(player)
        val message = messages[languageCode]?.get(key) ?: messages["en"]?.get(key) ?: key
        return Component.text(message)
    }

    fun getContentFromMessage(player: Player, key: String): String {
        val languageCode = getLanguage(player)
        val message = messages[languageCode]?.get(key) ?: messages["en"]?.get(key) ?: key
        return message
    }
}
