package com.surest.member.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class MemberRepositoryIntegrationTest {

    @Autowired
    private MemberRepository memberRepository;

//    @Test
//    void saveAndFindByEmail() {
//        Member m = new Member();
//        m.setFirstName("Int");
//        m.setLastName("Test");
//        m.setEmail("int.test@example.com");
//        m.setDateOfBirth(LocalDate.of(1990, 1, 1));
//        Member saved = memberRepository.save(m);
//
//        Optional<Member> found = memberRepository.findByEmail("int.test@example.com");
//        assertThat(found).isPresent();
//        assertThat(found.get().getId()).isEqualTo(saved.getId());
//    }

//    @Test
//    void pagingWorks_findAll() {
//        Member a = new Member();
//        a.setFirstName("A");
//        a.setLastName("A");
//        a.setEmail("a@example.com");
//        a.setDateOfBirth(LocalDate.of(1980, 1, 1));
//        memberRepository.save(a);
//
//        var page = memberRepository.findAll(PageRequest.of(0, 10));
//        assertThat(page).isNotNull();
//        assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(1);
//    }
}
