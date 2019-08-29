/*

Chad installed Kotlin, learned a bit of it, and wrote this in 5 hours. It is
what he would actually recommend, but it is all he can do with the knowledge he
has of Kotlin so far.

The correct way, he thinks, would be to make a kind of factory of memoizers,
that takes the function to memoize as a parameter, and uses reflection to
construct the object that can map the input parameters to the return value, not
just String->String as in the problem example. New memoizers should be rare, so
the memoizer-instantiation cost of a function's reflection should be negligible.

This is has only an implementation for a regular function, and the signature
for the class constructor can't take suspending functions. A secondary
constructor could store the function in a different variable and set a state
parameter that directs the run() along a different code path for the cache-miss
case, suspending properly. Chad stopped at the time limit, instead of
implementing this.

----

The primary interface to this memoizer is a `run` function that takes the
parameter to apply to the memoized function. It also implements functions to
cause future runs of the memoizer to get fresh results from the function, with
an `expire_older_than_ms` function that forgets aged values (defined by first
call*), `expire_one` that forgets a single instance's cached value, and
`expire_all` that forgets all unconditionally.

There are two instances here of backing of the initialization. For results that
are enormous, the on-disk storage is probably the best choice, and for small or
speedy memoization one should choose the in-memory one.  A future change should
be to combine the two to make enormous ones stored to disk and small one stored
in memory.


* The choice of which way to monitor and expire values based on time, probably
needs consideration in context of this code's usage. The time of last access is
just as likely to be the condition of interest, instead of first time of
access. The filesystem's "atime" or storing the current time at lookup will
change that behavior to the other way, to a kind of LRU cache.

----

To do:
 - Support suspending functions!
 - Use reflection to make parameters and return values arbitrarily supportable.
 - Combine disk and memory to keep most in memory unless very big (and maybe
   often-accessed). Learn from users about needs.
 - Maybe: Optionally make the age cache automatically maintained. Remove unused
   cached values older than some age.
*/


import java.io.File
import java.util.Base64

private class CachedValueNotAvailable(override var message:String): Exception(message)


abstract class StringFunctionMemoizer (val subject_function: (String) -> String) {

    var visibility_horizon_time = 0L

    public fun expire_older_than_ms(time: Long) {
        this.visibility_horizon_time = time
    }

    public fun expire_all() {
        expire_older_than_ms(System.currentTimeMillis())
    }

    abstract public fun expire_one(name: String)

    abstract public fun run(arg: String): String
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
        // FIXME: Probably not atomic. Write to a temp file and rename it into
        // place.
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
