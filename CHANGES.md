# Changes and TODO

## TODO

## 2012-Dec-?? / 0.1.0

* Abstractions
  * Actor
  * Agent
  * Callback
  * Mailbox
  * Worker
* Implementation
  * DefaultAgent (polls registered actors sequentially)
  * DefaultActor
  * AmbientActor
  * BatchActor
  * PollingActor
  * DefaultMailbox
  * JMSMailbox (tested with JMS API using embedded ActiveMQ broker)
* [TODO] Distinguish local and potentially-remote actors, possibly drop Callback
* Documentation
