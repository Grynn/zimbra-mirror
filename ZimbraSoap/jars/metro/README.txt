Taken from https://metro.dev.java.net/1.5/metro-1_5.jar

There isn't a more up to date version of Metro 1.X as of 24th March 2011

This version of Metro was chosen because it supports JAX-WS 2.1 which is
what comes with JDK6.

Later versions of Metro like version 2.1 require JAX-WS 2.2 API.
Attempting to use metro 2.1 results in the following failure from wsimport:
You are running on JDK6 which comes with JAX-WS 2.1 API,
but this tool requires JAX-WS 2.2 API.
Use the endorsed standards override mechanism
(http://java.sun.com/javase/6/docs/technotes/guides/standards/),
or set xendorsed="true" on <wsimport>.

Setting xendorsed="true" moves things on a bit but the generated code ends
up with compilation errors

