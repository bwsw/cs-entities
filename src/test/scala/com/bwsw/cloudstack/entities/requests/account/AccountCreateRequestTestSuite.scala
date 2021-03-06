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
package com.bwsw.cloudstack.entities.requests.account

import java.util.{TimeZone, UUID}

import br.com.autonomiccs.apacheCloudStack.client.ApacheCloudStackApiCommandParameter
import com.bwsw.cloudstack.entities.requests.Constants.{Commands, ParameterValues}
import com.bwsw.cloudstack.entities.requests.Constants.ParameterKeys._
import com.bwsw.cloudstack.entities.requests.account.AccountCreateRequest.RootAdmin
import org.scalatest.FlatSpec

import scala.collection.JavaConverters._

class AccountCreateRequestTestSuite extends FlatSpec {
  val settings = AccountCreateRequest.Settings(
    _type = RootAdmin,
    email = "test@example.com",
    firstName = "fn",
    lastName = "ln",
    password = "password",
    username = "test"
  )

  val defaultParameters = Set[ApacheCloudStackApiCommandParameter](
    new ApacheCloudStackApiCommandParameter(RESPONSE, ParameterValues.JSON),
    new ApacheCloudStackApiCommandParameter(EMAIL, settings.email),
    new ApacheCloudStackApiCommandParameter(FIRST_NAME, settings.firstName),
    new ApacheCloudStackApiCommandParameter(LAST_NAME, settings.lastName),
    new ApacheCloudStackApiCommandParameter(PASSWORD, settings.password),
    new ApacheCloudStackApiCommandParameter(USER_NAME, settings.username),
    new ApacheCloudStackApiCommandParameter(ACCOUNT_TYPE, settings._type.numericValue)
  )

  it should "create a request with predefined and specified (via constructor) parameters" in {
    val request = new AccountCreateRequest(settings)

    assert(request.getRequest.getParameters.asScala.toSet == defaultParameters)
    assert(request.getRequest.getCommand == Commands.CREATE_ACCOUNT)
  }

  "withId" should "add id parameter to a request" in {
    val accountId = UUID.randomUUID()
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter(ACCOUNT_ID, accountId.toString))
    val request = new AccountCreateRequest(settings)
    request.withId(accountId)

    assert(request.getRequest.getParameters.asScala.toSet == expectedParameters)
  }

  "withTimeZone" should "add time zone parameter to a request" in {
    val timezone = "GMT+0700"
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter(TIMEZONE, timezone))
    val request = new AccountCreateRequest(settings)
    request.withTimeZone(TimeZone.getTimeZone(timezone))

    assert(request.getRequest.getParameters.asScala.toSet == expectedParameters)
  }

  "withDomain" should "add domain id parameter to a request" in {
    val domainId = UUID.randomUUID()
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter(DOMAIN_ID, domainId))
    val request = new AccountCreateRequest(settings)
    request.withDomain(domainId)

    assert(request.getRequest.getParameters.asScala.toSet == expectedParameters)
  }

  "withName" should "add account name parameter to a request" in {
    val accountName = "name"
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter(ACCOUNT, accountName))
    val request = new AccountCreateRequest(settings)
    request.withName(accountName)

    assert(request.getRequest.getParameters.asScala.toSet == expectedParameters)
  }

  "withNetworkDomain" should "add network domain parameter to a request" in {
    val networkdomain = "root"
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter(NETWORK_DOMAIN, networkdomain))
    val request = new AccountCreateRequest(settings)
    request.withNetworkDomain(networkdomain)

    assert(request.getRequest.getParameters.asScala.toSet == expectedParameters)
  }

  "withRole" should "add role id parameter to a request" in {
    val role = 1
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter(ROLE_ID, role))
    val request = new AccountCreateRequest(settings)
    request.withRole(role)

    assert(request.getRequest.getParameters.asScala.toSet == expectedParameters)
  }

  "withUserId" should "add user id parameter to a request" in {
    val userId = UUID.randomUUID()
    val expectedParameters = defaultParameters ++ Set(new ApacheCloudStackApiCommandParameter(USER_ID, userId))
    val request = new AccountCreateRequest(settings)
    request.withUserId(userId)

    assert(request.getRequest.getParameters.asScala.toSet == expectedParameters)
  }

  it should "create child AccountCreateRequest with one of parent parameters and one new parameter" in {
    val userId = UUID.randomUUID()
    val testParameterValue = "testValue"
    val testParameterName = "testName"

    val expectedParameters = defaultParameters ++ Set(
      new ApacheCloudStackApiCommandParameter(USER_ID, userId),
      new ApacheCloudStackApiCommandParameter(testParameterName, testParameterValue)
    )

    class TestAccountCreateRequest extends AccountCreateRequest(settings) {
      def withTestParameter(value: String): Unit = {
        addParameter(testParameterName, value)
      }
    }

    val request = new TestAccountCreateRequest
    request.withUserId(userId)
    request.withTestParameter(testParameterValue)

    assert(request.getRequest.getParameters.asScala.toSet == expectedParameters)
  }
}
