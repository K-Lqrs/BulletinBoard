package net.rk4z.bulletinboard.utils

import com.velocitypowered.api.command.CommandSource

enum class Commands(val execute: (CommandSource) -> Unit) {
    OPENBOARD({ source ->

    }),
    NEWPOST({ source ->

    }),
    MYPOSTS({ source ->

    }),
    POSTS({ source ->

    }),
    DELETEDPOSTS({ source ->

    }),
    HELP({ source ->

    }),
    ABOUT({ source ->

    }),
    HOWTOUSE({ source ->

    });

    companion object {
        fun fromString(name: String): Commands? {
            return entries.find { it.name.equals(name, ignoreCase = true) }
        }
    }
}

enum class EventType {
    OPENBOARD,
    NEWPOST,
    MYPOSTS,
    POSTS
}