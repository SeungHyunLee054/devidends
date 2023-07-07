package zerobase.dividends.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import zerobase.dividends.exception.impl.AlreadyExistUserException;
import zerobase.dividends.exception.impl.CouldNotFindIdException;
import zerobase.dividends.exception.impl.CouldNotFindUserException;
import zerobase.dividends.exception.impl.IncorrectPasswordException;
import zerobase.dividends.model.Auth;
import zerobase.dividends.persist.MemberRepository;
import zerobase.dividends.persist.entity.MemberEntity;

@Slf4j
@Service
@AllArgsConstructor
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.memberRepository.findByUsername(username)
                .orElseThrow(() -> new CouldNotFindUserException());
    }

    public MemberEntity register(Auth.SignUp member) {
        boolean exists = this.memberRepository
                .existsByUsername(member.getUsername());
        if (exists) {
            throw new AlreadyExistUserException();
        }

        member.setPassword(this.passwordEncoder.encode(member.getPassword()));
        var result = this.memberRepository.save(member.toEntity());

        return result;
    }

    public MemberEntity authenticate(Auth.SignIn member) {
        var user = this.memberRepository.findByUsername(member.getUsername())
                .orElseThrow(() -> new CouldNotFindIdException());

        if (!this.passwordEncoder.matches(member.getPassword(), user.getPassword())) {
            throw new IncorrectPasswordException();
        }

        return user;
    }
}
