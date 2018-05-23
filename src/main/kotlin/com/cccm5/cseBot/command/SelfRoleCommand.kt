package com.cccm5.cseBot.command

import com.cccm5.cseBot.selfRole.SelfRoleManager
import com.cccm5.cseBot.util.Paginator
import com.cccm5.cseBot.util.Permission
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.handle.obj.*
import sx.blah.discord.util.MessageBuilder
import sx.blah.discord.util.PermissionUtils

object SelfRoleCommand: Command {
    override val label = "SelfRole"
    override val description = "Add a role to yourself from a list of available roles"
    override val aliases = listOf<String>()
    override val permission = Permission(label)

    private val guildRoles = mutableMapOf<IGuild,SelfRoleManager>()
    /*{
        logger.info("Running selfRoleCommand init")
        for(guild in client.guilds){
            guildRoles[guild] = SelfRoleManager(guild)
        }
    }*/

    override fun onCommand(sender: IUser, channel: IChannel, guild: IGuild, args: List<String>, message: IMessage) {
        if(!PermissionUtils.hasPermissions(guild,message.client.ourUser,Permissions.MANAGE_ROLES)){
            MessageBuilder(message.client)
                    .withChannel(channel)
                    .withContent("Error:", MessageBuilder.Styles.BOLD)
                    .appendContent(" The bot lacks the permission to manage roles.")
                    .send()
            return
        }
        if(guild !in guildRoles){
            guildRoles[guild]=SelfRoleManager(guild)
        }

        if (args.isEmpty()){
            MessageBuilder(message.client)
                    .withChannel(channel)
                    .withContent("Error:", MessageBuilder.Styles.BOLD)
                    .appendContent(" No arguments supplied! Valid arguments include ")
                    .appendContent("list", MessageBuilder.Styles.ITALICS)
                    .appendContent(", ")
                    .appendContent("add", MessageBuilder.Styles.ITALICS)
                    .appendContent(", ")
                    .appendContent("remove", MessageBuilder.Styles.ITALICS)
                    .appendContent(", or any added ")
                    .appendContent("self role.", MessageBuilder.Styles.ITALICS)
                    .send()
            return
        }
        val roleNames = guildRoles[guild]?.getLabels()?:setOf()
        val subCommand = args.first().toLowerCase()
        when (subCommand) {
            "add"     -> addRole(   sender, channel, guild, message.client, args.drop(1))
            "remove"  -> removeRole(sender, channel, guild, message.client, args.drop(1))
            "list"    -> listRoles( sender, channel, guild, message.client, args.drop(1))
            in roleNames -> toggleRole(sender, channel, guild, message.client, args)
            else -> {
                if(args.joinToString(" ").toLowerCase() in roleNames){
                    toggleRole(sender, channel, guild, message.client, args)
                    return
                }
                MessageBuilder(message.client)
                        .withChannel(channel)
                        .withContent("Error:", MessageBuilder.Styles.BOLD)
                        .appendContent(" Invalid argument! Valid arguments include ")
                        .appendContent("list", MessageBuilder.Styles.ITALICS)
                        .appendContent(", ")
                        .appendContent("add", MessageBuilder.Styles.ITALICS)
                        .appendContent(", ")
                        .appendContent("remove", MessageBuilder.Styles.ITALICS)
                        .appendContent(", or any added ")
                        .appendContent("self role.", MessageBuilder.Styles.ITALICS)
                        .send()
                return
            }
        }
    }

    private fun addRole(sender: IUser, channel: IChannel, guild: IGuild, client: IDiscordClient, args: List<String>){
        if(args.isEmpty()){
            MessageBuilder(client)
                    .withChannel(channel)
                    .withContent("Error:", MessageBuilder.Styles.BOLD)
                    .appendContent(" you need to supply a rank to add.")
                    .send()
            return
        }
        val rankName = args.joinToString(" ").toLowerCase()

        val ranks = guild.getRolesByName(rankName,true)
        if(ranks.isEmpty()){
            MessageBuilder(client)
                    .withChannel(channel)
                    .withContent("Error:", MessageBuilder.Styles.BOLD)
                    .appendContent(" No ranks found by the name ")
                    .appendContent(rankName, MessageBuilder.Styles.ITALICS)
                    .appendContent(".")
                    .send()
            return
        }
        val roleNames = guildRoles[guild]?.getLabels()?: setOf()
        if(rankName in roleNames){
            MessageBuilder(client)
                    .withChannel(channel)
                    .withContent("Error:", MessageBuilder.Styles.BOLD)
                    .appendContent(" Rank already registered with name ")
                    .appendContent(rankName, MessageBuilder.Styles.ITALICS)
                    .appendContent(".")
                    .send()
            return
        }
        if(!PermissionUtils.isUserHigher(guild, sender, listOf(ranks[0]))){
            MessageBuilder(client)
                    .withChannel(channel)
                    .withContent("Error:", MessageBuilder.Styles.BOLD)
                    .appendContent(" Cannot add SelfRoles for roles of equal or higher position in the role hierarchy.")
                    .send()
            return
        }
        guildRoles[guild]!!.register(rankName,ranks[0])
        MessageBuilder(client)
                .withChannel(channel)
                .withContent("SelfRole added with the name ")
                .appendContent(rankName, MessageBuilder.Styles.ITALICS)
                .appendContent(".")
                .send()
        return
    }

    private fun removeRole(sender: IUser, channel: IChannel, guild: IGuild, client: IDiscordClient, args: List<String>){
        val roleNames = guildRoles[guild]?.getLabels()?: setOf()
        if(roleNames.isEmpty()){
            MessageBuilder(client)
                    .withChannel(channel)
                    .withContent("Error:", MessageBuilder.Styles.BOLD)
                    .appendContent(" No self ranks are registered by ")
                    .appendContent(guild.name, MessageBuilder.Styles.ITALICS)
                    .appendContent(".")
                    .send()
            return
        }
        if(args.isEmpty()){
            MessageBuilder(client)
                    .withChannel(channel)
                    .withContent("Error:", MessageBuilder.Styles.BOLD)
                    .appendContent(" you need to supply a rank to remove.")
                    .send()
            return
        }
        val roleName = args.joinToString(" ").toLowerCase()
        if(roleName !in roleNames){
            MessageBuilder(client)
                    .withChannel(channel)
                    .withContent("Error:", MessageBuilder.Styles.BOLD)
                    .appendContent(" No ranks found by the name ")
                    .appendContent(roleName, MessageBuilder.Styles.ITALICS)
                    .appendContent(".")
                    .send()
            return
        }
        if(!PermissionUtils.isUserHigher(guild, sender, listOf(guildRoles[guild]!![roleName]))){
            MessageBuilder(client)
                    .withChannel(channel)
                    .withContent("Error:", MessageBuilder.Styles.BOLD)
                    .appendContent(" Cannot add SelfRoles for roles of equal or higher position in the role hierarchy.")
                    .send()
            return
        }
        guildRoles[guild]!!.unRegister(roleName)
        MessageBuilder(client)
                .withChannel(channel)
                .withContent("SelfRole ")
                .appendContent(roleName, MessageBuilder.Styles.ITALICS)
                .appendContent(" has been removed.")
                .send()
    }

    private fun listRoles(sender: IUser, channel: IChannel, guild: IGuild, client: IDiscordClient, args: List<String>){
        val roleNames = guildRoles[guild]?.getLabels()?:setOf()
        if(roleNames.isEmpty()){
            MessageBuilder(client)
                    .withChannel(channel)
                    .withContent("Error:", MessageBuilder.Styles.BOLD)
                    .appendContent(" No self ranks are registered by ")
                    .appendContent(guild.name, MessageBuilder.Styles.ITALICS)
                    .appendContent(".")
                    .send()
            return
        }
        val page = (if(args.isEmpty()) 1 else args[0].toIntOrNull()?:1)-1
        val book = Paginator()
        book.addAll(roleNames)
        if(page >= book.size || page < 0){
            MessageBuilder(client)
                    .withChannel(channel)
                    .withContent("Error:", MessageBuilder.Styles.BOLD)
                    .appendContent(" Page index out of bounds. Valid range is 1-${book.size}")
                    .send()
            return
        }
        channel.sendMessage(book[page])
    }

    private fun toggleRole(sender: IUser, channel: IChannel, guild: IGuild, client: IDiscordClient, args: List<String>){
        if(!PermissionUtils.hasHierarchicalPermissions(guild,client.ourUser,sender)){
            MessageBuilder(client)
                    .withChannel(channel)
                    .withContent("Error:", MessageBuilder.Styles.BOLD)
                    .appendContent(" Cannot modify roles for a user of equal or higher position in the role hierarchy.")
                    .send()
            return
        }
        if (args.isEmpty()){
            MessageBuilder(client)
                    .withChannel(channel)
                    .withContent("Error:", MessageBuilder.Styles.BOLD)
                    .appendContent(" you need to supply a rank.")
                    .send()
            return
        }
        val roleName = args[0].toLowerCase()
        val roleNames = guildRoles[guild]?.getLabels()?:setOf()
        if (roleName !in roleNames){
            MessageBuilder(client)
                    .withChannel(channel)
                    .withContent("Error:", MessageBuilder.Styles.BOLD)
                    .appendContent(" you need to supply a rank.")
                    .send()
            return
        }
        val role = guildRoles[guild]!![roleName]!!
        if(role in guild.getRolesForUser(sender)){
            guild.removeUserRole(sender,role)
            MessageBuilder(client)
                    .withChannel(channel)
                    .withContent("removed role ")
                    .appendContent(roleName, MessageBuilder.Styles.ITALICS)
                    .appendContent(" from ")
                    .appendContent(sender.name, MessageBuilder.Styles.ITALICS)
                    .appendContent(".")
                    .send()
            return
        }
        guild.addUserRole(sender,role)
        MessageBuilder(client)
                .withChannel(channel)
                .withContent("Added role ")
                .appendContent(roleName, MessageBuilder.Styles.ITALICS)
                .appendContent(" to ")
                .appendContent(sender.name, MessageBuilder.Styles.ITALICS)
                .appendContent(".")
                .send()
    }

    private fun IGuild.addUserRole(user: IUser, role: IRole){
        this.editUserRoles(user, this.getRolesForUser(user).toTypedArray() + role)
    }

    private fun IGuild.removeUserRole(user: IUser, role: IRole){
        this.editUserRoles(user, this.getRolesForUser(user).toTypedArray().without(role))
    }

    private inline fun <reified T> Array<T>.without(t: T): Array<T>{
        val lst = this.toMutableList()
        lst.remove(t)
        return lst.toTypedArray()
    }

    private fun IGuild.getRolesByName(name: String, ignoreCase: Boolean): List<IRole>{
        if(!ignoreCase)
            return this.getRolesByName(name)
        val output = mutableListOf<IRole>()
        for(role in this.roles){
            if(role.name.equals(name,true)){
                output.add(role)
            }
        }
        return output
    }


}