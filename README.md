Land
=========================

一个简单的基于`ClassLoader`的用于依赖隔离（在`Java`中即是`Jar`）的容器实现。依赖容器自然要处理下面的问题：

* 可以从多处加载类
* `ClassLoader`有继承关系

继承关系具备下面的可配置选项：

* 是否允许在上级`ClassLoader`中查找类
* 允许在上级`ClassLoader`查找哪些类/包
* 只允许在某级`ClassLoader`查找哪些类/包  
\# 即会忽略这级`ClassLoader`的下级`ClassLoader`中这些类/包

相关资料
--------------------------

* [Class Loading](http://docs.oracle.com/javase/jndi/tutorial/beyond/misc/classloader.html)
* [Multithreaded Custom Class Loaders in Java SE 7](http://docs.oracle.com/javase/7/docs/technotes/guides/lang/cl-mt.html)
* [Understanding Extension Class Loading](http://docs.oracle.com/javase/tutorial/ext/basics/load.html)
* [Understanding WebLogic Server Application Classloading](http://docs.oracle.com/cd/E24329_01/web.1211/e24368/classloading.htm)
* [Permissions in JDK 7](http://docs.oracle.com/javase/7/docs/technotes/guides/security/permissions.html)
* [Permissions in JDK 6](http://docs.oracle.com/javase/6/docs/technotes/guides/security/permissions.html)
* [Java安全模型介绍](http://www.ibm.com/developerworks/cn/java/j-lo-javasecurity/)
