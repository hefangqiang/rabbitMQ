qos服务质量
预取模式
如果不使用预取模式，则Rabbit会尽可能快速地发送队列中的所有消息到client端，client端容易OOM导致应用崩溃。

预取模式：consumer可以一次从rabbitmq中获取指定数量消息，缓存到client端，一旦缓冲区满了，
         Rabbit将会停止投递新的message到该consumer中直到它发出ack。

  1.假设prefetch值设置为10，共有两个consumer，意味着每个consumer每次从rabbitmq队列中取10条消息，放到本地缓存中等待消息，同时
  该channel的unacked数变为20。
  2.而rabbitmq的投递顺序是，先为consumer1投递满10条消息，然后再往consumer2投递10条消息。
  3.如果这时有新message需要投递，先判断channel的unacked数是否等于20，如果是则不会将消息投递到consumer中，message继续呆在queue中。
  之后其中consumer对一条消息进行ack，unacked此时等于19，Rabbit就判断哪个consumer的unacked少于10，就投递到哪个consumer中。
