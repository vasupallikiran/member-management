package com.tietoevry.member_management.service;

import com.tietoevry.member_management.dto.MemberDTO;
import com.tietoevry.member_management.dto.MemberMapper;
import com.tietoevry.member_management.entity.Member;
import com.tietoevry.member_management.exception.DuplicateEmailException;
import com.tietoevry.member_management.exception.ResourceNotFoundException;
import com.tietoevry.member_management.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@Transactional
public class MemberService {
    
    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private MemberMapper memberMapper;
    
    @Cacheable(value = "members", key = "#id")
    public MemberDTO getMemberById(UUID id) {
        log.debug("Fetching member with id: {}", id);
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));
        return memberMapper.toDTO(member);
    }
    
    public Page<MemberDTO> getAllMembers(int page, int size, String sort) {
        log.debug("Fetching all members with pagination - page: {}, size: {}, sort: {}", page, size, sort);
        
        Sort.Direction direction = Sort.Direction.ASC;
        String sortBy = "firstName";
        
        if (sort != null && !sort.isEmpty()) {
            String[] sortParams = sort.split(",");
            sortBy = sortParams[0];
            if (sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")) {
                direction = Sort.Direction.DESC;
            }
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<Member> members = memberRepository.findAll(pageable);
        return members.map(memberMapper::toDTO);
    }

    public MemberDTO createMember(MemberDTO memberDTO) {
        log.debug("Creating new member with email: {}", memberDTO.getEmail());
        
        if (memberRepository.findByEmail(memberDTO.getEmail()).isPresent()) {
            throw new DuplicateEmailException("Email already exists: " + memberDTO.getEmail());
        }
        
        Member member = memberMapper.toEntity(memberDTO);
        Member savedMember = memberRepository.save(member);
        log.info("Member created successfully with id: {}", savedMember.getId());
        
        return memberMapper.toDTO(savedMember);
    }

    public MemberDTO updateMember(UUID id, MemberDTO memberDTO) {
        log.debug("Updating member with id: {}", id);
        
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));

        if (!member.getEmail().equals(memberDTO.getEmail()) &&
            memberRepository.findByEmail(memberDTO.getEmail()).isPresent()) {
            throw new DuplicateEmailException("Email already exists: " + memberDTO.getEmail());
        }
        
        member.setFirstName(memberDTO.getFirstName());
        member.setLastName(memberDTO.getLastName());
        member.setDateOfBirth(memberDTO.getDateOfBirth());
        member.setEmail(memberDTO.getEmail());
        
        Member updatedMember = memberRepository.save(member);
        log.info("Member updated successfully with id: {}", id);
        
        return memberMapper.toDTO(updatedMember);
    }
    
    @CacheEvict(value = "members", key = "#id")
    public void deleteMember(UUID id) {
        log.debug("Deleting member with id: {}", id);
        
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));
        
        memberRepository.delete(member);
        log.info("Member deleted successfully with id: {}", id);
    }
}
