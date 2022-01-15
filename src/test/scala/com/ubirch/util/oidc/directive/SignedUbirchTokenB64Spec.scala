package com.ubirch.util.oidc.directive

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.typesafe.scalalogging.StrictLogging
import com.ubirch.util.crypto.ecc.EccUtil
import com.ubirch.util.redis.test.RedisCleanup
import org.joda.time.{DateTime, DateTimeZone}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers

class SignedUbirchTokenB64Spec extends AnyFeatureSpec
  with ScalatestRouteTest
  with Matchers
  with BeforeAndAfterEach
  with RedisCleanup
  with StrictLogging {

  val eccUtil = new EccUtil
  Feature("basic test") {

    ignore("simple check token 0") {
      val (pubKeyB64, privKeyB64) = eccUtil.generateEccKeyPairEncoded

      val euid = "f570898ab0ebcd7011a48c1ea4921e5e7827662b440ccd8cd0972ebd586b544ffef2f7f4fdd4cde49874d0b7f6b7348771a9eae2fd54af47c13dc7ee258234dd"
      val eid = "trackle-demo"
      val ts = "1532541202109"
      val payload = s"$euid##$ts"
      val signature = eccUtil.signPayload(privKeyB64, payload)

      val aToken = s"$eid::$payload::$signature"

      logger.debug(s"AuthToken: $aToken")

      val tsInt = ts.toLong

      val tokenparts = aToken.split("::")
      tokenparts.size shouldBe 3

      val envId = tokenparts(0)
      val extUserIdPart = tokenparts(1)
      val signatureB64 = tokenparts(2)

      extUserIdPart.contains("##") shouldBe true
      signatureB64.contains("##") shouldBe false

      val extUserIdParts = extUserIdPart.split("##")

      val extUserId = extUserIdParts(0)
      val timestamp = extUserIdParts(1)
      val timestampLong = timestamp.toLong

      tsInt shouldBe timestampLong

      val tsDT = new DateTime(timestampLong * 1000L, DateTimeZone.UTC)

      new EccUtil().validateSignature(pubKeyB64, signatureB64, payload) shouldBe true

    }

  }

}
