# relay

Actor model implementation for data processing pipelines in Java.

This projects helps setup concurrent data-pipelines using actors and mailboxes,
typically to work in a producer-consumer fashion.


## Usage


### Quickstart

Imports:

```java
import java.util.concurrent.ExecutorService;

import kumarshantanu.relay.impl.DefaultActor;
import kumarshantanu.relay.impl.DefaultAgent;
import kumarshantanu.relay.impl.Util;
```

Instantiate an actor; send message to it:

```java
ExecutorService threadPool = Util.newThreadPool(); // instantiate a thread-pool
DefaultAgent ag = new DefaultAgent(threadPool);    // instantiate an agent
DefaultActor<String, String> actor = new DefaultActor<String, String> {
    @Override
    public String execute(String req) {
        return "Received message: " + req;        // actual processing here
    }
};                      // instantiate an actor
threadPool.execute(ag); // start the agent (same thread-pool not necessary)
actor.send("foo");      // send message to the actor
```

### Notes on mailboxes:

1. `Mailbox` is an interface, and the default implementation is in-memory queue.
2. For distributed pipelines you may like to implement Mailbox using RabbitMQ,
   HornetQ, Beanstalkd etc.


### Lifecycle support:

Agents and actors can be decorated with lifecycle support (implementing the
`LifecycleAware` interface), as in they support extra operations such as Start,
Suspend, Resume, Stop, ForceStop etc. Transition of lifecycle states vis-a-vis
the operations are listed below:

|  Old State   | Start   | Suspend   | Resume  |  Stop   |   ForceStop   |
|--------------|---------|-----------|---------|---------|---------------|
|READY         | RUNNING |    ---    |   ---   |   ---   |      ---      |
|RUNNING       |   ---   | SUSPENDED |   ---   | STOPPED | FORCE_STOPPED |
|SUSPENDED     |   ---   |    ---    | RUNNING | STOPPED | FORCE_STOPPED |
|STOPPED       | RUNNING |    ---    |   ---   |   ---   |      ---      |
|FORCE_STOPPED |   ---   |    ---    |   ---   |   ---   |      ---      |


Note: FORCE_STOPPED gets automatically transitioned to STOPPED state on effect.


## License

Copyright © 2012 Shantanu Kumar
([kumar.shantanu@gmail.com](mailto:kumar.shantanu@gmail.com) and
[@kumarshantanu](http://twitter.com/#!/kumarshantanu))

Distributed under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).
