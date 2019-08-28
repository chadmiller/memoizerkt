

public fun append_foo(subject: String): String {
    return subject + "-foo"
}

fun main(args: Array<String>) {

    var other = StringFunctionMemoizer(::append_foo)
    println(other.run("testing"))
    println(other.run("testing"))
    println(other.run("testing"))
    println(other.run("testing"))


    //var twre = Memoizer(::test_web_resource_existence)
    //println(twre.run("https://revl.com"))
    //println(twre.run("https://revl.com"))
    //println(twre.run("https://revl.com"))

}

