# Introduction to relay

TODO: write [great documentation](http://jacobian.org/writing/great-documentation/what-to-write/)


## Overview

Relay is a Java actor model implementation for setting up data-processing
pipeline. _Messages_ are always passed asynchronously to the _actors_ and
orchestrated by an _agent_. You can specify the thread-pool for use by an
agent.

References:

* http://en.wikipedia.org/wiki/Actor__model
* http://channel9.msdn.com/Shows/Going+Deep/Hewitt-Meijer-and-Szyperski-The-Actor-Model-everything-you-wanted-to-know-but-were-afraid-to-ask


## Agents

An agent is a continuously running thread that invokes actors with messages. The
_DefaultAgent_ implementation polls and invokes actors on readiness. Also, it
sleeps when idling in order to free the CPU resources.


## Actors

The following _Actor_ implementations are provided:

* GenericActor - the standard actor that may use a local or a remote mailbox
* DefaultActor - actor implementation with local mailbox for use in most cases
* BatchActor   - batches up the mailbox messages on threshold; processes in bulk
* PollingActor - used to poll some state (instead of mailbox) for readiness
* DisposableActor - ephemeral actors with discreet number of messages in mailbox


## Mailbox

`Mailbox` is an interface, and the default implementation is an in-memory queue
called `DefaultMailbox`. For distributed pipelines you may like to implement
Mailbox using RabbitMQ, HornetQ, Beanstalkd etc.

* DefaultMailbox - based on local, in-memory queue
* JMSMailbox     - based on JMS queue or topic


## Lifecycle support:

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