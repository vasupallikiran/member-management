package com.surest.member.dto;

import com.surest.member.entity.Member;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    
    MemberDTO toDTO(Member member);
    
    Member toEntity(MemberDTO memberDTO);
}
