Land
=================================================

:point_right: 一个简单的基于`ClassLoader`的用于依赖隔离（在`Java`中主要是`Jar`）的容器实现。

依赖容器自然要处理下面的问题：

* 可以从多处加载类，并分配不同的`ClassLoader`
* `ClassLoader`之间有继承关系  
\# `ClassLoader`的继承关系会是一个树
* 类加载会在上下级`ClassLoader`之间有委托关系：
    * 是否允许在上级`ClassLoader`中查找类。   
即是否 **委托**。
    * 允许在上级`ClassLoader`查找哪些类/包。   
即可以配置 **委托** 的粒度。
    * 只允许在某级`ClassLoader`查找哪些类/包，会忽略这级`ClassLoader`的下级`ClassLoader`中这些类/包，不允许子`ClassLoader`。    
\# 即必须 **委托**。


功能
---------------------------------------

### 1. 实现`ClassLoader`委托的完备配置选项

完备委托关系可以先分析只有父子2层`ClassLoader`间委托方式的情况：

对于一个类在两层父子`ClassLoader`间委托方式，按是否加载排列组合有4种情况：

* 父不加载，子不加载【00】    
可以用来显示禁止某些类的加载。实际应用中应该 ***很少***有用到。
* 父不加载，子加载【01】  
子自理，父里即使包含了相同的类也不会污染子。实际场景：
    * 用来`Tomcat`容器自用`Lib`，不会影响`Web`应用。
* 父加载，子不加载【10】  
一定使用的父的类。实际场景：
    * `Tomcat`容器的`Servlet` `API`，不允许被`Web`应用修改。
* 父加载，子加载【11】      
两者可以加载的情况下，按谁优先分成2种情况：
    * 父优先。【PC，Parent-Child】    
    即是`Java`缺省委托策略，代理模式。这个委托策略可以保证Java核心库的类型优先加载，Java 核心库的类的加载工作由引导类加载器来统一完成，保证了Java应用所使用的都是同一个版本的Java核心库的类，是互相兼容的。
    * 子优先。【CP，Child-Parent】    
    这种委托方式比较复杂，有引起库版本混乱的风险！:bomb: :no_good: 实际应用中应该 ***很少***有用到。

按上面2层委托关系约定，嵌套推广一下即可得到 包含 **任意层**`ClassLoader`的完备委托关系。:sparkles:

举个3层`ClassLoader`包含上面组合的例子说明一下：

***TODO***

### 2. 实现常用类加载方式

* 加载本地类目录或`Jar`文件
* 加载本地有类目录或`Jar`文件的目录
* 加载网络上的类    
\# 这个功能应该很少使用 :stuck_out_tongue_winking_eye: ，为了功能完整而说明。
* 加密类工具/加载加密的类    
\# 这个功能应该很少使用 :stuck_out_tongue_closed_eyes: ，为了功能完整而说明。

目标
---------------------------------------

* 给出类加载委托情况的完备说明
* 给出类加载委托规则的规范描述
* 给出类加载委托规则的规范描述的自己的一个描述格式  
\# 对于我这个项目我会使用`Properties`来描述，简单够用。
* 说明`Java`的`ClassLoader`的用途和限制
* 给出`ClassLoader`使用和实现的原则
* `ClassLoader`使用和实现容易出错的地方
* 整理出使用了`ClassLoader`的常见框架
* 说明这些框架中`ClassLoader`的实现方法及其使用契约
* 给出`ClassLoader`实现方法及其使用契约的最佳实践

进阶目标
---------------------------------------

* `Web`容器集成
* 实现`OSGi`规范

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

* [IBM DeveloperWorks - 深入探讨Java类加载器](https://www.ibm.com/developerworks/cn/java/j-lo-classloader/)。一篇非常不错的`ClassLoader`的介绍文章，并且对比介绍了
    * `Java` `SPI`的类加载策略。包含`JDBC`和`JAXP`为代表的2种方式。
    * `Tomcat`的类加载策略。
    * `OSGi`的类加载策略。
* [The basics of Java class loaders](http://www.javaworld.com/article/2077260/learn-java/the-basics-of-java-class-loaders.html)，给出了实现自定义`ClassLoader`
    * 要想覆盖的`ClassLoader`关键方法
    * 要遵循的基本约定
    * 要注意的安全问题
* [Getting Started with Javassist - Class Loader](http://www.csg.ci.i.u-tokyo.ac.jp/~chiba/javassist/tutorial/tutorial.html#load)
* [Wikipedia - Java Classloader](http://en.wikipedia.org/wiki/Java_Classloader)
* [Class.forName() vs ClassLoader.loadClass() - which to use for dynamic loading?](http://stackoverflow.com/questions/8100376/class-forname-vs-classloader-loadclass-which-to-use-for-dynamic-loading)

### 安全

* [IBM DeveloperWorks - Java安全模型介绍](http://www.ibm.com/developerworks/cn/java/j-lo-javasecurity/)
* [IBM DeveloperWorks - Java 授权内幕](http://www.ibm.com/developerworks/cn/java/j-javaauth/)  
\# 更多内容参见：[IBM DeveloperWorks - Java安全专题](https://www.ibm.com/developerworks/cn/java/j-security/)
* [Java Security - Chapter 3. Java Class Loaders](http://docstore.mik.ua/orelly/java-ent/security/ch03_01.htm)

### ClassLoader Memory Leak

* [Classloader leaks: the dreaded "java.lang.OutOfMemoryError: PermGen space" exception](http://frankkieviet.blogspot.com/2006/10/classloader-leaks-dreaded-permgen-space.html)
* [How to fix the dreaded "java.lang.OutOfMemoryError: PermGen space" exception (classloader leaks)](http://frankkieviet.blogspot.com/2006/10/how-to-fix-dreaded-permgen-space.html)
* [IBM DeveloperWorks - Diagnosis of Java class loader memory leaks](http://www.ibm.com/developerworks/webservices/library/ws-javaclass/index.html)
* [Reloading Java Classes 201: How do ClassLoader leaks happen?](http://zeroturnaround.com/rebellabs/rjc201/)
* [Classloader-Related Memory Issues](http://javabook.compuware.com/content/memory/problem-patterns/class-loader-issues.aspx)
