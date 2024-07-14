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
