package net.rk4z.bulletinboard.utils

sealed class MessageKey {
    class System : MessageKey() {
        enum class Log {
            LOADING,
            ENABLING,
            DISABLING,
            CHECKING_UPDATE;

        }

    }

    class Main : MessageKey() {
        enum class GUI {
            MAIN_BOARD,
            POST_EDITOR
            ;
        }
    }
}