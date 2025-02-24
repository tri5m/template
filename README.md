### springboot 2.x的大型项目脚手架
* JDK17
* springBoot 2.7.18
* xbatis
* Tucache
* SpringDoc(OpenAPI3)
* redisson
* knife4j

### 使用方式
* 在services下新建自己的项目模块
* 可以依赖services-common使用封装好的一系列工具
* 如果是一个单独的模块项目可以选择不使用services-common，可以直接从services模块获取需要的依赖
* 参考样例项目template-service