package dev.entao.media

import android.net.Uri
import android.provider.MediaStore
import dev.entao.sql.RowData
import dev.entao.sql.UriQuery
import dev.entao.sql.listRowData

/**
 * Created by entaoyang@163.com on 16/5/12.
 */

class MediaInfo(val uri: Uri) {
    var displayName: String? = null
    var mimeType: String? = null
    var size = 0
    var width = 0
    var height = 0
    var record: RowData? = null
    var found = false


    init {
        ////content://media/external/images/media/10025
        if (uri.host == "media" && uri.scheme == "content") {
            val c = UriQuery(uri).queryOne()
            if (c != null) {
                val map = c.listRowData.firstOrNull()
                if (map != null) {
                    displayName = map.str(MediaStore.MediaColumns.DISPLAY_NAME)
                    mimeType = map.str(MediaStore.MediaColumns.MIME_TYPE)
                    size = map.int(MediaStore.MediaColumns.SIZE) ?: 0
                    width = map.int(MediaStore.MediaColumns.WIDTH) ?: 0
                    height = map.int(MediaStore.MediaColumns.HEIGHT) ?: 0
                    record = map
                    found = true
                }
            }

        }
    }

}
