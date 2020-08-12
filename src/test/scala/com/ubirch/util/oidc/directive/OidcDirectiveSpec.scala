package com.ubirch.util.oidc.directive

import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import akka.http.scaladsl.model.{HttpHeader, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{AuthorizationFailedRejection, Route}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.{Http, HttpExt}
import com.ubirch.util.oidc.config.{OidcUtilsConfig, OidcUtilsConfigKeys}
import com.ubirch.util.oidc.model.UserContext
import com.ubirch.util.oidc.util.OidcUtil
import com.ubirch.util.redis.RedisClientUtil
import com.ubirch.util.redis.test.RedisCleanup
import java.util.UUID
import org.json4s.native.Serialization.write
import org.scalatest.{BeforeAndAfterEach, FeatureSpec, Matchers}
import redis.RedisClient

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * author: cvandrei
  * since: 2017-03-21
  */
class OidcDirectiveSpec extends FeatureSpec
  with ScalatestRouteTest
  with Matchers
  with BeforeAndAfterEach
  with RedisCleanup {

  implicit protected val redis: RedisClient = RedisClientUtil.getRedisClient()


  implicit val httpClient: HttpExt = Http()

  private val configPrefix = OidcUtilsConfigKeys.PREFIX

  override protected def beforeEach(): Unit = {
    deleteAll(configPrefix = configPrefix)
    Thread.sleep(100)
  }

  private val oidcDirective = new OidcDirective()

  import oidcDirective._

  val testRoute: Route =
    get {
      pathSingleSlash {
        oidcToken2UserContext { userContext =>
          complete(s"context=${userContext.context}; userId=${userContext.externalUserId}")
        }
      }
    }

  feature("oidcToken2UserContext") {

    scenario("with all headers but token does not exist") {

      // prepare
      val authorizationHeader: HttpHeader = Authorization(OAuth2BearerToken("some-token"))

      // test
      Get().withHeaders(authorizationHeader) ~> testRoute ~> check {

        // verify
        handled shouldBe false
        rejection shouldEqual AuthorizationFailedRejection

      }

    }

    scenario("with Authorization header and token exists") {

      // prepare
      val context = "some-context"
      val token = "some-token"
      val providerId = "some-provider-id"
      val userId = UUID.randomUUID()
      val userName = "Jane Doe"
      val locale = "en"
      val externalId = "some-external-id"

      val initialTtl = 10L
      val refreshTtl = OidcUtilsConfig.redisUpdateExpirySeconds()

      val redisKey = OidcUtil.tokenToHashedKey(token)
      val redisValue = write(UserContext(
        context = context,
        providerId = providerId,
        externalUserId = externalId,
        userName = userName,
        locale = locale,
        userId = userId,
        hasPubKey = 1
      ))
      Await.result(redis.set(redisKey, redisValue, exSeconds = Some(initialTtl)), 2 seconds) shouldBe true

      val authorizationHeader: HttpHeader = Authorization(OAuth2BearerToken(token))

      // test
      Get().withHeaders(authorizationHeader) ~> Route.seal(testRoute) ~> check {

        // verify
        status === StatusCodes.OK
        responseAs[String] shouldEqual s"context=$context; userId=$externalId"

        Thread.sleep(2000)
        Await.result(redis.ttl(redisKey), 2 seconds) should be < refreshTtl

      }

    }

    scenario("test case: without Authorization header") {

      // test
      Get() ~> Route.seal(testRoute) ~> check {

        // verify
        status === StatusCodes.BadRequest
        responseAs[String] shouldEqual "{\"version\":\"1.0\",\"status\":\"NOK\",\"errorType\":\"01\",\"errorMessage\":\"Authorization header is missing.\"}"

      }

    }

  }

}
