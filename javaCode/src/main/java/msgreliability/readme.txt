rabbitmq的消息可靠性
消息流转途径：
[producer]--------------->[Exchange]------------>[queue]------------>[consumer]


[producer]---------->[Exchange] : 事务、生产者confirm
[producer]---------->[queue] : mandatory
[queue]------------->[consumer] : 消费者confirm
