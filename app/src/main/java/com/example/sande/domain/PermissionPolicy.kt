package com.example.sande.domain

import com.example.sande.data.entities.Member

object PermissionPolicy {
    fun canAdjustStock(member: Member?): Boolean =
        member?.role in listOf("OWNER", "MANAGER")

    fun canVoidSale(member: Member?): Boolean =
        member?.role in listOf("OWNER", "MANAGER")

    fun canEditProducts(member: Member?): Boolean =
        member?.role in listOf("OWNER", "MANAGER")

    fun canReadUserList(member: Member?): Boolean =
        member?.role == "OWNER"

    fun canRecordSale(member: Member?): Boolean =
        member?.status == "ACTIVE" && member?.role in listOf("OWNER", "MANAGER", "SALESPERSON")

    fun isOwner(member: Member?): Boolean = member?.role == "OWNER"

    fun isActive(member: Member?): Boolean = member?.status == "ACTIVE"
}
