package net.rk4z.bulletinboard

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import org.slf4j.Logger
import java.io.*

@Plugin(
    id = "bulletinboard",
    name = "BulletinBoard",
    version = "1.0.0",
    authors = ["Lars", "cotrin_d8"]
)
class BulletinBoard @Inject constructor(
    val server: ProxyServer,
    val logger: Logger,
    @DataDirectory val dataFolder: File
){
    companion object {
        lateinit var instance: BulletinBoard
            private set
        lateinit var dataBase: DataBase
            private set
    }

    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent) {
        instance = this
        dataBase = DataBase(this)

        if (dataBase.connectToDatabase()) {
            dataBase.createRequiredTables()
        }
    }

    @Subscribe
    fun onProxyStop(event: ProxyShutdownEvent) {

    }
}