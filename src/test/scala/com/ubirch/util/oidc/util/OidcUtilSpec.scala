package com.ubirch.util.oidc.util

import com.ubirch.util.crypto.hash.HashUtil
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers

/**
  * author: cvandrei
  * since: 2017-02-10
  */
class OidcUtilSpec extends AnyFeatureSpec
  with Matchers {

  Feature("stateToHashedKey()") {

    Scenario("all strings empty") {
      runStateTest("", "")
    }

    Scenario("provider string empty") {
      runStateTest("", "123123")
    }

    Scenario("state string empty") {
      runStateTest("google", "")
    }

    Scenario("none of the strings empty") {
      runStateTest("google", "123123")
    }

  }

  Feature("tokenToHashedKey()") {

    Scenario("token string empty") {
      runTokenTest("")
    }

    Scenario("token not empty") {
      runTokenTest("12341234")
    }

  }

  private def runStateTest(provider: String, state: String) = {

    // test
    val result = OidcUtil.stateToHashedKey(provider, state)

    // verify
    val expected = HashUtil.sha256HexString(s"state:$provider:$state")
    result shouldBe expected

  }

  private def runTokenTest(token: String) = {

    // test
    val result = OidcUtil.tokenToHashedKey(token)

    // verify
    result shouldBe HashUtil.sha256HexString(token)

  }

}
