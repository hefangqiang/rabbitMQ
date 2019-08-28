消息与一个特定的路由键完全匹配.

生产者： 分别发送路由键为"error","info","warning"的消息到交换机test_direct_logs

NormalComsumer: 消费者获取队列test_queue_info中的消息。
                (队列绑定到交换机的路由键为info，所以该队列只能接收到消费者发送到Exchange 路由键为info的消息)

MultiBindConsumer: 消费者获取test_queue_multiBind中的消息。(多重绑定)
               (队列绑定到交换机的路由键为error,info,warning,所以该队列可以接收到消费者发送到Exchange 路由键为error,info,warning的消息)

MultiConsumerOneQueue1,2: 队列test_queue_multiconsumer,有多个消费者(一个队列，多个消费者)
                          (消息轮询发送到消费者1和2,其实就是多个消费者平摊队列中的消息)

MultiChannelConsumer: MultiChannelConsumer中开启一个连接，新建五个线程(Consumer获取消息)并传入该连接。(一个连接，多个信道，减少tcp连接创建)
