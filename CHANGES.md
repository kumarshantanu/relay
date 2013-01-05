# Changes and TODO

## TODO

* PrebatchActor for low-latency actors
* PriorityAgent with Agent.onSend(ActorID) sig for
* AmqpMailboxTest to demonstrate AMQP queue based mailboxes

## 2013-Jan-?? / 0.1.0

* Abstractions
  * Actor
  * Agent
  * Callback
  * Mailbox
  * Worker
* Implementation
  * DefaultAgent (polls registered actors sequentially)
  * DefaultActor
  * BatchActor
  * PollingActor
  * DefaultMailbox
  * JMSMailbox (tested with JMS API using embedded ActiveMQ broker) [TODO] 2-way messaging with Future<T>
* Documentation
