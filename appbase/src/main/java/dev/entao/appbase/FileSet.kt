package dev.entao.appbase

import dev.entao.json.YsonArray
import dev.entao.json.YsonNull
import java.io.File
import java.io.FileNotFoundException

class FileSet<T : Any>(filename: String, private val coder: ItemCoder) {

    private val file: File = AppFile.doc.file( filename)
    var items: HashSet<T> = HashSet(256)

    init {
        load()
    }

    @Suppress("UNCHECKED_CAST")
    private fun load() {
        this.items.clear()
        try {
            val s = this.file.readText()
            val ya = YsonArray(s)
            val newSet = HashSet<T>(ya.size)
            for (yv in ya) {
                if (yv !is YsonNull) {
                    val item = this.coder.decoder(yv) ?: continue
                    newSet.add(item as T)
                }
            }
            this.items = newSet
        } catch (ex: FileNotFoundException) {

        }
    }

    fun save() {
        val ya = YsonArray(this.items.size)
        for (item in this.items) {
            val yv = this.coder.encoder(item)
            ya.add(yv)
        }
        file.writeText(ya.yson())
    }

}