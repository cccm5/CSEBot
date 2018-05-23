package com.cccm5.cseBot.util

import sx.blah.discord.api.internal.json.objects.EmbedObject
import sx.blah.discord.util.EmbedBuilder
import java.lang.Integer.min

private const val ENTRIES_PER_PAGE = 10
class Paginator{
    private val entries: MutableList<String> = mutableListOf()

    val size: Int
        get() = entries.size ceilDivide ENTRIES_PER_PAGE

    fun add(entry: String): Boolean {
       return entries.addSorted(entry)
    }

    fun addAll(lst: Collection<String>): Boolean {
        return entries.addAllSorted(lst)
    }

    operator fun get(index: Int): EmbedObject{
        return EmbedBuilder()
                .withTitle("Page: ${index+1}/$size")
                .withColor(255,255,255)
                .forEach(index * ENTRIES_PER_PAGE until min((index + 1) * ENTRIES_PER_PAGE,entries.size),
                        {builder: EmbedBuilder, entryIndex: Int ->
                            builder.appendDesc("${(entryIndex + 1)}. ${entries[entryIndex]}\n")})
                .build()

    }

    //private fun getEntry(index: Int) = entries[index]


}

private inline fun <T,K> T.forEach(iterable: Iterable<K>, function: (T, K) -> T): T{
    var output: T = this
    for(i in iterable){
        output = function(output,i)
    }
    return output
}

private fun <T: Comparable<T>> MutableList<T>.addSorted(element: T): Boolean{
    val updated = this.add(element)
    this.sort()
    return updated
}

private fun <T: Comparable<T>> MutableList<T>.addAllSorted(elements: Collection<T>): Boolean{
    var updated = false
    for(element in elements){
        if(this.add(element))
            updated = true
    }
    this.sort()
    return updated
}

/*
private inline fun <T> T.with(range: IntRange, function: (T,Int) -> T): T{
    var output: T = this
    for(i in range){
        output = function(output,i)
    }
    return output
}*/
