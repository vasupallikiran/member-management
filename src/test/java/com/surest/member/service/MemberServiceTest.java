package com.surest.member.service;

import com.surest.member.dto.MemberDTO;
import com.surest.member.dto.MemberMapper;
import com.surest.member.entity.Member;
import com.surest.member.exception.InvalidEmailException;
import com.surest.member.exception.UserAlreadyExistsException;
import com.surest.member.exception.ResourceNotFoundException;
import com.surest.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberMapper memberMapper;

    @InjectMocks
    private MemberServiceImpl memberService;

    private UUID existingId;
    private Member existingMember;
    private MemberDTO existingDto;

    @BeforeEach
    void setUp() {
        existingId = UUID.randomUUID();
        existingMember = new Member();
        existingMember.setId(existingId);
        existingMember.setFirstName("John");
        existingMember.setLastName("Doe");
        existingMember.setEmail("john.doe@example.com");
        existingMember.setDateOfBirth(LocalDate.of(1990, 1, 1));
        existingMember.setCreatedAt(LocalDateTime.now());
        existingMember.setUpdatedAt(LocalDateTime.now());

        existingDto = new MemberDTO(
                existingId,
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                "john.doe@example.com",
                existingMember.getCreatedAt(),
                existingMember.getUpdatedAt()
        );
    }

    @Test
    void getMemberById_whenFound_returnsDto() {
        when(memberRepository.findById(existingId)).thenReturn(Optional.of(existingMember));
        when(memberMapper.toDTO(existingMember)).thenReturn(existingDto);

        MemberDTO dto = memberService.getMemberById(existingId);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(existingId);
        verify(memberRepository).findById(existingId);
        verify(memberMapper).toDTO(existingMember);
    }

    @Test
    void getMemberById_whenNotFound_throwsResourceNotFound() {
        UUID id = UUID.randomUUID();
        when(memberRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.getMemberById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Member not found with id");

        verify(memberRepository).findById(id);
        verifyNoInteractions(memberMapper);
    }

    @Test
    void getAllMembers_returnsPagedDtos() {
        Member m1 = existingMember;
        Member m2 = new Member();
        m2.setId(UUID.randomUUID());
        m2.setFirstName("Jane");
        m2.setLastName("Smith");
        m2.setEmail("jane.smith@example.com");
        m2.setDateOfBirth(LocalDate.of(1992, 2, 2));

        MemberDTO d1 = existingDto;
        MemberDTO d2 = new MemberDTO(m2.getId(), "Jane","Smith", m2.getDateOfBirth(), m2.getEmail(), null, null);

        Page<Member> page = new PageImpl<>(List.of(m1, m2));
        when(memberRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(memberMapper.toDTO(m1)).thenReturn(d1);
        when(memberMapper.toDTO(m2)).thenReturn(d2);

        Page<MemberDTO> result = memberService.getAllMembers(0, 10, "firstName,asc");

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).containsExactly(d1, d2);
        verify(memberRepository).findAll(any(Pageable.class));
    }

    @Test
    void createMember_whenEmailExists_throwsDuplicateEmail() {
        MemberDTO dto = new MemberDTO(null, "Alice", "Wonder", LocalDate.of(1985, 3, 3), "alice@example.com", null, null);
        when(memberRepository.findByEmail(dto.getEmail()))
                .thenReturn(Optional.of(new Member()));

        assertThatThrownBy(() -> memberService.createMember(dto))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("Email already exists");

        verify(memberRepository).findByEmail(dto.getEmail());
        verify(memberRepository, never()).save(any());
    }

    @Test
    void createMember_success_returnsSavedDto() {
        MemberDTO dto = new MemberDTO(null, "Alice", "Wonder", LocalDate.of(1985, 3, 3), "alice@example.com", null, null);
        Member toSave = new Member();
        toSave.setFirstName(dto.getFirstName());
        toSave.setLastName(dto.getLastName());
        toSave.setEmail(dto.getEmail());
        toSave.setDateOfBirth(dto.getDateOfBirth());

        Member saved = new Member();
        UUID id = UUID.randomUUID();
        saved.setId(id);
        saved.setFirstName(dto.getFirstName());
        saved.setLastName(dto.getLastName());
        saved.setEmail(dto.getEmail());
        saved.setDateOfBirth(dto.getDateOfBirth());

        MemberDTO savedDto = new MemberDTO(id, dto.getFirstName(), dto.getLastName(), dto.getDateOfBirth(), dto.getEmail(), null, null);

        when(memberRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(memberMapper.toEntity(dto)).thenReturn(toSave);
        when(memberRepository.save(toSave)).thenReturn(saved);
        when(memberMapper.toDTO(saved)).thenReturn(savedDto);

        MemberDTO result = memberService.createMember(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getEmail()).isEqualTo(dto.getEmail());

        verify(memberRepository).findByEmail(dto.getEmail());
        verify(memberRepository).save(toSave);
    }

    @Test
    void updateMember_whenNotFound_throwsResourceNotFound() {
        UUID id = UUID.randomUUID();
        MemberDTO dto = new MemberDTO(null, "X", "Y", LocalDate.now(), "x@y.com", null, null);
        when(memberRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.updateMember(id, dto))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(memberRepository).findById(id);
    }

    @Test
    void updateMember_whenEmailTaken_throwsDuplicateEmail() {
        UUID id = existingId;
        MemberDTO dto = new MemberDTO(null, "John", "Doe", existingMember.getDateOfBirth(), "other@example.com", null, null);

        when(memberRepository.findById(id)).thenReturn(Optional.of(existingMember));
        when(memberRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(new Member()));

        assertThatThrownBy(() -> memberService.updateMember(id, dto))
                .isInstanceOf(InvalidEmailException.class);

        verify(memberRepository).findById(id);
        verify(memberRepository).findByEmail(dto.getEmail());
    }

    @Test
    void updateMember_success_returnsUpdatedDto() {
        UUID id = existingId;
        MemberDTO dto = new MemberDTO(null, "Johnny", "Doe", existingMember.getDateOfBirth(), "john.doe@example.com", null, null);

        when(memberRepository.findById(id)).thenReturn(Optional.of(existingMember));
        // same email so no duplicate check required
        existingMember.setFirstName(dto.getFirstName());
        existingMember.setLastName(dto.getLastName());
        when(memberRepository.save(existingMember)).thenReturn(existingMember);
        when(memberMapper.toDTO(existingMember)).thenReturn(new MemberDTO(id, "Johnny", "Doe", existingMember.getDateOfBirth(), existingMember.getEmail(), null, null));

        MemberDTO updated = memberService.updateMember(id, dto);

        assertThat(updated.getFirstName()).isEqualTo("Johnny");
        verify(memberRepository).save(existingMember);
    }

    @Test
    void deleteMember_whenNotFound_throwsResourceNotFound() {
        UUID id = UUID.randomUUID();
        when(memberRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.deleteMember(id))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(memberRepository).findById(id);
    }

    @Test
    void deleteMember_success_deletes() {
        UUID id = existingId;
        when(memberRepository.findById(id)).thenReturn(Optional.of(existingMember));
        doNothing().when(memberRepository).delete(existingMember);

        memberService.deleteMember(id);

        verify(memberRepository).delete(existingMember);
    }
}
