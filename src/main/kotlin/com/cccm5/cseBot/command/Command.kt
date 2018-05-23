package com.cccm5.cseBot.command

import com.cccm5.cseBot.util.Permission
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IGuild
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IUser

interface Command{
    val label: String
    val description: String
    val aliases : List<String>
    val permission : Permission

    /**
     * called whenever a user executes a command with a label matching <Code>label</Code>
     * @param [sender] the use who executed the command
     * @param [command] the command that was executed
     * @param [args] the arguments of the command
     */
    fun onCommand(sender: IUser, channel: IChannel, guild: IGuild, args: List<String>, message: IMessage)

}