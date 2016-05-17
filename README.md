# Table of contents

+ [Installation](#installation)
+ [How to use](#how-to-use)
+ [Configuration](#configuration)

## Installation

You can easily use the sbt tool to download the resources to your project

```scala
libraryDependencies  ++=  Seq(
    "com.github.kitefusion" %% "play-requestfilter" % "0.1.1"
)
```

## How to use

First you have to setup an implementation of HttpFilters in root package.

see: https://www.playframework.com/documentation/2.5.x/ScalaHttpFilters

If you do not have already filters defined you can implement one by creating a file "Filters.scala" in the app directory
 with the following content:

```scala
import javax.inject.Inject
import play.api.http.HttpFilters
import com.github.kitefusion.play.requestfilter.SessionTimeoutFilter

@Singleton
class Filters @Inject() (
  sessionTimeoutFilter: SessionTimeoutFilter
) extends HttpFilters {

  override val filters = Seq(
    sessionTimeoutFilter
  )
}
```


## Configuration

All configurations are optional because there are already default configurations defined. If you want to override
 them you can just override the following configurations.

```hocon
kitefusion.play.filter {
  sessionTimeOut {
    lifeTime=30 // number in minutes the session is valid
    usernameKey="username" // name of key in session which determines if the user is logged in
    sessionKeyName="slt" // name of the timestamp session key
    redirectUri="/" // redirect Uri the user will be redirected. If not provided the user will be redirected to the same page he requested
  }
}
```