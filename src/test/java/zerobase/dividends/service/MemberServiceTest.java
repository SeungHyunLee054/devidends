package zerobase.dividends.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import zerobase.dividends.model.Auth;
import zerobase.dividends.persist.MemberRepository;
import zerobase.dividends.persist.entity.MemberEntity;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("회원가입 성공")
    void registerSuccess() {
        //given
        given(memberRepository.existsByUsername(anyString()))
                .willReturn(false);
        Auth.SignUp member = new Auth.SignUp();
        member.setUsername("a");
        member.setPassword(passwordEncoder.encode("ps"));
        member.setRoles(Arrays.asList("ROLE_READ"));
        given(memberRepository.save(any()))
                .willReturn(member.toEntity());

        //when
        MemberEntity memberEntity = memberService.register(member);

        //then
        assertEquals("a", memberEntity.getUsername());
        assertEquals("ROLE_READ",memberEntity.getRoles().get(0));
    }
}