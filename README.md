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
final DefaultActor<String, String> actorB = new DefaultActor<String, String> {
    @Override
    public String execute(String req) {
        String ret = "Received message: " + req;
        System.out.println(ret);
        return ret;     // actual processing here
    }
};                      // instantiate actor B
DefaultActor<String, String> actorA = new DefaultActor<String, String> {
    @Override
    public String execute(String req) {
        String ret = "Forwarding message: " + req;
        actorB.send(ret);
        System.out.println(ret);
        return ret;     // actual processing here
    }
};                      // instantiate actor A
threadPool.execute(ag); // start the agent (same thread-pool not necessary)
actor.send("foo");      // send message to the actor
```

### Documentation

Please refer the doc/intro.md file in this repo.


## License

Copyright © 2012 Shantanu Kumar
([kumar.shantanu@gmail.com](mailto:kumar.shantanu@gmail.com) and
[@kumarshantanu](http://twitter.com/#!/kumarshantanu))

Distributed under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).
