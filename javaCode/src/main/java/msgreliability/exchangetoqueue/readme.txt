交换机到队列可靠性：
交换机推送消息到队列，如果此时rabbitmq宕机，则队列中的消息丢失，未保证队列中的消息不丢失，则需要队列进行持久化：
创建队列时，设置durable为true；