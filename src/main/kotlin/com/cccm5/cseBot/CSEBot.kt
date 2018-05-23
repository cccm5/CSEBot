package com.cccm5.cseBot

import com.cccm5.cseBot.command.BeanCommand
import com.cccm5.cseBot.command.RespectCommand
import com.cccm5.cseBot.command.SelfRoleCommand
import com.cccm5.cseBot.command.TestCommand
import org.slf4j.LoggerFactory
import sx.blah.discord.api.ClientBuilder
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.util.DiscordException
import java.io.File
import kotlin.properties.Delegates

internal val logger = LoggerFactory.getLogger("com.cccm5.cseBot.CSEBot")
internal var client: IDiscordClient by Delegates.notNull()
    private set
internal val keyMap : MutableMap<String,String> = mutableMapOf()
private val VALID_KEYS = setOf(
        "DISCORD_API_KEY",
        "REDDIT_USERNAME",
        "REDDIT_PASSWORD",
        "REDDIT_CLIENT_ID",
        "REDDIT_CLIENT_SECRET",
        "IMGUR_CLIENT_ID",
        "IMGUR_API_SECRET",
        "IMGUR_API_KEY")
//internal val jedis = Jedis("localhost")

fun main(args: Array<String>) {
    if(args.isEmpty()){
       logger.error("No tokens supplied")
        return
    }
    for(element in args){
        val splitElement= element.split(":")
        if (splitElement.size != 2){
            logger.error("Erroneous token supplied")
            return
        }
        val prefix = splitElement.first()
        if(prefix !in VALID_KEYS){
            logger.error("Erroneous token ID supplied")
            return
        }
        if(prefix in keyMap){
            logger.error("Duplicate token ID supplied")
            return
        }
        val suffix = splitElement.last()
        logger.info("prefix: $prefix Suffix: $suffix")
        keyMap[prefix] = suffix
    }

    if (keyMap.size != VALID_KEYS.size){
        logger.error("Not enough tokens supplied")
        return
    }

    val manager = CommandManager
    val tempClient  = createClient(keyMap["DISCORD_API_KEY"]!!,true)
    if(tempClient == null){
        logger.error("Client initialization failed")
        return
    }
    client = tempClient
    File("./data/guilds/").mkdirs()
    client.dispatcher.registerListener(CommandManager)
    manager.registerCommand(TestCommand)
    manager.registerCommand(RespectCommand)
    manager.registerCommand(SelfRoleCommand)
    manager.registerCommand(BeanCommand)
    logger.info("Hello, World")
}
fun createClient(token: String, login: Boolean): IDiscordClient? { // Returns a new instance of the Discord client
    val clientBuilder = ClientBuilder() // Creates the ClientBuilder instance
    clientBuilder.withToken(token) // Adds the login info to the builder
    return try {
        if (login) {
            clientBuilder.login() // Creates the client instance and logs the client in
        } else {
            clientBuilder.build() // Creates the client instance but it doesn't log the client in yet, you would have to call client.login() yourself
        }
    } catch (e: DiscordException) { // This is thrown if there was a problem building the client
        logger.error("Critical error: ", e)
        null
    }
}

