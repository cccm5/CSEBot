package com.cccm5.cseBot.command

import com.cccm5.cseBot.util.Permission
import com.cccm5.cseBot.util.nextInt
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IGuild
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.MessageBuilder
import java.util.*

object RespectCommand: Command {
    override val label = "f"
    override val description = "sells respect"
    override val aliases = arrayListOf<String>()
    override val permission = Permission(label)

    private val hearts = listOf("❤","💛","💙","💜","💚","🖤")
    private val random = Random()

    override fun onCommand(sender: IUser, channel: IChannel, guild: IGuild, args: List<String>, message: IMessage) {
        MessageBuilder(message.client)
                .withChannel(channel)
                .withContent(sender.name, MessageBuilder.Styles.BOLD)
                .appendContent(" has sold their respects ")
                .appendContent(hearts.random())
                .ifElse(args.isNotEmpty(),
                        { it.appendContent(" for ")
                                .appendContent(args.joinToString(" "), MessageBuilder.Styles.BOLD)})
                .send()
    }

    /**
     *
     */
    private fun <T> List<T>.random(): T{
        return this[random.nextInt(0 until this.size)]
    }

    /**
     * Represents an if else conditional for builder patterns
     *
     * @param [T] inferred type of the expression
     * @param [condition] determines which function to execute
     * @param [consequence] executed if [condition] is true
     * @param [alternative] executed if [condition] is false
     * @return an object of type [T] resulting from the evaluation of either [consequence] or [alternative]
     */
    private inline fun <T> T.ifElse(condition: Boolean, consequence: (T) -> T, alternative: (T) -> T = {it: T -> it}): T{
        if(condition){
            return consequence(this)
        }
        return alternative(this)
    }

}