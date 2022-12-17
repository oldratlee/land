# <div align="center">🍡 Land</div>

<p align="center">
<a href="https://github.com/oldratlee/land/actions/workflows/ci.yaml"><img src="https://img.shields.io/github/actions/workflow/status/oldratlee/land/ci.yaml?branch=master&logo=github&logoColor=white" alt="Github Workflow Build Status"></a>
<a href="https://openjdk.java.net/"><img src="https://img.shields.io/badge/Java-8+-green?logo=openjdk&logoColor=white" alt="JDK support"></a>
<a href="https://www.apache.org/licenses/LICENSE-2.0.html"><img src="https://img.shields.io/github/license/oldratlee/land?color=4D7A97&logo=apache" alt="License"></a>
<a href="https://github.com/oldratlee/land/stargazers"><img src="https://img.shields.io/github/stars/oldratlee/land" alt="GitHub Stars"></a>
<a href="https://github.com/oldratlee/land/fork"><img src="https://img.shields.io/github/forks/oldratlee/land" alt="GitHub Forks"></a>
<a href="https://github.com/oldratlee/land/issues"><img src="https://img.shields.io/github/issues/oldratlee/land" alt="GitHub issues"></a>
<a href="https://github.com/oldratlee/land"><img src="https://img.shields.io/github/repo-size/oldratlee/land" alt="GitHub repo size"></a>
<a href="https://gitpod.io/#https://github.com/oldratlee/land"><img src="https://img.shields.io/badge/Gitpod-ready--to--code-green?label=gitpod&logo=gitpod&logoColor=white" alt="gitpod: Ready to Code"></a>
</p>


<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->


- [:checkered_flag: 概述](#checkered_flag-%E6%A6%82%E8%BF%B0)
- [:wrench: 功能](#wrench-%E5%8A%9F%E8%83%BD)
    - [1. `ClassLoader`委托关系的完备配置](#1-classloader%E5%A7%94%E6%89%98%E5%85%B3%E7%B3%BB%E7%9A%84%E5%AE%8C%E5%A4%87%E9%85%8D%E7%BD%AE)
        - [父子`ClassLoader`委托](#%E7%88%B6%E5%AD%90classloader%E5%A7%94%E6%89%98)
        - [兄弟`ClassLoader`委托](#%E5%85%84%E5%BC%9Fclassloader%E5%A7%94%E6%89%98)
    - [2. 常用类加载方式](#2-%E5%B8%B8%E7%94%A8%E7%B1%BB%E5%8A%A0%E8%BD%BD%E6%96%B9%E5%BC%8F)
- [:art: 使用场景](#art-%E4%BD%BF%E7%94%A8%E5%9C%BA%E6%99%AF)
- [:beer: 目标](#beer-%E7%9B%AE%E6%A0%87)
- [:loudspeaker: 进阶目标](#loudspeaker-%E8%BF%9B%E9%98%B6%E7%9B%AE%E6%A0%87)
- [:books: 相关资料](#books-%E7%9B%B8%E5%85%B3%E8%B5%84%E6%96%99)
    - [已有的基于ClassLoader实现的隔离容器项目](#%E5%B7%B2%E6%9C%89%E7%9A%84%E5%9F%BA%E4%BA%8Eclassloader%E5%AE%9E%E7%8E%B0%E7%9A%84%E9%9A%94%E7%A6%BB%E5%AE%B9%E5%99%A8%E9%A1%B9%E7%9B%AE)
    - [:microscope: 官方资料](#microscope-%E5%AE%98%E6%96%B9%E8%B5%84%E6%96%99)
        - [ClassLoader](#classloader)
        - [Permission](#permission)
    - [:bookmark: 二手资料](#bookmark-%E4%BA%8C%E6%89%8B%E8%B5%84%E6%96%99)
        - [ClassLoader](#classloader-1)
    - [ClassLoader实际应用](#classloader%E5%AE%9E%E9%99%85%E5%BA%94%E7%94%A8)
    - [安全](#%E5%AE%89%E5%85%A8)
    - [ClassLoader Memory Leak](#classloader-memory-leak)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# :checkered_flag: 概述

:point_right: 一个简单的基于`ClassLoader`用于依赖隔离的容器实现。  
\# 在`Java`中依赖主要是`Jar`。

依赖容器自然涉及下面的问题：

* 可以从多处加载类，并分配不同的`ClassLoader`
* `ClassLoader`之间有继承关系  
`ClassLoader`的继承关系会是一个树
* 类加载会在上下级`ClassLoader`之间有委托关系，如：
    * 是否允许在上级`ClassLoader`中查找类。   
    即是否 **委托**。
    * 允许在上级`ClassLoader`查找哪些类/包。   
    即可以配置 **委托** 的粒度。
    * 只允许在某级`ClassLoader`查找哪些类/包，会忽略这级`ClassLoader`的下级`ClassLoader`中这些类/包，不允许子`ClassLoader`。    
即必须 **委托**。


# :wrench: 功能

## 1. `ClassLoader`委托关系的完备配置

### 父子`ClassLoader`委托

完备委托关系可以先分析只有父子2层`ClassLoader`间委托关系的情况。

某个类的加载在两层父子`ClassLoader`间委托关系，按是否加载排列组合一共有4种情况：

* 父不加载，子不加载【00】    
可以用来显式禁止某些类的加载。    
实际应用中应该 **_很少_** 会用到。
* 父不加载，子加载【01】  
子自理，父里即使包含了相同的类也不会污染子。实际场景：
    * 用来`Tomcat`容器自用`Lib`，不会影响到的`Web`应用。
* 父加载，子不加载【10】  
一定使用的父的类。实际场景：
    * `Tomcat`容器的`Servlet` `API`，不允许被`Web`应用改写。
* 父加载，子加载【11】      
两者可以加载的情况下，按谁优先分成2个Case：
    * 父优先。【`Parent-Child`】    
    即是`Java`缺省的委托策略，代理模式（`Delegation Mode`）。    
    这个委托策略可以保证`Java`核心库的类型优先加载，`Java`核心库的类的加载工作由引导类加载器来统一完成，保证了`Java`应用所使用的都是同一个版本的`Java`核心库的类，是互相兼容的。
    * 子优先。【`Child-Parent`】    
    这种委托关系比较复杂，有引起类版本混乱的风险！:bomb:  
    实际应用中应该 **_避免_** 这种委托关系。 :no_good:

> :information_source:    
> 关于子优先【Child-Parent】类版本混乱的风险的细节原因看了后面参考资料就清楚了，这里只说一个简单例子：    
> 子里有类`Wheel`；父里有类`Car`、`Wheel`；类`Car`引用了类`Wheel`。    
> 子加载类`Car`里通过实际是父来加载（子中没有这个类），返回的`Car`所引用`Wheel`是用`Car`的`ClassLoader`即父来加载。    
> 子中直接使用`Wheel`时，由子来加载（子里有`Wheel`类）。    
> 结果`Car`引用的类`Wheel`和子直接使用的类`Wheel`的`ClassLoader`不同，即类型不兼容，看起来正确的赋值会抛出的`ClassCastException`！

上面【11】的情况分成2个子Case，合起来一共有5种情况。

委托关系可以统一描述成：

1. `None`
2. `Child-Only`
3. `Parent-Only`
4. `Parent-Child`
5. `Child-Parent`

按上面说明的2层委托关系约定，嵌套推广即可得到 包含 **任意层**`ClassLoader`的完备委托关系。:sparkles:

### 兄弟`ClassLoader`委托

父子`ClassLoader`由于树状的单继承关系，委托关系比较单一。
要实现复杂的代理关系，兄弟`ClassLoader`之间代理可以简化。

举个场景，多个中间件如`RPC`、`Message`等，要需要把部分类代理给应用`ClassLoader`，如果就使用父子代理，结果会是这样：

```bash
System ClassLoader
    |
    V
RPC ClassLoader
    |
    V
Message ClassLoader
    |
    V
App ClassLoader
```

上面问题是，`RPC ClassLoader`和`Message ClassLoader`之间的父子关系不符合实际关系（`RPC ClassLoader`和`Message ClassLoader`之间并不需要父子代理）。

更合理些的代理关系是：

```bash
                  System ClassLoader
                /         |         \
              /           |          \
            V             V           V
RPC ClassLoader -> App ClassLoader <- Message ClassLoader
```

即`RPC ClassLoader`和`Message ClassLoader`作为`App ClassLoader`的兄弟`ClassLoader`并代理。

## 2. 常用类加载方式

* 加载本地类目录或`Jar`文件
* 加载本地有类目录或`Jar`文件的目录
* 加载网络上的类    
这个功能应该很少使用 :stuck_out_tongue_winking_eye: ，为了功能完整而说明。
* 加密类工具/加载加密的类    
这个功能应该很少使用 :stuck_out_tongue_closed_eyes: ，为了功能完整而说明。

# :art: 使用场景

1. 在一个`JVM`中部署多个应用，但应用依赖不互相影响。    
这样是提高 **系统利用率** 的一种方式。
2. 把平台级的二方库从应用中隔离出来，由架构部门统一升级。这样做的原因是：
    - 平台级二方库如果有`Bug`影响面广，有统一的升级的需求。
    - 平台级二方库升级使用面广，升级困难。

> :information_source:  
> 上面的部署方式中，依赖容器的引入对于应用的开发应该是 **透明** 的。

# :beer: 目标

* 给出类加载委托情况的完备说明
* 给出类加载委托规则的规范描述
* 给出类加载委托规则的规范描述的自己的一个描述格式  
对于这个项目会优先使用`Properties`来描述，简单够用。
* 说明`Java`的`ClassLoader`的用途和限制
* 给出`ClassLoader`使用和实现的原则
* `ClassLoader`使用和实现容易出错的地方
* 整理出使用了`ClassLoader`的常见框架
* 说明这些框架中`ClassLoader`的实现方法及其使用契约
* 给出`ClassLoader`实现方法及其使用契约的最佳实践

# :loudspeaker: 进阶目标

* `Web`容器集成
* 实现`OSGi`规范    
这个也可以用来验证实现是否面向编程友好

# :books: 相关资料

## 已有的基于ClassLoader实现的隔离容器项目

- [`jboss-modules`](https://github.com/jboss-modules/jboss-modules)  
    JBoss Modules is a standalone implementation of a modular (non-hierarchical) class loading and execution environment for Java.
- [`sofa-jarslink`](https://github.com/sofastack/sofa-jarslink)  
    Jarslink is a sofa ark plugin used to manage multi-application deployment

## :microscope: 官方资料

### ClassLoader

* [The Java Language Specification](http://docs.oracle.com/javase/specs/jls/se7/html/index.html)的[第12章 Execution](http://docs.oracle.com/javase/specs/jls/se7/html/jls-12.html)和[The Java Virtual Machine Specification](http://docs.oracle.com/javase/specs/jvms/se7/html/index.html)的[第5章 Loading, Linking, and Initializing](http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-5.html)    
详细介绍了`Java`类的加载、链接和初始化。    
不同`Java`版本的语言和`JVM`规范在<http://docs.oracle.com/javase/specs/>
* [Multithreaded Custom Class Loaders in Java SE 7](http://docs.oracle.com/javase/7/docs/technotes/guides/lang/cl-mt.html)，`JDK` 7修复了`ClassLoader`的死锁问题。    
这个问题在`JDK 7`之前的版本中一直存在。平时使用中确实不容易碰到，但在线上应用复杂和高压力场景中有实际观察到过。    
这篇文档给出问题的原因及其修复方法。
* `Java`命令行选项`-verbose:class`可以在加载类时显示相关信息。    
完整`Java`命令行选项参见： <http://docs.oracle.com/javase/7/docs/technotes/tools/windows/java.html>
* [Java API doc - ClassLoader](http://docs.oracle.com/javase/7/docs/api/java/lang/ClassLoader.html)
* [Understanding Extension Class Loading](http://docs.oracle.com/javase/tutorial/ext/basics/load.html)
* `Sun` `JDK`用于启动应用的[`Laucher`实现类](http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/7u40-b43/sun/misc/Launcher.java)。    
注： `sun.misc.Launcher`类没有在`JDK`附带的`src.zip`中，因为是`JDK`具体实现部分（厂商相关），可以在`JDK`源码中找到这个`Java`类。    
`Sun`的`JDK`的源码下载在[这里](http://download.java.net/openjdk/jdk7/)，厂商相关`Java`类在目录`jdk/src/share/classes`下。
* `Laucher`类中包含了`Java`除`BootstrapClassloader`（是用本地代码`C/C++`来实现）外另2个`Buildin` `ClassLoader`类的实现：
    * [`ExtClassLoader`](http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/7u40-b43/sun/misc/Launcher.java#Launcher.ExtClassLoader)
    * [`AppClassLoader`](http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/7u40-b43/sun/misc/Launcher.java#Launcher.AppClassLoader)

### Permission

* [Permissions in JDK 7](http://docs.oracle.com/javase/7/docs/technotes/guides/security/permissions.html)
* [Permissions in JDK 6](http://docs.oracle.com/javase/6/docs/technotes/guides/security/permissions.html)

## :bookmark: 二手资料

### ClassLoader

* [IBM DeveloperWorks - 深入探讨Java类加载器](https://www.ibm.com/developerworks/cn/java/j-lo-classloader/)。一篇非常不错的`ClassLoader`的介绍文章，并且对比介绍了
    * `Java` `SPI`的类加载策略。包含`JDBC`和`JAXP`为代表的2种方式。
    * `Tomcat`的类加载策略。
    * `OSGi`的类加载策略。
* [The basics of Java class loaders](http://www.javaworld.com/article/2077260/learn-java/the-basics-of-java-class-loaders.html)   
`1996`年(`Java 1`的年代)的一篇老文章，其中描述功能现在可以通过`Java 2`提供的[`java.net.URLClassLoader`](http://docs.oracle.com/javase/7/docs/api/java/net/URLClassLoader.html)方便的完成。    
但实现复杂自定义`ClassLoader`的流程是一样的，文章给出了实现自定义`ClassLoader`
    * 要覆盖的`ClassLoader`关键方法
    * 要遵循的基本约定
    * 要注意的安全问题
* [IBM DeveloperWorks - Understanding the Java ClassLoader](http://www6.software.ibm.com/developerworks/education/j-classloader/j-classloader-a4.pdf)
* [IBM DeveloperWorks - Java programming dynamics, Part 1: Java classes and class loading](https://www.ibm.com/developerworks/library/j-dyn0429/)    
中文版在：[Java编程的动态性，第 1 部分: 类和类装入](http://www.ibm.com/developerworks/cn/java/j-dyn0429/)    
[Java programming dynamics series](http://www.ibm.com/developerworks/views/java/libraryview.jsp?search_by=Java+dynamics)，这个系列的中文版[Java编程的动态性](https://www.ibm.com/developerworks/cn/views/java/libraryview.jsp?type_by=%E6%8A%80%E6%9C%AF%E6%96%87%E7%AB%A0&view_by=search&search_by=Java+%E7%BC%96%E7%A8%8B%E7%9A%84%E5%8A%A8%E6%80%81%E6%80%A7)
* [onjava.com - Internals of Java Class Loading](http://www.onjava.com/pub/a/onjava/2005/01/26/classloading.html)
* [IBM DeveloperWorks - Demystifying class loading problems series](http://www.ibm.com/developerworks/views/java/libraryview.jsp?search_by=demystifying+class+loading+problems)
* [IBM Java Diagnostics Guide - Class loading](http://publib.boulder.ibm.com/infocenter/javasdk/v5r0/index.jsp?topic=%2Fcom.ibm.java.doc.diagnostics.50%2Fdiag%2Funderstanding%2Fclass_loader.html)
* [Getting Started with Javassist - Class Loader](http://www.csg.ci.i.u-tokyo.ac.jp/~chiba/javassist/tutorial/tutorial.html#load)
* [Sheng Liang and Gilad Bracha, "Dynamic Class Loading in the Java Virtual Machine"](http://kenwublog.com/docs/Dynamic+Class+Loading+in+the+Java+Virtual+Machine.pdf)    
ACM OOPSLA'98, pp.36-44, 1998.
* [Wikipedia - Java Classloader](http://en.wikipedia.org/wiki/Java_Classloader)
* [Class.forName() vs ClassLoader.loadClass() - which to use for dynamic loading?](http://stackoverflow.com/questions/8100376/class-forname-vs-classloader-loadclass-which-to-use-for-dynamic-loading)

## ClassLoader实际应用

* [Understanding WebLogic Server Application Classloading](http://docs.oracle.com/cd/E24329_01/web.1211/e24368/classloading.htm)
* [The Apache Tomcat 5.5 Servlet/JSP Container - Class Loader HOW-TO](http://tomcat.apache.org/tomcat-5.5-doc/class-loader-howto.html)：详细介绍了`Tomcat` 5.5中的类加载器机制。
* [OSGi Service Platform Core Specification](http://www.osgi.org/Specifications/HomePage)

## 安全

* [IBM DeveloperWorks - Java安全模型介绍](http://www.ibm.com/developerworks/cn/java/j-lo-javasecurity/)
* [IBM DeveloperWorks - Java 授权内幕](http://www.ibm.com/developerworks/cn/java/j-javaauth/)  
更多内容参见：[IBM DeveloperWorks - Java安全专题](https://www.ibm.com/developerworks/cn/java/j-security/)
* [Java Security - Chapter 3. Java Class Loaders](http://docstore.mik.ua/orelly/java-ent/security/ch03_01.htm)

## ClassLoader Memory Leak

* [Classloader leaks: the dreaded "java.lang.OutOfMemoryError: PermGen space" exception](http://frankkieviet.blogspot.com/2006/10/classloader-leaks-dreaded-permgen-space.html)
* [How to fix the dreaded "java.lang.OutOfMemoryError: PermGen space" exception (classloader leaks)](http://frankkieviet.blogspot.com/2006/10/how-to-fix-dreaded-permgen-space.html)
* [IBM DeveloperWorks - Diagnosis of Java class loader memory leaks](http://www.ibm.com/developerworks/webservices/library/ws-javaclass/index.html)
* [Reloading Java Classes 201: How do ClassLoader leaks happen?](http://zeroturnaround.com/rebellabs/rjc201/)
* [Classloader-Related Memory Issues](http://javabook.compuware.com/content/memory/problem-patterns/class-loader-issues.aspx)
