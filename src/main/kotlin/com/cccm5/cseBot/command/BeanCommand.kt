package com.cccm5.cseBot.command

import com.cccm5.cseBot.client
import com.cccm5.cseBot.keyMap
import com.cccm5.cseBot.logger
import com.cccm5.cseBot.util.Permission
import com.cccm5.cseBot.util.nextInt
import net.dean.jraw.RedditClient
import net.dean.jraw.http.OkHttpNetworkAdapter
import net.dean.jraw.http.UserAgent
import net.dean.jraw.models.Submission
import net.dean.jraw.models.SubredditSort
import net.dean.jraw.oauth.Credentials
import net.dean.jraw.oauth.OAuthHelper
import org.apache.commons.io.FileUtils
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IGuild
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.MessageBuilder
import java.io.File
import java.net.URL
import java.util.*


object BeanCommand: Command{
    override val label = "bean"
    override val description = "gets a picture that makes bean cry"
    override val aliases = listOf<String>()
    override val permission = Permission(label)

    private val redditClient: RedditClient
    //private val deviceId = UUID.randomUUID()
    private val subbredits =
            listOf("aww",
                    "RarePuppers",
                    "Eyebleach",
                    "cats",
                    "Otterable",
                    "Rabbits",
                    "kitty",
                    "babyelephantgifs",
                    "tippytaps",
                    "holdmycatnip")
    private val legalExtensions = setOf(".gif", ".png", ".jpg", ".mp4", ".jpeg")
    private val random = Random()

    private var submissions = mutableListOf<Submission>()
    private var lastUpdate: Long = 0


    init{
        //Create our user agent
        val userAgent = UserAgent("bot", "com.cccm5.cseBot", "v0.1", "cccm5dev")
        // Create our credentials
        val credentials = Credentials.script(keyMap["REDDIT_USERNAME"]!!,keyMap["REDDIT_PASSWORD"]!!,keyMap["REDDIT_CLIENT_ID"]!!,keyMap["REDDIT_CLIENT_SECRET"]!!)
        // This is what really sends HTTP requests
        val adapter = OkHttpNetworkAdapter(userAgent)
        // Authenticate and get a RedditClient instance

        redditClient = OAuthHelper.automatic(adapter, credentials)

    }

    override fun onCommand(sender: IUser, channel: IChannel, guild: IGuild, args: List<String>, message: IMessage) {
        if(subbredits.size < 2){
            MessageBuilder(client)
                    .withChannel(channel)
                    .withContent("Error:", MessageBuilder.Styles.BOLD)
                    .appendContent(" Too few subbredits!")
                    .send()
            return
        }
        for(i in 0..4) {
            val time = System.nanoTime()
            if (submissions.isEmpty() || time > lastUpdate + 3.6e12) {
                logger.info("Re-polling reddit for animal pictures. Current submissions: ${submissions.size} Time since last update: ${(time - lastUpdate) / 3.6e12} hours")
                lastUpdate = time
                val paginator = redditClient
                        .subreddits(subbredits.first(), subbredits[1], *(subbredits.drop(2).toTypedArray()))
                        .posts()
                        .limit(100)
                        .sorting(SubredditSort.HOT)
                        .build()
                submissions = paginator.next()
            }

            val post = submissions.randomPop()
            if (post == null) {
                /*MessageBuilder(client)
                        .withChannel(channel)
                        .withContent("Error:", MessageBuilder.Styles.BOLD)
                        .appendContent(" No posts found!")
                        .send()
                return*/
                continue
            }
            if (post.isSelfPost) {
                /*MessageBuilder(client)
                        .withChannel(channel)
                        .withContent("Error:", MessageBuilder.Styles.BOLD)
                        .appendContent(" no media found!")
                        .send()
                return*/
                continue
            }
            val media = post.embeddedMedia
            if (media == null) {
                if(sendUrlAsFile(channel, post.url))
                    return
                continue
            }

            val image = media.oEmbed
            if (image != null) {
                if (image.type == "photo") {
                    if(sendUrlAsFile(channel, image.url!!))
                        return
                    continue
                }
                if(sendUrlAsFile(channel, post.url))
                    return
                continue
            }
            val video = media.redditVideo
            if (video != null) {
                if(sendUrlAsFile(channel, video.fallbackUrl, ".mp4"))
                    return
                continue
            }
            /*MessageBuilder(client)
                    .withChannel(channel)
                    .withContent("Error:", MessageBuilder.Styles.BOLD)
                    .appendContent(" image not found!")
                    .send()
            return*/
            continue
        }
        MessageBuilder(client)
                .withChannel(channel)
                .withContent("Error:", MessageBuilder.Styles.BOLD)
                .appendContent(" severe error, try again!")
                .send()
    }

    private fun <T> List<T>.random() = if(this.isEmpty()) null else this[random.nextInt(0 until this.size)]

    private fun <T> MutableList<T>.randomPop() = if(this.isEmpty()) null else this.removeAt(random.nextInt(0 until this.size))

    private fun getTypeExtension(url: String): String{
        return url.substring(url.lastIndexOf("."))
    }

    private tailrec fun (()->(Boolean)).tryUntilSuccess(tries: Int = 5){
        require(tries < 0) {
            "tries ($tries) must be greater than 0"
        }
        if(tries == 0){
            return
        }
        if(!this()){
            this.tryUntilSuccess(tries-1)
        }
    }
    private tailrec fun <T> (()->(T)).loop(predicate: (T)->(Boolean)){
        if(predicate(this()))
            return
        this.loop(predicate)
    }


    private fun sendUrlAsFile(channel: IChannel, url: String, extension: String = ""): Boolean{
        /*val fixedUrl = if(url.contains("imgur.com",true) && !url.contains("gallery",true)){
            val image = getImageFromId(url.substringAfterLast("/"))
            if(image == null){
                logger.info("Error in imgur lookup")
                url
            } else image.link
        } else url*/

        var actualExtension = if(extension=="") getTypeExtension(url) else extension
        val fixedUrl = if(actualExtension == ".gifv" && url.startsWith("https://i.imgur.com/")) {
            actualExtension = ".gif"
            url.replaceAfterLast(".","gif")
        } else {
            url
        }
        //If the extension is not legal discord will not display it correctly
        if(actualExtension !in legalExtensions){
            channel.sendMessage(fixedUrl)
            return true
        }
        val file = File.createTempFile("$label-", actualExtension)
        //Read the file with limits to make sure we don't download a huge file
        FileUtils.copyURLToFile(URL(fixedUrl),file,1500,1500)
        if(file.length() <= 8000000){
            channel.sendFile(file)
            file.delete()
        }else{
            /*MessageBuilder(client)
                    .withChannel(channel)
                    .withContent("Error:", MessageBuilder.Styles.BOLD)
                    .appendContent(" file to large!")
                    .send()*/
            file.delete()
            return false
        }
        return true
    }
}