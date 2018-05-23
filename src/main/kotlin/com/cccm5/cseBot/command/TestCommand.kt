package com.cccm5.cseBot.command

import com.cccm5.cseBot.util.Permission
import sx.blah.discord.handle.impl.obj.ReactionEmoji
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IGuild
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IUser

object TestCommand: Command {
    override val label: String = "Test"
    override val description: String = "tests the command function"
    override val aliases: List<String> = ArrayList()
    override val permission: Permission = Permission(label)

    private val ryanPanicEmoji = ReactionEmoji.of("ryanPanic",418275762110464001, true)

    override fun onCommand(sender: IUser, channel: IChannel, guild: IGuild, args: List<String>, message: IMessage) {
        message.addReaction(ryanPanicEmoji)
        //message.reply("Testing 123")
    }




}