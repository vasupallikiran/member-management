package com.tietoevry.member_management.controllers;

import com.tietoevry.member_management.dto.MemberDTO;
import com.tietoevry.member_management.service.MemberService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/members")
@Slf4j
public class MemberController {
    
    @Autowired
    private MemberService memberService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<MemberDTO>> getAllMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort) {
        
        log.info("GET /members request - page: {}, size: {}", page, size);
        
        Page<MemberDTO> members = memberService.getAllMembers(page, size, sort);
        return ResponseEntity.ok(members);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<MemberDTO> getMemberById(@PathVariable UUID id) {
        log.info("GET /members/{} request", id);
        MemberDTO member = memberService.getMemberById(id);
        return ResponseEntity.ok(member);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MemberDTO> createMember(@Valid @RequestBody MemberDTO memberDTO) {
        log.info("POST /members request - email: {}", memberDTO.getEmail());
        MemberDTO createdMember = memberService.createMember(memberDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMember);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MemberDTO> updateMember(
            @PathVariable UUID id,
            @Valid @RequestBody MemberDTO memberDTO) {
        
        log.info("PUT /members/{} request", id);
        MemberDTO updatedMember = memberService.updateMember(id, memberDTO);
        return ResponseEntity.ok(updatedMember);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMember(@PathVariable UUID id) {
        log.info("DELETE /members/{} request", id);
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }
}
