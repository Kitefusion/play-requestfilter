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
	val sessionLifeTime: Int = 60 * configuration.getInt("kitefusion.play.filter.sessionTimeOut.lifeTime").get

	/**
	 * key of logged in user to determine whether the session have to expire on timeout
	 */
	val sessionUsernameKey: String = configuration.getString("kitefusion.play.filter.sessionTimeOut.usernameKey").get

	/**
	 * uri where the user should be redirected when session is timed out
	 * if empty the user will be redirected to the same uri he requested
	 */
	val redirectUri: Option[String] = configuration.getString("kitefusion.play.filter.sessionTimeOut.redirectUri")

	/**
	 * name of the timestamp key in the session
	 */
	val lifeTimeKey: String = configuration.getString("kitefusion.play.filter.sessionTimeOut.sessionKeyName").get

	/**
	 * apply method which checks for the session lifetime
	 *
	 * @param nextFilter next filter which is in the chain
	 * @param requestHeader request header which contains the session
	 * @return
	 */
	override def apply(nextFilter: RequestHeader => Future[Result]) (requestHeader: RequestHeader): Future[Result] = {

		val oldSessionTime = (System.currentTimeMillis / 1000) - sessionLifeTime

		requestHeader.session.get(lifeTimeKey) match {

			case Some(value) if value.toInt < oldSessionTime && requestHeader.session.get(sessionUsernameKey).isDefined =>

				Future successful Redirect(redirectUri.getOrElse(requestHeader.uri)).withNewSession
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
	protected def continue(nextFilter: RequestHeader => Future[Result]) (requestHeader: RequestHeader) = {

		nextFilter(requestHeader).map { result =>

			result.addingToSession(lifeTimeKey -> (System.currentTimeMillis / 1000).toString)(requestHeader)
		}
	}
}