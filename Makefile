run: example.jar
	java -jar $<

example.jar: example.kt memoizer.kt
	kotlinc $^ -include-runtime -d $@

clean:
	rm -f example.jar
	rm -rf /tmp/memocache
