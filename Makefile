run: KXCORO = -classpath /snap/kotlin/current/lib/kotlinx-coroutines-core-common-1.0.1.jar:/snap/kotlin/current/lib/kotlinx-coroutines-core-1.0.1.jar
run: test-memoizer.jar
	kotlin ${KXCORO} $^

test-memoizer.jar: KXCORO = -classpath /snap/kotlin/current/lib/kotlinx-coroutines-core-common-1.0.1.jar:/snap/kotlin/current/lib/kotlinx-coroutines-core-1.0.1.jar
test-memoizer.jar: memoizer.kt test-memoizer.kt
	kotlinc -include-runtime ${KXCORO} -verbose -d $@ $^

clean:
	rm -f test-memoizer.jar
