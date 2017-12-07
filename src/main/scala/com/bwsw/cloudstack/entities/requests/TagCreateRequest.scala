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
package com.bwsw.cloudstack.entities.requests

import java.util.UUID

import br.com.autonomiccs.apacheCloudStack.client.ApacheCloudStackRequest
import com.bwsw.cloudstack.entities.requests.util.Constants.Commands
import com.bwsw.cloudstack.entities.requests.util.Constants.Parameters._
import com.bwsw.cloudstack.entities.requests.traits.Request
import com.bwsw.cloudstack.entities.requests.util.traits.TagType
import com.bwsw.cloudstack.entities.responses.Tag

class TagCreateRequest(settings: TagCreateRequest.Settings) extends Request {
  override val request = new ApacheCloudStackRequest(Commands.CREATE_TAGS)
    .addParameter(RESPONSE,"json")
    .addParameter(RESOURCE_TYPE, settings.resourceType.toString)
    .addParameter(RESOURCE_IDS, settings.resourceIds.mkString(","))

  private var i = 0
  settings.tags.foreach { tag =>
    request.addParameter(s"tags[$i].key", tag.key)
    request.addParameter(s"tags[$i].value", tag.value)
    i = i + 1
  }
}

object TagCreateRequest {
  case class Settings(resourceType: TagType, resourceIds: Set[UUID], tags: List[Tag])
}
