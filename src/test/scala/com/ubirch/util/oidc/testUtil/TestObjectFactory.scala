package com.ubirch.util.oidc.testUtil

import com.ubirch.user.client.model.{Action, User}
import org.joda.time.{DateTime, DateTimeZone}

import java.util.UUID

object TestObjectFactory {

  val externalId = "testExternalId"

  def createUser(id: Option[UUID] = Some(UUID.randomUUID()), active: Boolean = true,
                 action : Option[Action]= None, executionDate: Option[DateTime] = None): User ={
    val yesterday = DateTime.now(DateTimeZone.UTC).minusDays(1)
    User(id, "displayName", "unit-test", externalId,None, active, "trackle", action,  executionDate, yesterday, yesterday)
  }
}
