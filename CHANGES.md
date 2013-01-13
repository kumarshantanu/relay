# Changes and TODO

## TODO

* Let user specify max concurrency for actor - Agent.register(actor, 3);
* PrebatchActor for low-latency actors
* PriorityAgent with Agent.onSend(ActorID) sig for
* AmqpMailboxTest to demonstrate AMQP queue based mailboxes
* A way for local and remote actors to be supervised

## 2013-Jan-?? / 0.1.0

* Abstractions
  * Actor (Lifecycle aware)
  * Agent (Lifecycle aware)
  * Mailbox
  * Worker
* Implementation
  * DefaultAgent    - polls/invokes registered actors in round-robin order
  * DefaultActor    - actor with local mailbox
  * DisposableActor - decorator, for creating ephemeral, short-lived actor
  * BatchActor      - actor that buffers messages and processes in a batch
  * PollingActor    - actor without a mailbox; polls to find next job
  * DefaultMailbox  - local mailbox, used by DefaultActor
  * JMSMailbox      - JMS based mailbox (tested with embedded ActiveMQ broker)
  * JMSActor        - actor that uses JMSMailbox for message passing
* [TODO] Documentation
