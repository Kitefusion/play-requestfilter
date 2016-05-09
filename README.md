# How to use this filter

First you have to setup an implementation of HttpFilters in root package.

see: https://www.playframework.com/documentation/2.5.x/ScalaHttpFilters

After that you can add the filters like described in the play documentation. Afterwards this will look
 like the following:

```scala
@Singleton
class Filters @Inject() (
  env: Environment,
  sessionTimeoutFilter: com.github.kitefusion.play.requestfilter.SessionTimeoutFilter,
) extends HttpFilters {

  override val filters = Seq(
    sessionTimeoutFilter
  )
}
```