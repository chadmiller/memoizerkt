import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.*

public fun append_foo(subject: String): String {
    Thread.sleep(3000)
    return subject + "-foo"
}

suspend public fun append_bar(subject: String): String {
    delay(3000)
    return subject + "-bar"
}

fun main(args: Array<String>) {
    var memoized_append_bar = MemoizeSuspFun(::append_bar)

    val channel = Channel<String>()
    runBlocking {
        launch {
            channel.send(memoized_append_bar.run("testing12"))
            channel.send(memoized_append_bar.run("testing10"))
            channel.send(memoized_append_bar.run("testing11"))
            channel.send(memoized_append_bar.run("testing11"))
            channel.send(memoized_append_bar.run("testing10"))
            channel.send(memoized_append_bar.run("testing11"))
            channel.send(memoized_append_bar.run("testing10"))
            channel.send(memoized_append_bar.run("testing10"))
            channel.send(memoized_append_bar.run("testing10"))
            channel.send(memoized_append_bar.run("testing12"))
            channel.send(memoized_append_bar.run("testing12"))
            channel.send(memoized_append_bar.run("testing11"))
            channel.send(memoized_append_bar.run("testing12"))
            channel.send(memoized_append_bar.run("testing11"))
            channel.send(memoized_append_bar.run("testing11"))
            channel.send(memoized_append_bar.run("testing12"))
            channel.send(memoized_append_bar.run("testing12"))
            channel.close()
        }
        for (y in channel) println(y)
    }
}

