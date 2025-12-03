package com.surest.member.controllers;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.surest.member.dto.MemberDTO;
import com.surest.member.service.MemberServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MemberControllerTest {

    @Mock
    private MemberServiceImpl memberService;

    private MemberController memberController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        memberController = new MemberController();
        ReflectionTestUtils.setField(memberController, "memberService", memberService);

        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(memberController)
                .addFilters((req, res, chain) -> chain.doFilter(req, res)) // disable security
                .build();
    }

    @Test
    void getMemberById_returnsMember() throws Exception {
        UUID id = UUID.randomUUID();
        MemberDTO dto = new MemberDTO(id, "A", "B", LocalDate.now(), "a@b.com", null, null);
        when(memberService.getMemberById(id)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/members/" + id))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(memberService).getMemberById(id);
    }

    @Test
    void createMember_returnsCreated() throws Exception {
        MemberDTO request = new MemberDTO(null, "New", "User", LocalDate.of(1995,1,1), "new@u.com", null, null);
        MemberDTO created = new MemberDTO(UUID.randomUUID(), request.getFirstName(), request.getLastName(), request.getDateOfBirth(), request.getEmail(), null, null);
        when(memberService.createMember(any())).thenReturn(created);

        mockMvc.perform(post("/api/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(memberService).createMember(any(MemberDTO.class));
    }

    @Test
    void updateMember_returnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        MemberDTO request = new MemberDTO(null, "Upd", "User", LocalDate.of(1990,1,1), "upd@u.com", null, null);
        MemberDTO updated = new MemberDTO(id, request.getFirstName(), request.getLastName(), request.getDateOfBirth(), request.getEmail(), null, null);
        when(memberService.updateMember(eq(id), any())).thenReturn(updated);

        mockMvc.perform(put("/api/v1/members/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(memberService).updateMember(eq(id), any(MemberDTO.class));
    }

    @Test
    void deleteMember_returnsNoContent() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(memberService).deleteMember(id);

        mockMvc.perform(delete("/api/v1/members/" + id))
                .andExpect(status().isNoContent());

        verify(memberService).deleteMember(id);
    }
}
