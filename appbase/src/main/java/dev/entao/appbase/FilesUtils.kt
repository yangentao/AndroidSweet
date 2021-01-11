package dev.entao.appbase


inline fun <reified T : Any> FileListOf(filename: String): FileList<T> {
    return FileList(filename, ItemCoder(T::class))
}


inline fun <reified T : Any> FileSetOf(filename: String): FileSet<T> {
    return FileSet(filename, ItemCoder(T::class))
}

inline fun <reified T : Any> FileMapOf(filename: String): FileMap<T> {
    return FileMap(filename, ItemCoder(T::class))
}

