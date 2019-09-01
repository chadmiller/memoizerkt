run: KXCORO = -classpath /snap/kotlin/current/lib/kotlinx-coroutines-core-common-1.0.1.jar:/snap/kotlin/current/lib/kotlinx-coroutines-core-1.0.1.jar
run: example.jar
	kotlin ${KXCORO} $^

example.jar: KXCORO = -classpath /snap/kotlin/current/lib/kotlinx-coroutines-core-common-1.0.1.jar:/snap/kotlin/current/lib/kotlinx-coroutines-core-1.0.1.jar
example.jar: memoizer.kt example.kt
	kotlinc -include-runtime ${KXCORO} -verbose -d $@ $^

clean:
	rm -f example.jar
