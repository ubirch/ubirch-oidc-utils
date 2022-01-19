package com.ubirch.util.oidc.util

import com.ubirch.util.crypto.ecc.EccUtil
import com.ubirch.util.crypto.hash.HashUtil
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers


/**
  * author: cvandrei
  * since: 2018-03-15
  */
class UbirchTokenUtilSpec extends AnyFeatureSpec with Matchers {

  Feature("providerId") {

    Scenario("ensure value did not change") {
      UbirchTokenUtil.providerId shouldBe "ubirchToken"
    }

  }

  Feature("delim") {

    Scenario("ensure value did not change") {
      UbirchTokenUtil.delim shouldBe "::"
    }

  }

  Feature("defaultSignature") {

    Scenario("ensure value did not change") {
      UbirchTokenUtil.defaultSignature shouldBe "to-be-specified"
    }

  }

  Feature("toUbirchToken()") {

    Scenario("without private key") {

      // prepare
      val context = "ubirch-local"
      val email = "test@ubirch.com"

      // test
      val result = UbirchTokenUtil.toUbirchToken(
        context = context,
        email = email
      )

      // verify
      val hashedEmail = UbirchTokenUtil.hashEmail(email)
      val expected = s"$context${UbirchTokenUtil.delim}$hashedEmail${UbirchTokenUtil.delim}${UbirchTokenUtil.defaultSignature}"
      result shouldBe expected

    }

    Scenario("with private key") {

      // prepare
      val context = "ubirch-local"
      val (_, privateKey) = new EccUtil().generateEccKeyPairEncoded
      val email = "test@ubirch.com"

      // test
      val result = UbirchTokenUtil.toUbirchToken(
        context = context,
        email = email,
        privateKey = Some(privateKey)
      )

      // verify
      val hashedEmail = UbirchTokenUtil.hashEmail(email)
      val expected = s"$context${UbirchTokenUtil.delim}$hashedEmail${UbirchTokenUtil.delim}${UbirchTokenUtil.defaultSignature}"
      result shouldBe expected

    }

  }

  Feature("hashEmail()") {

    Scenario("hash --> sha256HexString() is applied") {

      // prepare
      val email = "test@ubirch.com"

      // test
      val result = UbirchTokenUtil.hashEmail(email)

      // verify
      val expected = HashUtil.sha256HexString(email)
      result shouldBe expected

    }

  }

}
