package com.maeng0830.stockdividend.service;

import com.maeng0830.stockdividend.exception.customException.AlreadyExistUserException;
import com.maeng0830.stockdividend.exception.customException.IncorrectMemberIdException;
import com.maeng0830.stockdividend.exception.customException.IncorrectMemberPasswordException;
import com.maeng0830.stockdividend.model.Auth;
import com.maeng0830.stockdividend.persist.entity.MemberEntity;
import com.maeng0830.stockdividend.persist.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class MemberService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.memberRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("couldn't find user ->" + username));
    }

    public MemberEntity register(Auth.SignUp member) {
        boolean exists = this.memberRepository.existsByUsername(member.getUsername());
        if (exists) {
            throw new AlreadyExistUserException();
        }

        member.setPassword(this.passwordEncoder.encode(member.getPassword()));

        log.info("success register member ->" + member.getUsername());
        return this.memberRepository.save(member.toEntity());
    }

    public MemberEntity authenticate(Auth.SignIn member) {
        MemberEntity user = this.memberRepository.findByUsername(member.getUsername())
            .orElseThrow(() -> new IncorrectMemberIdException());

        if (!this.passwordEncoder.matches(member.getPassword(), user.getPassword())) {
            throw new IncorrectMemberPasswordException();
        }

        log.info("success auth member ->" + member.getUsername());
        return user;
    }
}
