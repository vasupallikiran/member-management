package com.surest.member.controllers;

import com.surest.member.dto.MemberDTO;
import com.surest.member.service.MemberServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@RequestMapping("/api/v1/members")
@Slf4j
@Tag(name = "Members API", description = "Operations related to member management")
@SecurityRequirement(name = "bearerAuth")
public class MemberController {

    @Autowired
    private MemberServiceImpl memberService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
            summary = "Get all members",
            description = "Retrieve a paginated list of all members",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Members retrieved successfully",
                            content = @Content(schema = @Schema(implementation = Page.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            }
    )
    public ResponseEntity<Page<MemberDTO>> getAllMembers(
            @Parameter(description = "Page number, starting from 0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Sort field (optional)")
            @RequestParam(required = false) String sort) {

        log.info("GET /members request - page: {}, size: {}", page, size);
        Page<MemberDTO> members = memberService.getAllMembers(page, size, sort);
        return ResponseEntity.ok(members);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
            summary = "Get member by ID",
            description = "Retrieve a single member using UUID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Member retrieved successfully",
                            content = @Content(schema = @Schema(implementation = MemberDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Member not found")
            }
    )
    public ResponseEntity<MemberDTO> getMemberById(
            @Parameter(description = "UUID of the member") @PathVariable UUID id) {

        log.info("GET /members/{} request", id);
        MemberDTO member = memberService.getMemberById(id);
        return ResponseEntity.ok(member);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create a new member",
            description = "Admin-only endpoint to create a new member",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Member created successfully",
                            content = @Content(schema = @Schema(implementation = MemberDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request body")
            }
    )
    public ResponseEntity<MemberDTO> createMember(
            @Valid @RequestBody MemberDTO memberDTO) {

        log.info("POST /members request - email: {}", memberDTO.getEmail());
        MemberDTO createdMember = memberService.createMember(memberDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMember);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update member details",
            description = "Admin-only endpoint to update existing member details",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Member updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Member not found")
            }
    )
    public ResponseEntity<MemberDTO> updateMember(
            @Parameter(description = "UUID of the member") @PathVariable UUID id,
            @Valid @RequestBody MemberDTO memberDTO) {

        log.info("PUT /members/{} request", id);
        MemberDTO updatedMember = memberService.updateMember(id, memberDTO);
        return ResponseEntity.ok(updatedMember);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete a member",
            description = "Admin-only endpoint to delete a member by UUID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Member deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Member not found")
            }
    )
    public ResponseEntity<Void> deleteMember(
            @Parameter(description = "UUID of the member") @PathVariable UUID id) {

        log.info("DELETE /members/{} request", id);
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }
}
