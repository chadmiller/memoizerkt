/*

The primary interface to this memoizer is a `run` function that takes the
parameter to apply to the memoized function. It also implements functions to
cause future runs of the memoizer to get fresh results from the function, with
an `expire_older_than_ms` function that forgets aged values (defined by first
call*), `expire_one` that forgets a single instance's cached value, and
`expire_all` that forgets all unconditionally.

* The choice of which way to monitor and expire values based on time, probably
needs consideration in context of this code's usage. The time of last access is
just as likely to be the condition of interest, instead of first time of
access. The filesystem's "atime" or storing the current time at lookup will
change that behavior to the other way, to a kind of LRU cache.

----

To do:
 - Support varargs parameter instead of one parameter.
 - Maybe: Optionally make the age cache automatically maintained. Remove unused
   cached values older than some age.
*/


import java.io.File
import java.util.Base64

class MemoizeS<K, V> (val subject_function: suspend (K) -> V) {

    private var visibility_horizon_time = 0L

    private class AnnotatedValue<V>(val value: V) {
        var mtime = System.currentTimeMillis()
    }

    private val storage = HashMap<K, AnnotatedValue<V>>()

    fun expire_older_than_ms(time: Long) {
        this.visibility_horizon_time = time
    }

    fun expire_all() {
        storage.clear()
    }

    fun expire_one(name: K) {
        storage.remove(name)
    }

    suspend fun run(arg: K): V {
        val item = storage.get(arg)
        if ((item != null) && (item.mtime > this.visibility_horizon_time)) {
            item.mtime = System.currentTimeMillis()
            return item.value
        }

        return subject_function(arg).also {
            result -> storage.put(arg, AnnotatedValue(result))
        }
    }
}


/* Memoizer for mundane functions. */
class MemoizeF<K, V> (val subject_function: (K) -> V) {

    private var visibility_horizon_time = 0L

    private class AnnotatedValue<V>(val value: V) {
        val mtime: Long = System.currentTimeMillis()
    }

    private val storage = HashMap<K, AnnotatedValue<V>>()

    fun expire_older_than_ms(time: Long) {
        this.visibility_horizon_time = time
    }

    fun expire_all() {
        expire_older_than_ms(System.currentTimeMillis())
    }

    fun expire_one(name: K) {
        storage.remove(name)
    }

    fun run(arg: K): V {
        val item = storage.get(arg)
        if ((item != null) && (item.mtime < this.visibility_horizon_time)) {
            return item.value
        }

        return subject_function(arg).also {
            result -> storage.put(arg, AnnotatedValue(result))
        }
    }
}
