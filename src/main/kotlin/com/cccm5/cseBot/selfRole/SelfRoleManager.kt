package com.cccm5.cseBot.selfRole

import com.cccm5.cseBot.logger
import org.yaml.snakeyaml.Yaml
import sx.blah.discord.handle.obj.IGuild
import sx.blah.discord.handle.obj.IRole
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter

class SelfRoleManager(private val guild: IGuild){
    private val yaml = Yaml()
    private val guildFolder = "./data/guilds/${guild.stringID}/"
    private val yamlPath = guildFolder + "self roles.yml"
    //private val jedisKey = "guild:" + guild.stringID + ":self.role"
    private val labeledRoles: MutableMap<String,IRole> = mutableMapOf()


    init {
        File(guildFolder).mkdirs()
        if(!File(yamlPath).exists()){
            yaml.dump(labeledRoles, FileWriter(yamlPath))
            logger.info("Created self roles file for guild ${guild.name}")
        }else{
            val stream = FileInputStream(File(yamlPath))
            labeledRoles.putAll((yaml.load(stream) as Map<String, Long>).mapValues { guild.getRoleByID(it.value) })
            stream.close()
            logger.info("Loaded ${labeledRoles.size} roles for guild ${guild.name}")
        }
        //labeledRoles.putAll(jedis.hgetAll(jedisKey).mapValues{guild.getRoleByID(it.key.toLong())})
    }
    fun register(label: String, role: IRole){
        val lowerCaseLabel = label.toLowerCase()
        if(lowerCaseLabel in this) {
            return
        }
        if (guild.getRolesByName(lowerCaseLabel)==null){
            return
        }
        //jedis.hset(jedisKey, lowerCaseLabel, role.longID.toString())
        labeledRoles[lowerCaseLabel]=role
        val writer = FileWriter(yamlPath)
        yaml.dump(labeledRoles.mapValues { it.value.longID }, writer)
        writer.close()
    }

    fun unRegister(label: String){
        val lowerCaseLabel = label.toLowerCase()
        if(label !in this){
            return
        }
        //jedis.hdel(jedisKey,lowerCaseLabel)
        labeledRoles.remove(lowerCaseLabel)
        val writer = FileWriter(yamlPath)
        yaml.dump(labeledRoles.mapValues { it.value.longID }, writer)
        writer.close()
    }

    fun getLabels(): Set<String>{
        return labeledRoles.keys
    }

    operator fun contains(label: String): Boolean{
        return label.toLowerCase() in labeledRoles
    }

    operator fun get(label: String): IRole?{
        return labeledRoles[label]
    }
}