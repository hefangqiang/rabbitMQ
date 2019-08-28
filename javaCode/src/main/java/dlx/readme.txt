死信交换器
和其他普通交换区没啥区别，只不过存放一些特殊场景的消息,
利用 dlx，当消息在一个队列中变成死信 (dead message) 之后，它能被重新 publish 到另一个 exchange，这个 exchange 就是 dlx.

消息变成死信一般是以下几种情况：
•	消息被拒绝，并且设置 requeue 参数为 false
•	消息过期
•	队列达到最大长度

