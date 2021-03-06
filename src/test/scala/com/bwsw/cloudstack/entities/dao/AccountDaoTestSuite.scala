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
package com.bwsw.cloudstack.entities.dao

import java.util.UUID

import br.com.autonomiccs.apacheCloudStack.client.ApacheCloudStackRequest
import br.com.autonomiccs.apacheCloudStack.exceptions.ApacheCloudStackClientRequestRuntimeException
import com.bwsw.cloudstack.entities.Executor
import com.bwsw.cloudstack.entities.requests.account.AccountCreateRequest.RootAdmin
import com.bwsw.cloudstack.entities.requests.account.{AccountCreateRequest, AccountFindRequest}
import com.bwsw.cloudstack.entities.responses.account.Account
import org.scalatest.FlatSpec

class AccountDaoTestSuite extends FlatSpec with TestData {
  val findRequest = new AccountFindRequest

  "find" should "return non-empty entity list if a response json string contains the relevant data" in {
    val expectedAccountList = List(testAccount)

    val executor = new Executor(executorSettings, clientCreator){
      override def executeRequest(request: ApacheCloudStackRequest): String = {
        assert(findRequest.getRequest == request)
        Response.getAccountResponseJson(testAccount)
      }
    }

    val accountDao = new AccountDao(executor, jsonMapper)

    assert(accountDao.find(findRequest) == expectedAccountList)
  }

  "find" should "return an empty entity list if a response json string does not contain the relevant data" in {
    val executor = new Executor(executorSettings, clientCreator){
      override def executeRequest(request: ApacheCloudStackRequest): String = {
        assert(findRequest.getRequest == request)
        Response.getResponseWithEmptyAccountList
      }
    }

    val accountDao = new AccountDao(executor, jsonMapper)

    assert(accountDao.find(findRequest) == List.empty[Account])
  }

  "find" should "return an empty entity list if Executor throws ApacheCloudStackClientRequestRuntimeException" +
    " with a status 431" in {
    val statusCode = 431
    val executor = new Executor(executorSettings, clientCreator){
      override def executeRequest(request: ApacheCloudStackRequest): String = {
        assert(findRequest.getRequest == request)
        throw new ApacheCloudStackClientRequestRuntimeException(statusCode, "", "")
      }
    }

    val accountDao = new AccountDao(executor, jsonMapper)

    assert(accountDao.find(findRequest) == List.empty[Account])
  }

  "find" should "not swallow non-ApacheCloudStackClientRequestRuntimeException" in {
    val executor = new Executor(executorSettings, clientCreator){
      override def executeRequest(request: ApacheCloudStackRequest): String = {
        assert(findRequest.getRequest == request)
        throw new Exception
      }
    }

    val accountDao = new AccountDao(executor, jsonMapper)

    assertThrows[Exception](accountDao.find(findRequest))
  }

  "find" should "not swallow ApacheCloudStackClientRequestRuntimeException with a status different from 431" in {
    val statusCode = 400
    val executor = new Executor(executorSettings, clientCreator) {
      override def executeRequest(request: ApacheCloudStackRequest): String = {
        assert(findRequest.getRequest == request)
        throw new ApacheCloudStackClientRequestRuntimeException(statusCode, "", "")
      }
    }

    val accountDao = new AccountDao(executor, jsonMapper)

    assertThrows[ApacheCloudStackClientRequestRuntimeException](accountDao.find(findRequest))
  }

  "find" should "handle child of AccountFindRequest" in {
    val expectedAccountList = List(testAccount)

    class TestAccountFindRequest extends AccountFindRequest

    val childRequest = new TestAccountFindRequest

    val executor = new Executor(executorSettings, clientCreator){
      override def executeRequest(request: ApacheCloudStackRequest): String = {
        assert(childRequest.getRequest == request)
        Response.getAccountResponseJson(testAccount)
      }
    }

    val accountDao = new AccountDao(executor, jsonMapper)

    assert(accountDao.find(childRequest) == expectedAccountList)
  }

  "create" should "submit request to Executor" in {
    var actualRequests = List.empty[ApacheCloudStackRequest]
    val createRequest = new AccountCreateRequest(AccountCreateRequest.Settings(
      _type = RootAdmin,
      email = "e@e",
      firstName = "fn",
      lastName = "ln",
      password = "passw",
      username = "user"
    ))

    val expectedRequests = List(createRequest.getRequest)

    val executor = new Executor(executorSettings, clientCreator) {
      override def executeRequest(request: ApacheCloudStackRequest): String = {
        actualRequests = actualRequests ::: request :: Nil
        ""
      }
    }

    val accountDao = new AccountDao(executor, jsonMapper)

    assert(accountDao.create(createRequest).isInstanceOf[Unit])
    assert(actualRequests == expectedRequests)
  }

  "create" should "not swallow an exception" in {
    var actualRequests = List.empty[ApacheCloudStackRequest]
    val createRequest = new AccountCreateRequest(AccountCreateRequest.Settings(
      _type = RootAdmin,
      email = "e@e",
      firstName = "fn",
      lastName = "ln",
      password = "passw",
      username = "user"
    ))

    val expectedRequests = List(createRequest.getRequest)

    val executor = new Executor(executorSettings, clientCreator) {
      override def executeRequest(request: ApacheCloudStackRequest): String = {
        actualRequests = actualRequests ::: request :: Nil
        throw new Exception
      }
    }

    val accountDao = new AccountDao(executor, jsonMapper)

    assertThrows[Exception](accountDao.create(createRequest).isInstanceOf[Unit])
    assert(actualRequests == expectedRequests)
  }

  "create" should "handle child of AccountCreateRequest" in {
    var actualRequests = List.empty[ApacheCloudStackRequest]
    val createRequestSettings = AccountCreateRequest.Settings(
      _type = RootAdmin,
      email = "e@e",
      firstName = "fn",
      lastName = "ln",
      password = "passw",
      username = "user"
    )

    class TestAccountCreateRequest extends AccountCreateRequest(createRequestSettings)

    val createRequest = new TestAccountCreateRequest

    val expectedRequests = List(createRequest.getRequest)

    val executor = new Executor(executorSettings, clientCreator) {
      override def executeRequest(request: ApacheCloudStackRequest): String = {
        actualRequests = actualRequests ::: request :: Nil
        ""
      }
    }

    val accountDao = new AccountDao(executor, jsonMapper)

    assert(accountDao.create(createRequest).isInstanceOf[Unit])
    assert(actualRequests == expectedRequests)
  }
}
