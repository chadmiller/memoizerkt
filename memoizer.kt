import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

import java.util.Base64

class StringFunctionMemoizer (val subject_function: (String) -> String, container: String = "memocache") {

    val container = File(container, Base64.getUrlEncoder().encode(subject_function.toByteArray()).toString())

    fun run(arg: String): String {
        val cache_path = File(this.container, Base64.getUrlEncoder().encode(arg.toByteArray()).toString())
        
        println(cache_path)


        return subject_function(arg)
    }

}


/*
class Memoizer(val subject_function: (<IN_T>) -> <OUT_T>) {

    //public fun clear_all_older_than(
    //public fun clear_all_for_function((

    public fun run(arg: String) -> String {
        println("run string")
    }

    //public fun run(vararg arg: String, suspension = ) {
    //}


}

*/
