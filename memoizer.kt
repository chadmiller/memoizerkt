/*

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

* The choice of which way to monitor and expire values based on time, probably
needs consideration in context of this code's usage. The time of last access is
just as likely to be the condition of interest, instead of first time of
access. The filesystem's "atime" or storing the current time at lookup will
change that behavior to the other way, to a kind of LRU cache.

----

To do:
 - Support suspending functions!
 - Maybe: Optionally make the age cache automatically maintained. Remove unused
   cached values older than some age.
*/


import java.io.File
import java.util.Base64

class Memoize<K, V> (val subject_function: (K) -> V) {

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
