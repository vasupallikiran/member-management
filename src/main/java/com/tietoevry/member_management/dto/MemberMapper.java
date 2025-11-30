package com.tietoevry.member_management.dto;

import com.tietoevry.member_management.entity.Member;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    
    MemberDTO toDTO(Member member);
    
    Member toEntity(MemberDTO memberDTO);
}
