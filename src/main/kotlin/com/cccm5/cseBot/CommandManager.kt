package com.cccm5.cseBot

import com.cccm5.cseBot.command.Command
import com.cccm5.cseBot.util.hasPermission
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent

object CommandManager{
    const val prefix = "!"
    private val commands = hashMapOf<String,Command>()

    @EventSubscriber
    fun onMessage(event: MessageReceivedEvent){
        if (!event.message.content.startsWith(prefix))
            return
        val label = event.message.content.split(" ").first().drop(1).toLowerCase()
        if(label !in commands)
            return
        if(!event.author.hasPermission(commands[label]!!.permission)) {
            event.message.reply("you don't have permission for that")
            return
        }
        commands[label]!!.onCommand(
                event.author,
                event.channel,
                event.guild,
                event.message.content.split(" ").drop(1),
                event.message)
    }

    fun registerCommand(command: Command){
        if (command.label.toLowerCase() in commands){
            logger.warn("attempted to register command with pre existing label \"$(command.label)\"")
            //return
        }
        commands[command.label.toLowerCase()] = command
    }
}