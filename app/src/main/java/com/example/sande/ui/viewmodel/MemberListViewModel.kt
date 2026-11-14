package com.example.sande.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.sande.data.entities.Member
import com.example.sande.data.repository.MemberRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.viewModelScope

class MemberListViewModel(
    private val memberRepository: MemberRepository
) : ViewModel() {

    val members: StateFlow<List<Member>> =
        memberRepository.getAll().stateIn(
            viewModelScope,
            kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )
}
