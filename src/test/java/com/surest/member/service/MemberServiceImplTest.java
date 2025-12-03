package com.surest.member.service;

import com.surest.member.dto.MemberDTO;
import com.surest.member.dto.MemberMapper;
import com.surest.member.entity.Member;
import com.surest.member.exception.InvalidEmailException;
import com.surest.member.exception.ResourceNotFoundException;
import com.surest.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MemberServiceImplTest {

    private MemberRepository repo;
    private MemberMapper mapper;
    private MemberServiceImpl service;

    @BeforeEach
    void setup() {
        repo = mock(MemberRepository.class);
        mapper = mock(MemberMapper.class);
        service = new MemberServiceImpl();
        ReflectionTestUtils.setField(service, "memberRepository", repo);
        ReflectionTestUtils.setField(service, "memberMapper", mapper);
    }

    @Test
    void getMemberById_success() {
        UUID id = UUID.randomUUID();
        Member member = new Member();
        member.setId(id);

        MemberDTO dto = new MemberDTO();
        dto.setId(id);

        when(repo.findById(id)).thenReturn(Optional.of(member));
        when(mapper.toDTO(member)).thenReturn(dto);

        MemberDTO result = service.getMemberById(id);

        assertEquals(id, result.getId());
    }

    @Test
    void getMemberById_notFound() {
        UUID id = UUID.randomUUID();
        when(repo.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getMemberById(id));
    }

    @Test
    void getAllMembers_sorting() {
        Member member = new Member();
        Page<Member> page = new PageImpl<>(java.util.List.of(member));

        when(repo.findAll(any(Pageable.class))).thenReturn(page);
        when(mapper.toDTO(any())).thenReturn(new MemberDTO());

        Page<MemberDTO> result = service.getAllMembers(0, 10, "email,desc");

        assertEquals(1, result.getContent().size());
        verify(repo).findAll(any(Pageable.class));
    }

    @Test
    void createMember_emailExists() {
        MemberDTO dto = new MemberDTO();
        dto.setEmail("test@test.com");

        when(repo.findByEmail("test@test.com")).thenReturn(Optional.of(new Member()));

        assertThrows(InvalidEmailException.class, () -> service.createMember(dto));
    }

    @Test
    void createMember_success() {
        MemberDTO dto = new MemberDTO();
        dto.setEmail("test@test.com");

        Member entity = new Member();
        entity.setId(UUID.randomUUID());

        when(repo.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repo.save(entity)).thenReturn(entity);
        when(mapper.toDTO(entity)).thenReturn(dto);

        MemberDTO result = service.createMember(dto);

        assertEquals(dto, result);
    }

    @Test
    void updateMember_emailConflict() {
        UUID id = UUID.randomUUID();

        Member existing = new Member();
        existing.setId(id);
        existing.setEmail("old@mail.com");

        MemberDTO update = new MemberDTO();
        update.setEmail("new@mail.com");

        when(repo.findById(id)).thenReturn(Optional.of(existing));
        when(repo.findByEmail("new@mail.com")).thenReturn(Optional.of(new Member()));

        assertThrows(InvalidEmailException.class, () -> service.updateMember(id, update));
    }

    @Test
    void updateMember_success() {
        UUID id = UUID.randomUUID();

        Member existing = new Member();
        existing.setId(id);
        existing.setEmail("same@mail.com");

        MemberDTO update = new MemberDTO();
        update.setEmail("same@mail.com");

        when(repo.findById(id)).thenReturn(Optional.of(existing));
        when(repo.save(existing)).thenReturn(existing);
        when(mapper.toDTO(existing)).thenReturn(update);

        MemberDTO result = service.updateMember(id, update);

        assertEquals(update.getEmail(), result.getEmail());
    }

    @Test
    void deleteMember_notFound() {
        UUID id = UUID.randomUUID();
        when(repo.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.deleteMember(id));
    }

    @Test
    void deleteMember_success() {
        UUID id = UUID.randomUUID();
        Member m = new Member();
        when(repo.findById(id)).thenReturn(Optional.of(m));
        service.deleteMember(id);
        verify(repo).delete(m);
    }
}
