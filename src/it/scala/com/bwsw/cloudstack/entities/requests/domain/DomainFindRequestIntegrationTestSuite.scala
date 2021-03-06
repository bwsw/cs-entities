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
package com.bwsw.cloudstack.entities.requests.domain

import java.util.UUID

import com.bwsw.cloudstack.entities.TestEntities
import com.bwsw.cloudstack.entities.requests.Constants
import com.bwsw.cloudstack.entities.responses.domain.{DomainFindResponse, DomainList}
import com.bwsw.cloudstack.entities.util.requests.RequestExecutionHandler
import org.scalatest.FlatSpec

class DomainFindRequestIntegrationTestSuite extends FlatSpec with TestEntities {

  it should "retrieve json string if request contains only default parameters" in {
    val domainFindRequest = new DomainFindRequest
    val response = mapper.deserialize[DomainFindResponse](executor.executeRequest(domainFindRequest.getRequest))

    assert(response.entityList.isInstanceOf[DomainList])
  }

  it should "throw ApacheCloudStackClientRequestRuntimeException with status code 431 " +
    "if entity with a specified value of id parameter does not exist" in {
    val domainId = UUID.randomUUID()
    val domainFindRequest = new DomainFindRequest {
      def withId(id: UUID): Unit = {
        addParameter(Constants.ParameterKeys.ID, id)
      }
    }
    domainFindRequest.withId(domainId)

    assert(RequestExecutionHandler.entityNotExist(domainFindRequest))
  }

  it should "return an empty list of domains if entity with a specified value of name parameter does not exist" in {
    val domainName = UUID.randomUUID().toString
    val domainFindRequest = new DomainFindRequest {
      def withName(name: String): Unit = {
        addParameter(Constants.ParameterKeys.NAME, name)
      }
    }
    domainFindRequest.withName(domainName)
    val response = mapper.deserialize[DomainFindResponse](executor.executeRequest(domainFindRequest.getRequest))

    assert(response.entityList.entities.isEmpty)
  }

  it should "ignore a parameter with incorrect key" in {
    val incorrectParameterKey = UUID.randomUUID().toString
    val request = new DomainFindRequest().getRequest.addParameter(incorrectParameterKey, "value")
    val response = mapper.deserialize[DomainFindResponse](executor.executeRequest(request))

    assert(response.entityList.isInstanceOf[DomainList])
  }
}
