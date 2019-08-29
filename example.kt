

public fun append_foo(subject: String): String {
    Thread.sleep(300)
    return subject + "-foo"
}

fun main(args: Array<String>) {
    var disk_memoized_append_foo = StringFunctionMemoizerToDisk(::append_foo, "/tmp/memocache")
    disk_memoized_append_foo.expire_all()
    disk_memoized_append_foo.expire_one("testing0")
    println(disk_memoized_append_foo.run("testing1"))
    disk_memoized_append_foo.expire_one("testing1")
    println(disk_memoized_append_foo.run("testing1"))
    println(disk_memoized_append_foo.run("testing1"))
    println(disk_memoized_append_foo.run("testing1"))
    println(disk_memoized_append_foo.run("testing1"))
    println(disk_memoized_append_foo.run("testing1"))
    disk_memoized_append_foo.expire_all()
    println(disk_memoized_append_foo.run("testing1"))
    println(disk_memoized_append_foo.run("testing2"))
    println(disk_memoized_append_foo.run("testing2"))
    println(disk_memoized_append_foo.run("testing3"))
    println(disk_memoized_append_foo.run("testing3"))
    println(disk_memoized_append_foo.run("testing2"))
    println(disk_memoized_append_foo.run("testing2"))
    println(disk_memoized_append_foo.run("testing3"))
    println(disk_memoized_append_foo.run("testing3"))


    var mem_memoized_append_foo = StringFunctionMemoizerToDisk(::append_foo)
    mem_memoized_append_foo.expire_all()
    mem_memoized_append_foo.expire_one("testing0")
    println(mem_memoized_append_foo.run("testing1"))
    mem_memoized_append_foo.expire_one("testing1")
    println(mem_memoized_append_foo.run("testing1"))
    println(mem_memoized_append_foo.run("testing1"))
    println(mem_memoized_append_foo.run("testing1"))
    println(mem_memoized_append_foo.run("testing1"))
    println(mem_memoized_append_foo.run("testing1"))
    mem_memoized_append_foo.expire_all()
    println(mem_memoized_append_foo.run("testing1"))
    println(mem_memoized_append_foo.run("testing2"))
    println(mem_memoized_append_foo.run("testing2"))
    println(mem_memoized_append_foo.run("testing3"))
    println(mem_memoized_append_foo.run("testing3"))
    println(mem_memoized_append_foo.run("testing2"))
    println(mem_memoized_append_foo.run("testing2"))
    println(mem_memoized_append_foo.run("testing3"))
    println(mem_memoized_append_foo.run("testing3"))
}

