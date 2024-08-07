@file:Suppress("unused", "DEPRECATION")

package net.rk4z.bulletinBoard.managers

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

object LanguageManager {
    private val messages: MutableMap<String, MutableMap<String, String>> = mutableMapOf(
        "ja" to mutableMapOf(
            //region GUI
            "mainBoard" to "メインボード",
            "postEditor" to "投稿エディタ",
            "allPosts" to "投稿一覧",
            "myPosts" to "自分の投稿",
            //endregion

            //region Button
            "newPost" to "新規投稿",
            "deletedPosts" to "削除済み投稿",
            "aboutPlugin" to "このプラグインについて",
            "settings" to "設定",
            "help" to "ヘルプ",

            "savePost" to "投稿する",
            "cancelPost" to "投稿をキャンセル",
            //endregion

            //region Messages
            "pleaseEnterTitle" to "タイトルを入力して下さい: ",
            "pleaseEnterContent" to "内容を入力してください: ",
            //endregion

            //region Others
            "noTitle" to "タイトルがありません...",
            "noContent" to "コンテンツがありません...",
            "noPosts" to "ここには何もないようです...",
            //endregion

            //region How to Use
            "htuHeader" to "BulletinBoardの使い方",
            "htuOpenboard" to "メインボードを開くには /bb openboard を使用します。" +
                    "メインボードには、新規投稿、全投稿、自分の投稿を見ることができるアイコンがあります。",
            "htuNewPost" to "新しい投稿を作成するには /bb newpost を使用するか、メインボードからアイコンをクリックします。" +
                    "タイトル、コンテンツはチャット欄で入力します。GUIのアイテムをクリックすると一度GUIが閉じられ、" +
                    "入力を求められます。送信すると、それがタイトル、コンテンツに設定されます。" +
                    "すべて入力し終わったら、GUIから保存ができます。",
            "htuMyPosts" to "自分の投稿を見るには /bb myposts を使用するか、メインボードからアイコンをクリックします。" +
                    "自分の投稿が表示され、削除することもできます。また、投稿をクリックすると詳細を見ることができます。",
            "htuPosts" to "すべての投稿を見るには /bb posts を使用するか、メインボードからアイコンをクリックします。" +
                    "すべての投稿が表示され、クリックすると詳細を見ることができます。",
            "htuPreview" to "投稿のプレビューを見るには 投稿エディタで「プレビュー」をクリックします。" +
                    "プレビューを見ると、投稿の内容がチャット欄に表示されます。" +
                    "プレビューを閉じるには /bb previewclose を使用します。",
            "htuPreviewClose" to "投稿のプレビューを閉じるには /bb previewclose を使用します。" +
                    "このコマンドは、投稿エディタでプレビューをしている場合のみ使用できます。"
            //endregion
        ),
        "en" to mutableMapOf(
            //region GUI
            "mainBoard" to "Main Board",
            "postEditor" to "Post Editor",
            "allPosts" to "Posts",
            "myPosts" to "My Posts",
            //endregion

            //region Button
            "newPost" to "New Post",
            "deletedPosts" to "Deleted Posts",
            "aboutPlugin" to "About Plugin",
            "settings" to "Settings",
            "help" to "Help",

            "savePost" to "Save Post",
            "cancelPost" to "Cancel Post",
            //endregion

            //region Others
            "noTitle" to "No Title",
            "noContent" to "No Content",
            "noPosts" to "Nothing to see here...",
            //endregion

            //region How to Use
            "htuHeader" to "How to Use Bulletin Board",
            "htuOpenboard" to "To open the main board, use /bb openboard. The main board has icons for creating a new post, viewing all posts, and viewing your own posts.",
            "htuNewPost" to "To create a new post, use /bb newpost or click the icon on the main board. " +
                    "You will be prompted to enter a title and content in the chat. " +
                    "Clicking on the GUI items will close the GUI and prompt you to enter the information. " +
                    "Once you have entered all the information, you can save it from the GUI.",
            "htuMyPosts" to "To view your own posts, use /bb myposts or click the icon on the main board. " +
                    "Your posts will be displayed, and you can delete them. " +
                    "You can also click on a post to view more details.",
            "htuPosts" to "To view all posts, use /bb posts or click the icon on the main board. " +
                    "All posts will be displayed, and you can click on them to view more details.",
            "htuPreview" to "To preview a post, click on the \"Preview\" button in the post editor. " +
                    "The post content will be displayed in the chat. " +
                    "To close the preview, use /bb previewclose.",
            "htuPreviewClose" to "To close the post preview, use /bb previewclose. " +
                    "This command can only be used when previewing a post in the post editor."
            //endregion
        )
    )

    private fun getLanguage(player: Player): String {
        return player.locale.substring(0, 2)
    }

    // getMessage() returns a Component object
    fun getMessage(player: Player, key: String): Component {
        val language = getLanguage(player)
        val message = messages[language]?.get(key) ?: messages["en"]?.get(key) ?: key
        return Component.text(message)
    }

    // getContentFromMessage() returns a String object
    fun getContentFromMessage(player: Player, key: String): String {
        val language = getLanguage(player)
        val message = messages[language]?.get(key) ?: messages["en"]?.get(key) ?: key
        return message
    }
}