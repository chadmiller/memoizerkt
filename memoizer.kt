import java.io.File
import java.util.Base64

private class CachedValueNotAvailable(override var message:String): Exception(message)


abstract class StringFunctionMemoizer (val subject_function: (String) -> String) {
    var visibility_horizon_time = 0L

    fun expire_older_than_ms(time: Long) {
        this.visibility_horizon_time = time
    }

    fun expire_all() {
        expire_older_than_ms(System.currentTimeMillis())
    }

    abstract fun expire_one(name: String)

    abstract fun run(arg: String): String
}

class StringFunctionMemoizerToDisk (subject_function: (String) -> String, container: String = "memocache") : StringFunctionMemoizer(subject_function) {

    val container = File(container, Base64.getUrlEncoder().encodeToString(subject_function.toString().toByteArray())).also {
        dn -> dn.mkdirs()
    }

    override fun expire_one(name: String) {
        val storage = this.storage_location(name)
        storage.delete()
    }

    override fun run(arg: String): String {
        val storage = this.storage_location(arg)

        try {
            return fetch_from_storage(storage, arg)
        } catch (exc: CachedValueNotAvailable) {
            // We must calculate it. Do that next.
        }

        return subject_function(arg).also {
            result -> write_to_storage(storage, result)
        }
    }

    private fun fetch_from_storage(storage: File, name: String): String {
        try {
            if (storage.lastModified() < this.visibility_horizon_time) {
                throw CachedValueNotAvailable(name)
            }

            val input_stream = storage.inputStream()
            val cached_result = input_stream.bufferedReader().use { it -> it.readText() }
            return cached_result
        } catch (exc: java.io.FileNotFoundException) {
            throw CachedValueNotAvailable(name)
        } 
    }

    private fun write_to_storage(storage: File, value: String) {
        storage.writeText(value)
    }

    private fun storage_location(name: String): File {
        return File(this.container, Base64.getUrlEncoder().encodeToString(name.toByteArray()))
    }
}


class StringFunctionMemoizerToMemory (subject_function: (String) -> String) : StringFunctionMemoizer(subject_function) {

    class AnnotatedValue(val value: String) {
        val mtime: Long = System.currentTimeMillis()
    }

    val storage = HashMap<String,AnnotatedValue>()

    override fun expire_one(name: String) {
        storage.remove(name)
    }

    override fun run(arg: String): String {
        val item = storage.get(arg)
        if ((item != null) && (item.mtime < this.visibility_horizon_time)) {
            return item.value
        }

        return subject_function(arg).also {
            result -> storage.put(arg, AnnotatedValue(result))
        }
    }

    
}
