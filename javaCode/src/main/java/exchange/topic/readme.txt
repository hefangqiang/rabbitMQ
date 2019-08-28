将路由键和某模式进行匹配. [info.order.A, info.order.B, info.order.C]
* : 匹配一个关键字，一层匹配。  比如info.*.A  可以匹配到info.order.A
# : 匹配多个关键字，多层匹配。  比如info.# 可以匹配到  info.order.A, info.order.B, info.order.C
