package com.cccm5.cseBot.util

import sx.blah.discord.handle.obj.IUser

/**
 * Represents a permission for a given guild
 */
data class Permission(val name: String)

fun IUser.hasPermission(permission: Permission): Boolean {
    return true
    //return this.getRolesForGuild(permission.guild).any { it in permission.roles }
}
