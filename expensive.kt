

public fun append_foo(subject: String): String {
    Thread.sleep(1000)
    return subject + "-foo"
}

fun main(args: Array<String>) {
    var memoized_append_foo = StringFunctionMemoizerToDisk(::append_foo, "/tmp/memocache")
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

}

