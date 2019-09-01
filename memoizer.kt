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

class MemoizeSuspFun<K, V> (val subject_function: suspend (K) -> V) {
    /** Memoize a suspending function that takes a single hashable parameter.  */

    private var visibility_horizon_time = 0L

    private class AnnotatedValue<V>(val value: V) {
        var atime = System.currentTimeMillis()
    }

    private val storage = java.util.Collections.synchronizedMap(HashMap<K, AnnotatedValue<V>>())

    fun expire_older_than_ms(time: Long) {
        /** Calls against the memoized function that have not been used since
         * given point in time (expressed in milliseconds since epoch) are no
         * longer returned. 
         */
        this.visibility_horizon_time = time
    }

    fun expire_all() {
        /** Expunge all cached return values. */
        storage.clear()
    }

    fun expire_one(name: K) {
        /** Expunge the cached return value for the call of a single value. */
        storage.remove(name)
    }

    suspend fun run(arg: K): V {
        /** Run the memoized function with a given parameter. */
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
class MemoizeFun<K, V> (val subject_function: (K) -> V) {
    /** Memoize a regular function that takes a single hashable parameter.  */

    private var visibility_horizon_time = 0L

    private class AnnotatedValue<V>(val value: V) {
        var atime = System.currentTimeMillis()
    }

    private val storage = java.util.Collections.synchronizedMap(HashMap<K, AnnotatedValue<V>>())

    fun expire_older_than_ms(time: Long) {
        /** Calls against the memoized function that have not been used since
         * given point in time (expressed in milliseconds since epoch) are no
         * longer returned. 
         */
        this.visibility_horizon_time = time
    }

    fun expire_all() {
        /** Expunge all cached return values. */
        storage.clear()
    }

    fun expire_one(name: K) {
        /** Expunge the cached return value for the call of a single value. */
        storage.remove(name)
    }

    fun run(arg: K): V {
        /** Run the memoized function with a given parameter. */
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

