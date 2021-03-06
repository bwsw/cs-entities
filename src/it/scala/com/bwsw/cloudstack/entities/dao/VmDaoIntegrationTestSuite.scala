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

import com.bwsw.cloudstack.entities.TestEntities
import com.bwsw.cloudstack.entities.requests.account.AccountCreateRequest
import com.bwsw.cloudstack.entities.requests.account.AccountCreateRequest.RootAdmin
import com.bwsw.cloudstack.entities.requests.vm.{VmCreateRequest, VmFindRequest}
import org.scalatest.FlatSpec

class VmDaoIntegrationTestSuite extends FlatSpec with TestEntities {
  val vmDao = new VirtualMachineDao(executor, mapper)

  it should "retrieve virtual machines after their creation" in {
    val firstAccountName = UUID.randomUUID().toString
    val secondAccountName = UUID.randomUUID().toString

    val domainId = retrievedAdminDomainId
    val accountDao = new AccountDao(executor, mapper)
    createAccountWithName(accountDao, firstAccountName, domainId)
    createAccountWithName(accountDao, secondAccountName, domainId)

    val findByAccountNameRequest = new VmFindRequest()
    findByAccountNameRequest.withAccountName(firstAccountName)

    assert(vmDao.find(findByAccountNameRequest).isEmpty)

    val serviceOfferingId = retrievedServiceOffering.id
    val templateId = retrievedTemplateId
    val zoneId = retrievedZoneId

    val vmCreationSettings = VmCreateRequest.Settings(
      serviceOfferingId,
      templateId,
      zoneId
    )
    val firstVmCreateRequest = new VmCreateRequest(vmCreationSettings)
    firstVmCreateRequest.withDomainAccount(firstAccountName, domainId)

    vmDao.create(firstVmCreateRequest)

    val vmsAfterCreation = vmDao.find(findByAccountNameRequest)
    assert(vmsAfterCreation.size == 1 && vmsAfterCreation.head.accountName == firstAccountName)

    val secondVmCreateRequest = new VmCreateRequest(vmCreationSettings)
    secondVmCreateRequest.withDomainAccount(secondAccountName, domainId)

    vmDao.create(secondVmCreateRequest)

    val findRequest = new VmFindRequest
    val allAccountNamesInVms = vmDao.find(findRequest).map(_.accountName)

    assert(allAccountNamesInVms.contains(firstAccountName) && allAccountNamesInVms.contains(secondAccountName))
  }

  private def createAccountWithName(accountDao: AccountDao, name: String, domainId: UUID): Unit = {
    val firstAccountCreationSettings = AccountCreateRequest.Settings(
      _type = RootAdmin,
      email = "e@e",
      firstName = "first",
      lastName = "last",
      password = "passwd",
      username = s"username $name"
    )

    val firstAccountCreateRequest = new AccountCreateRequest(firstAccountCreationSettings)
    firstAccountCreateRequest.withName(name)
    firstAccountCreateRequest.withDomain(domainId)

    accountDao.create(firstAccountCreateRequest)
  }
}
