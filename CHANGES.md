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
  * DefaultAgent (polls registered actors sequentially)
  * DefaultActor
  * BatchActor
  * PollingActor
  * DefaultMailbox
  * JMSMailbox (tested with JMS API using embedded ActiveMQ broker)
  * JMSActor [TODO] figure out how to mix transactions with JMS mailboxes
* [TODO] Documentation
