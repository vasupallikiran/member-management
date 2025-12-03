package com.surest.member.service;

import com.surest.member.dto.MemberDTO;
import com.surest.member.dto.MemberMapper;
import com.surest.member.entity.Member;
import com.surest.member.exception.InvalidEmailException;
import com.surest.member.exception.ResourceNotFoundException;
import com.surest.member.repository.MemberRepository;
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
public class MemberServiceImpl {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberMapper memberMapper;

    @Transactional(readOnly = true)
    @Cacheable(value = "members", key = "#id")
    public MemberDTO getMemberById(UUID id) {
        log.debug("Fetching member with id: {}", id);
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));
        return memberMapper.toDTO(member);
    }

    @Transactional(readOnly = true)
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

    @Transactional
    public MemberDTO createMember(MemberDTO memberDTO) {
        log.debug("Creating new member with email: {}", memberDTO.getEmail());

        if (memberRepository.findByEmail(memberDTO.getEmail()).isPresent()) {
            throw new InvalidEmailException("Email already exists: " + memberDTO.getEmail());
        }

        Member member = memberMapper.toEntity(memberDTO);
        Member savedMember = memberRepository.save(member);
        log.info("Member created successfully with id: {}", savedMember.getId());

        return memberMapper.toDTO(savedMember);
    }

    @Transactional
    public MemberDTO updateMember(UUID id, MemberDTO memberDTO) {
        log.debug("Updating member with id: {}", id);

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));

        if (!member.getEmail().equals(memberDTO.getEmail()) &&
                memberRepository.findByEmail(memberDTO.getEmail()).isPresent()) {
            throw new InvalidEmailException("Email already exists: " + memberDTO.getEmail());
        }

        member.setFirstName(memberDTO.getFirstName());
        member.setLastName(memberDTO.getLastName());
        member.setDateOfBirth(memberDTO.getDateOfBirth());
        member.setEmail(memberDTO.getEmail());

        Member updatedMember = memberRepository.save(member);
        log.info("Member updated successfully with id: {}", id);

        return memberMapper.toDTO(updatedMember);
    }

    @Transactional
    @CacheEvict(value = "members", key = "#id")
    public void deleteMember(UUID id) {
        log.debug("Deleting member with id: {}", id);

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));

        memberRepository.delete(member);
        log.info("Member deleted successfully with id: {}", id);
    }
}
