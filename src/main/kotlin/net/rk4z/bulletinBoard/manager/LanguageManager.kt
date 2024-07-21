package net.rk4z.bulletinBoard.manager

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

@Suppress("unused", "DEPRECATION")
object LanguageManager {
    private val messages = mapOf(
        "ja" to mapOf(
            "main_board" to "メインボード",
            "post_editor" to "投稿エディタ",
            "post_editor_for_edit" to "編集用投稿エディタ",
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
        ),
        "en" to mapOf(
            "main_board" to "Main Board",
            "post_editor" to "Post Editor",
            "post_editor_for_edit" to "Post Editor for Edit",
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
            "htu_newpost" to "To create a new post, use /bb newpost or click the icon on the main board. " +
                    "You will enter the title and content in the chat. " +
                    "Clicking on an item in the GUI will close the GUI and prompt you for input. " +
                    "When you submit, it will be set as the title or content. " +
                    "Once you have finished entering everything, you can save from the GUI.",
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
        ),
        "fr" to mapOf(
            "main_board" to "Tableau Principal",
            "post_editor" to "Éditeur de Publication",
            "post_editor_for_edit" to "Éditeur de Publication pour Modifier",
            "my_posts" to "Mes Publications",
            "all_posts" to "Toutes les Publications",
            "select_post_to_delete" to "Sélectionnez la publication à supprimer",
            "delete_confirmation" to "Êtes-vous sûr de vouloir supprimer?",
            "confirmation" to "Confirmation",
            "new_post" to "Nouvelle Publication",
            "cancel_post" to "Annuler",
            "save_post" to "Enregistrer la Publication",
            "no_posts" to "Aucune publication disponible",
            "prev_page" to "Page Précédente",
            "next_page" to "Page Suivante",
            "back_button" to "Retour",
            "delete_post" to "Supprimer la Publication",
            "confirm_yes" to "Oui",
            "confirm_no" to "Non",
            "preview_of_post" to "Aperçu",
            "preview_message" to "Aperçu de votre publication:",
            "title_label" to "Titre: ",
            "content_label" to "Contenu: ",
            "author_label" to "Auteur:",
            "type_preview_close" to "Tapez /bb previewclose pour fermer l'aperçu et continuer.",
            "post_saved" to "Publication Enregistrée: ",
            "post_deleted" to "Publication supprimée avec succès.",
            "please_enter_title" to "Veuillez entrer le titre de votre publication:",
            "please_enter_content" to "Veuillez entrer le contenu de votre publication:",
            "no_title" to "Pas de titre",
            "no_content" to "Pas de contenu",
            "date_label" to "Date: ",
            "input_set" to "{inputType} a été défini sur {input}",
            "usage_openboard" to "Ouvrir le tableau principal",
            "usage_newpost" to "Ouvrir l'éditeur de publication",
            "usage_myposts" to "Voir vos publications",
            "usage_posts" to "Voir toutes les publications",
            "usage_previewclose" to "Fermer l'aperçu de la publication",
            "please_use_help" to "Veuillez utiliser /bb help pour obtenir une liste des sous-commandes",
            "htu_header" to "Comment Utiliser BulletinBoard",
            "htu_title" to "Vous trouverez ici des instructions sur la façon d'utiliser BulletinBoard.",
            "htu_openboard" to "Pour ouvrir le tableau principal, utilisez /bb openboard. " +
                    "Le tableau principal contient des icônes pour créer une nouvelle publication, voir toutes les publications, et voir vos propres publications.",
            "htu_newpost" to "Pour créer une nouvelle publication, utilisez /bb newpost ou cliquez sur l'icône sur le tableau principal. " +
                    "Vous entrerez le titre et le contenu dans le chat. " +
                    "Cliquer sur un élément dans l'interface graphique fermera l'interface et vous invitera à entrer les informations. " +
                    "Lorsque vous soumettez, cela sera défini comme le titre ou le contenu. " +
                    "Une fois que vous avez fini d'entrer tout, vous pouvez enregistrer depuis l'interface graphique.",
            "htu_myposts" to "Pour voir vos publications, utilisez /bb myposts ou cliquez sur l'icône sur le tableau principal. " +
                    "Vos publications seront affichées, et vous pouvez les supprimer. " +
                    "Vous pouvez également cliquer sur une publication pour voir plus de détails.",
            "htu_posts" to "Pour voir toutes les publications, utilisez /bb posts ou cliquez sur l'icône sur le tableau principal. " +
                    "Toutes les publications seront affichées, et vous pouvez cliquer dessus pour voir plus de détails.",
            "htu_preview" to "Pour voir un aperçu de votre publication, cliquez sur 'Aperçu' dans l'éditeur de publication. " +
                    "L'aperçu montrera le contenu de votre publication dans le chat. " +
                    "Pour fermer l'aperçu, utilisez /bb previewclose.",
            "htu_previewclose" to "Pour fermer l'aperçu de votre publication, utilisez /bb previewclose. " +
                    "Cette commande ne peut être utilisée que lorsque vous visualisez une publication."
        ),
        "ar" to mapOf(
            "main_board" to "اللوحة الرئيسية",
            "post_editor" to "محرر المنشورات",
            "post_editor_for_edit" to "محرر المنشورات للتحرير",
            "my_posts" to "منشوراتي",
            "all_posts" to "جميع المنشورات",
            "select_post_to_delete" to "اختر المنشور الذي تريد حذفه",
            "delete_confirmation" to "هل أنت متأكد أنك تريد الحذف؟",
            "confirmation" to "تأكيد",
            "new_post" to "منشور جديد",
            "cancel_post" to "إلغاء",
            "save_post" to "حفظ المنشور",
            "no_posts" to "لا توجد منشورات",
            "prev_page" to "الصفحة السابقة",
            "next_page" to "الصفحة التالية",
            "back_button" to "عودة",
            "delete_post" to "حذف المنشور",
            "confirm_yes" to "نعم",
            "confirm_no" to "لا",
            "preview_of_post" to "معاينة",
            "preview_message" to "معاينة منشورك:",
            "title_label" to "العنوان:",
            "content_label" to "المحتوى:",
            "author_label" to "المؤلف:",
            "type_preview_close" to "اكتب /bb previewclose لإغلاق المعاينة والمتابعة.",
            "post_saved" to "تم حفظ المنشور:",
            "post_deleted" to "تم حذف المنشور بنجاح.",
            "please_enter_title" to "يرجى إدخال عنوان المنشور:",
            "please_enter_content" to "يرجى إدخال محتوى المنشور:",
            "no_title" to "لا يوجد عنوان",
            "no_content" to "لا يوجد محتوى",
            "date_label" to "التاريخ:",
            "input_set" to "{inputType} تم تعيينه على {input}",
            "usage_openboard" to "افتح اللوحة الرئيسية",
            "usage_newpost" to "افتح محرر المنشورات",
            "usage_myposts" to "عرض منشوراتك",
            "usage_posts" to "عرض جميع المنشورات",
            "usage_previewclose" to "إغلاق معاينة المنشور",
            "please_use_help" to "يرجى استخدام /bb help للحصول على قائمة الأوامر الفرعية",
            "htu_header" to "كيفية استخدام BulletinBoard",
            "htu_title" to "هنا ستجد تعليمات حول كيفية استخدام BulletinBoard.",
            "htu_openboard" to "لفتح اللوحة الرئيسية، استخدم /bb openboard. " +
                    "تحتوي اللوحة الرئيسية على أيقونات لإنشاء منشور جديد، وعرض جميع المنشورات، وعرض منشوراتك الخاصة.",
            "htu_newpost" to "لإنشاء منشور جديد، استخدم /bb newpost أو انقر على الأيقونة في اللوحة الرئيسية. " +
                    "ستدخل العنوان والمحتوى في الدردشة. " +
                    "سيتم إغلاق واجهة المستخدم الرسومية (GUI) عند النقر على أحد العناصر، وسيطلب منك الإدخال. " +
                    "عند الإرسال، سيتم تعيينه كعنوان أو محتوى. " +
                    "بعد إدخال كل شيء، يمكنك الحفظ من خلال واجهة المستخدم الرسومية (GUI).",
            "htu_myposts" to "لعرض منشوراتك، استخدم /bb myposts أو انقر على الأيقونة في اللوحة الرئيسية. " +
                    "سيتم عرض منشوراتك ويمكنك حذفها. " +
                    "يمكنك أيضًا النقر على منشور لعرض مزيد من التفاصيل.",
            "htu_posts" to "لعرض جميع المنشورات، استخدم /bb posts أو انقر على الأيقونة في اللوحة الرئيسية. " +
                    "سيتم عرض جميع المنشورات، ويمكنك النقر عليها لعرض مزيد من التفاصيل.",
            "htu_preview" to "لعرض معاينة لمنشورك، انقر على 'معاينة' في محرر المنشورات. " +
                    "ستعرض المعاينة محتوى منشورك في الدردشة. " +
                    "لإغلاق المعاينة، استخدم /bb previewclose.",
            "htu_previewclose" to "لإغلاق معاينة منشورك، استخدم /bb previewclose. " +
                    "يمكن استخدام هذا الأمر فقط عند معاينة منشور."
        ),
        "ru" to mapOf(
            "main_board" to "Главная доска",
            "post_editor" to "Редактор постов",
            "post_editor_for_edit" to "Редактор постов для редактирования",
            "my_posts" to "Мои посты",
            "all_posts" to "Все посты",
            "select_post_to_delete" to "Выберите пост для удаления",
            "delete_confirmation" to "Вы уверены, что хотите удалить?",
            "confirmation" to "Подтверждение",
            "new_post" to "Новый пост",
            "cancel_post" to "Отмена",
            "save_post" to "Сохранить пост",
            "no_posts" to "Нет доступных постов",
            "prev_page" to "Предыдущая страница",
            "next_page" to "Следующая страница",
            "back_button" to "Назад",
            "delete_post" to "Удалить пост",
            "confirm_yes" to "Да",
            "confirm_no" to "Нет",
            "preview_of_post" to "Предварительный просмотр",
            "preview_message" to "Предварительный просмотр вашего поста:",
            "title_label" to "Заголовок: ",
            "content_label" to "Содержание: ",
            "author_label" to "Автор:",
            "type_preview_close" to "Введите /bb previewclose, чтобы закрыть предварительный просмотр и продолжить.",
            "post_saved" to "Пост сохранен: ",
            "post_deleted" to "Пост успешно удален.",
            "please_enter_title" to "Пожалуйста, введите заголовок вашего поста:",
            "please_enter_content" to "Пожалуйста, введите содержание вашего поста:",
            "no_title" to "Нет заголовка",
            "no_content" to "Нет содержания",
            "date_label" to "Дата: ",
            "input_set" to "{inputType} установлен на {input}",
            "usage_openboard" to "Открыть главную доску",
            "usage_newpost" to "Открыть редактор постов",
            "usage_myposts" to "Просмотреть ваши посты",
            "usage_posts" to "Просмотреть все посты",
            "usage_previewclose" to "Закрыть предварительный просмотр поста",
            "please_use_help" to "Пожалуйста, используйте /bb help для списка подкоманд",
            "htu_header" to "Как использовать BulletinBoard",
            "htu_title" to "Здесь вы найдете инструкции по использованию BulletinBoard.",
            "htu_openboard" to "Чтобы открыть главную доску, используйте /bb openboard. " +
                    "Главная доска имеет значки для создания нового поста, просмотра всех постов и просмотра ваших собственных постов.",
            "htu_newpost" to "Чтобы создать новый пост, используйте /bb newpost или нажмите на значок на главной доске. " +
                    "Вы введете заголовок и содержание в чат. " +
                    "Нажатие на элемент в GUI закроет GUI и предложит вам ввести данные. " +
                    "После отправки данные будут установлены как заголовок или содержание. " +
                    "После ввода всего вы можете сохранить из GUI.",
            "htu_myposts" to "Чтобы просмотреть ваши посты, используйте /bb myposts или нажмите на значок на главной доске. " +
                    "Ваши посты будут отображаться, и вы можете их удалить. " +
                    "Вы также можете нажать на пост, чтобы увидеть больше деталей.",
            "htu_posts" to "Чтобы просмотреть все посты, используйте /bb posts или нажмите на значок на главной доске. " +
                    "Все посты будут отображаться, и вы можете нажать на них, чтобы увидеть больше деталей.",
            "htu_preview" to "Чтобы просмотреть ваш пост, нажмите 'Предварительный просмотр' в редакторе постов. " +
                    "Предварительный просмотр покажет содержание вашего поста в чате. " +
                    "Чтобы закрыть предварительный просмотр, используйте /bb previewclose.",
            "htu_previewclose" to "Чтобы закрыть предварительный просмотр вашего поста, используйте /bb previewclose. " +
                    "Эта команда может использоваться только при предварительном просмотре поста."
        ),
        "zh" to mapOf(
            "main_board" to "主板",
            "post_editor" to "帖子编辑器",
            "post_editor_for_edit" to "编辑帖子编辑器",
            "my_posts" to "我的帖子",
            "all_posts" to "所有帖子",
            "select_post_to_delete" to "选择要删除的帖子",
            "delete_confirmation" to "你确定要删除吗？",
            "confirmation" to "确认",
            "new_post" to "新帖子",
            "cancel_post" to "取消",
            "save_post" to "保存帖子",
            "no_posts" to "没有可用的帖子",
            "prev_page" to "上一页",
            "next_page" to "下一页",
            "back_button" to "返回",
            "delete_post" to "删除帖子",
            "confirm_yes" to "是",
            "confirm_no" to "否",
            "preview_of_post" to "预览",
            "preview_message" to "你的帖子预览：",
            "title_label" to "标题：",
            "content_label" to "内容：",
            "author_label" to "作者：",
            "type_preview_close" to "输入 /bb previewclose 关闭预览并继续。",
            "post_saved" to "帖子已保存：",
            "post_deleted" to "帖子已成功删除。",
            "please_enter_title" to "请输入帖子标题：",
            "please_enter_content" to "请输入帖子内容：",
            "no_title" to "没有标题",
            "no_content" to "没有内容",
            "date_label" to "日期：",
            "input_set" to "{inputType} 已设置为 {input}",
            "usage_openboard" to "打开主板",
            "usage_newpost" to "打开帖子编辑器",
            "usage_myposts" to "查看你的帖子",
            "usage_posts" to "查看所有帖子",
            "usage_previewclose" to "关闭帖子预览",
            "please_use_help" to "请使用 /bb help 获取子命令列表",
            "htu_header" to "如何使用 BulletinBoard",
            "htu_title" to "这里你会找到如何使用 BulletinBoard 的说明。",
            "htu_openboard" to "要打开主板，请使用 /bb openboard。" +
                    "主板上有创建新帖子、查看所有帖子和查看你自己的帖子的图标。",
            "htu_newpost" to "要创建新帖子，请使用 /bb newpost 或点击主板上的图标。" +
                    "你将在聊天中输入标题和内容。" +
                    "点击 GUI 中的项目将关闭 GUI 并提示你输入。" +
                    "提交后，它将被设置为标题或内容。" +
                    "输入完所有内容后，你可以从 GUI 保存。",
            "htu_myposts" to "要查看你的帖子，请使用 /bb myposts 或点击主板上的图标。" +
                    "你的帖子将被显示，你可以删除它们。" +
                    "你还可以点击帖子查看更多详情。",
            "htu_posts" to "要查看所有帖子，请使用 /bb posts 或点击主板上的图标。" +
                    "所有帖子将被显示，你可以点击它们查看更多详情。",
            "htu_preview" to "要查看帖子预览，请点击帖子编辑器中的“预览”。" +
                    "预览将显示你的帖子内容在聊天中。" +
                    "要关闭预览，请使用 /bb previewclose。",
            "htu_previewclose" to "要关闭帖子预览，请使用 /bb previewclose。" +
                    "此命令仅在你正在预览帖子时可用。"
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
