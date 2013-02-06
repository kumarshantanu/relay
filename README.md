# relay

Actor model implementation for data processing pipelines in Java.

This projects helps setup concurrent data-pipelines using actors and mailboxes,
typically to work in a producer-consumer fashion.

**Important:** _Early days for the project. API and implementation might change._

## Usage


### Quickstart

Imports:

```java
import java.util.concurrent.ExecutorService;

import kumarshantanu.relay.impl.DefaultActor;
import kumarshantanu.relay.impl.DefaultAgent;
import kumarshantanu.relay.impl.Util;
```

Instantiate actors; send messages to them:

```java
ExecutorService threadPool = Util.newThreadPool(); // instantiate a thread-pool
DefaultAgent ag = new DefaultAgent(threadPool);    // instantiate an agent
final DefaultActor<String, String> actorB = new DefaultActor<String, String>() {
    public void act(String req) {
        String ret = "Received message: " + req;
        System.out.println(ret);
    }
};                      // instantiate actor B
DefaultActor<String, String> actorA = new DefaultActor<String, String>() {
    public void act(String req) {
        String ret = "Forwarding message: " + req;
        actorB.send(ret);
        System.out.println(ret);
    }
};                      // instantiate actor A
ag.register(actorA);
ag.register(actorB);
threadPool.execute(ag); // start the agent (same thread-pool not mandatory)
actorA.send("foo");     // send message to the actor
```

### Documentation

Please refer the doc/intro.md file in this repo.


## License

Copyright © 2012-2013 Shantanu Kumar
([kumar.shantanu@gmail.com](mailto:kumar.shantanu@gmail.com) and
[@kumarshantanu](http://twitter.com/#!/kumarshantanu))

Distributed under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).


## Acknowledgement

YourKit is kindly supporting open source projects with its full-featured Java Profiler.
YourKit, LLC is the creator of innovative and intelligent tools for profiling
Java and .NET applications. Take a look at YourKit's leading software products:
<a href="http://www.yourkit.com/java/profiler/index.jsp">YourKit Java Profiler</a> and
<a href="http://www.yourkit.com/.net/profiler/index.jsp">YourKit .NET Profiler</a>.
