Land
=================================================

一个简单的基于`ClassLoader`的用于依赖隔离（在`Java`中即是`Jar`）的容器实现。依赖容器自然要处理下面的问题：

* 可以从多处加载类，分配不同的`ClassLoader`
* `ClassLoader`之间有继承关系  
\# `ClassLoader`的继承关系会是一个树
* 类加载会在上下级`ClassLoader`之间有委托关系

`ClassLoader`委托的配置选项
---------------------------------------

* 是否允许在上级`ClassLoader`中查找类。   
即是否 **委托**。
* 允许在上级`ClassLoader`查找哪些类/包。   
即可以配置 **委托** 的粒度。
* 只允许在某级`ClassLoader`查找哪些类/包，会忽略这级`ClassLoader`的下级`ClassLoader`中这些类/包，不允许子`ClassLoader`。    
\# 即必须 **委托**。

目标
---------------------------------------

* 给出类加载委托情况的完备说明
* 给出类加载委托规则的规范描述。
* 给出类加载委托规则的规范描述的自己的一个描述格式。  
\# 对于我这个项目我会使用`Properties`来描述，简单够用。
* `Java`的`ClassLoader`的用途和限制
* 理出`ClassLoader`使用和实现的原则
* `ClassLoader`使用和实现容易出错的地方
* 使用了`ClassLoader`的常见框架
* 这些框架中`ClassLoader`的实现方法及其使用契约
* 给出`ClassLoader`实现方法及其使用契约的最佳实践

进阶目标
---------------------------------------

* 实现`OSGi`集成

相关资料
---------------------------------------

### 官方资料

#### ClassLoader

* [Class Loading](http://docs.oracle.com/javase/jndi/tutorial/beyond/misc/classloader.html)
* [Multithreaded Custom Class Loaders in Java SE 7](http://docs.oracle.com/javase/7/docs/technotes/guides/lang/cl-mt.html)
* [Understanding Extension Class Loading](http://docs.oracle.com/javase/tutorial/ext/basics/load.html)
* [Understanding WebLogic Server Application Classloading](http://docs.oracle.com/cd/E24329_01/web.1211/e24368/classloading.htm)
* [Java API doc - ClassLoader](http://docs.oracle.com/javase/7/docs/api/java/lang/ClassLoader.html)

#### Permission

* [Permissions in JDK 7](http://docs.oracle.com/javase/7/docs/technotes/guides/security/permissions.html)
* [Permissions in JDK 6](http://docs.oracle.com/javase/6/docs/technotes/guides/security/permissions.html)

### 二手资料

#### ClassLoader

* [The basics of Java class loaders](http://www.javaworld.com/article/2077260/learn-java/the-basics-of-java-class-loaders.html)
* [Java Security - Chapter 3. Java Class Loaders](http://docstore.mik.ua/orelly/java-ent/security/ch03_01.htm)
* [Java安全模型介绍](http://www.ibm.com/developerworks/cn/java/j-lo-javasecurity/)
* [Class.forName() vs ClassLoader.loadClass() - which to use for dynamic loading?](http://stackoverflow.com/questions/8100376/class-forname-vs-classloader-loadclass-which-to-use-for-dynamic-loading)

### ClassLoader Memory Leak

* [Classloader leaks: the dreaded "java.lang.OutOfMemoryError: PermGen space" exception](http://frankkieviet.blogspot.com/2006/10/classloader-leaks-dreaded-permgen-space.html)
* [How to fix the dreaded "java.lang.OutOfMemoryError: PermGen space" exception (classloader leaks)](http://frankkieviet.blogspot.com/2006/10/how-to-fix-dreaded-permgen-space.html)
* [Diagnosis of Java class loader memory leaks](http://www.ibm.com/developerworks/webservices/library/ws-javaclass/index.html)
* [Reloading Java Classes 201: How do ClassLoader leaks happen?](http://zeroturnaround.com/rebellabs/rjc201/)
