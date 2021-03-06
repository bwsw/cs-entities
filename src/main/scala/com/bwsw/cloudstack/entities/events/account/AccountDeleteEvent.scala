/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package com.bwsw.cloudstack.entities.events.account

import java.time.OffsetDateTime
import java.util.UUID

import com.bwsw.cloudstack.entities.common.CommonJsonFormats._
import com.bwsw.cloudstack.entities.events.{CloudStackEvent, EventDateTime}
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

final case class AccountDeleteEvent(status: Option[String],
                                    entityuuid: Option[UUID],
                                    eventDateTime: Option[OffsetDateTime],
                                    description: Option[String])
  extends CloudStackEvent with EventDateTime


object AccountDeleteEvent {

  implicit val accountDeleteEventJsonFormat: RootJsonFormat[AccountDeleteEvent] =
    jsonFormat4(AccountDeleteEvent.apply)
}
