不处理路由键，所有绑定到该exchange的队列都会收到消息，很像广播模式。

NormalComsumer1：获取队列test_queue_info的消息(队列绑定test_fanout_logs交换机 使用的routeKey为info)
NormalComsumer1：获取队列test_queue_error的消息(队列绑定test_fanout_logs交换机 使用的routeKey为error)

主要测试: 生产者FanoutProducer 分别发送routeKey为error,info,warning的消息到交换机，
         消费者1和2，虽然分别绑定队列的roukey是info和error，但是由于使用的fanout类型的交换机，
         所以消费者1和2可以收到生产者发的到test_fanout_logs交换机的所有路由键(error,info,warning)的消息。