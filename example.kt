

public fun append_foo(subject: String): String {
    Thread.sleep(300)
    return subject + "-foo"
}

suspend public fun append_bar(subject: String): String {
    //delay(300)
    return subject + "-foo"
}

fun main(args: Array<String>) {
    var memoized_append_foo = Memoize<String, String>(::append_foo)
    memoized_append_foo.expire_all()
    memoized_append_foo.expire_one("testing0")
    println(memoized_append_foo.run("testing1"))
    memoized_append_foo.expire_one("testing1")
    println(memoized_append_foo.run("testing1"))
    println(memoized_append_foo.run("testing1"))
    println(memoized_append_foo.run("testing1"))
    println(memoized_append_foo.run("testing1"))
    println(memoized_append_foo.run("testing1"))
    memoized_append_foo.expire_all()
    println(memoized_append_foo.run("testing1"))
    println(memoized_append_foo.run("testing2"))
    println(memoized_append_foo.run("testing2"))
    println(memoized_append_foo.run("testing3"))
    println(memoized_append_foo.run("testing3"))
    println(memoized_append_foo.run("testing2"))
    println(memoized_append_foo.run("testing2"))
    println(memoized_append_foo.run("testing3"))
    println(memoized_append_foo.run("testing3"))

    /*
    var disk_memoized_append_bar = StringFunctionMemoizer(::append_bar, "/tmp/memocache")
    runBlocking {
        println(mem_memoized_append_bar.run("testing10"))
        println(mem_memoized_append_bar.run("testing11"))
        println(mem_memoized_append_bar.run("testing12"))
    }
    */
}

