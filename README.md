# relay

Actor model implementation for data processing pipelines in Java.

This projects helps setup concurrent data-pipelines using actors and mailboxes,
typically to work in a producer-consumer fashion.


## Usage

### Steps for Basic implementation:

1. Instantiate an agent. (`DefaultAgent` uses fully asynchonous strategy.)
2. Instantiate workers (`Worker`), callbacks (`Callback`) and actors (`VolumeActor` and `AmbientActor`).
3. Start the agent.

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
