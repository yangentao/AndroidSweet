package dev.entao.appbase

import dev.entao.json.YsonNull
import dev.entao.json.YsonObject
import java.io.File
import java.io.FileNotFoundException

class FileMap<V : Any>(filename: String, private val coder: ItemCoder) {

    private val file: File = AppFile.doc.file( filename)
    var map: HashMap<String, V> = HashMap(256)

    init {
        load()
    }

    @Suppress("UNCHECKED_CAST")
    private fun load() {
        this.map.clear()
        try {
            val s = this.file.readText()
            val ya = YsonObject(s)
            val newMap = HashMap<String, V>(ya.size)
            for ((k, v) in ya) {
                if (v !is YsonNull) {
                    val item = this.coder.decoder(v) ?: continue
                    newMap[k] = item as V
                }
            }
            this.map = newMap
        } catch (ex: FileNotFoundException) {

        }
    }

    fun save() {
        val yo = YsonObject(this.map.size)
        for ((k, v) in this.map) {
            val yv = this.coder.encoder(v)
            yo.data[k] = yv
        }
        file.writeText(yo.yson())
    }

}