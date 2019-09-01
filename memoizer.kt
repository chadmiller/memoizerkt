/*

The primary interface to this memoizer is a `run` function that takes the
parameter to apply to the memoized function. It also implements functions to
cause future runs of the memoizer to get fresh results from the function, with
an `expire_older_than_ms` function that forgets aged values (defined by most
recent call), `expire_one` that forgets a single instance's cached value, and
`expire_all` that forgets all unconditionally.

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
        var atime = System.currentTimeMillis()
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

    suspend fun run(arg: K): V {
        val item = storage.get(arg)
        if ((item != null) && (item.atime > this.visibility_horizon_time)) {
            item.atime = System.currentTimeMillis()
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
        var atime = System.currentTimeMillis()
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
        if ((item != null) && (item.atime > this.visibility_horizon_time)) {
            item.atime = System.currentTimeMillis()
            return item.value
        }

        return subject_function(arg).also {
            result -> storage.put(arg, AnnotatedValue(result))
        }
    }
}

