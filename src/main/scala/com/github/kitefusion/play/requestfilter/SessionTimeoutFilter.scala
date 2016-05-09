package com.github.kitefusion.play.requestfilter

import javax.inject._

import akka.stream.Materializer
import play.api.Configuration
import play.api.mvc.Results.Redirect
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/**
 * this filter is used to validate a session for its life time
 *
 * if the lifetime of the session expired
 */
@Singleton
class SessionTimeoutFilter @Inject()(implicit override val mat: Materializer, exec: ExecutionContext, configuration: Configuration) extends Filter {

	/**
	 * session time out in seconds how long the session is active
	 */
	val sessionLifeTime: Int = 60 * configuration.getInt("play.security.filter.lifeTime").get

	/**
	 * apply method which checks for the session lifetime
	 *
	 * @param nextFilter next filter which is in the chain
	 * @param requestHeader request header which contains the session
	 * @return
	 */
	override def apply(nextFilter: RequestHeader => Future[Result]) (requestHeader: RequestHeader): Future[Result] = {

		val oldSessionTime = (System.currentTimeMillis / 1000) - sessionLifeTime

		requestHeader.session.get("timestamp") match {

			case Some(value) if value.toInt < oldSessionTime && requestHeader.session.get("username").isDefined =>

				Future successful Redirect(requestHeader.uri).withNewSession
			case _ => continue(nextFilter)(requestHeader)
		}
	}

	/**
	 * continue with the next filter and add timestamp to session
	 *
	 * @param nextFilter next filter which is in the chain
	 * @param requestHeader request header which contains the session
	 * @return
	 */
	def continue(nextFilter: RequestHeader => Future[Result]) (requestHeader: RequestHeader) = {

		nextFilter(requestHeader).map { result =>

			val newTimestamp = (System.currentTimeMillis / 1000) + sessionLifeTime
			result.addingToSession("timestamp" -> newTimestamp.toString)(requestHeader)
		}
	}
}