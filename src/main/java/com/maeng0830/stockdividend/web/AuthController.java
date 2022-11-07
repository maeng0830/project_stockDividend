package com.maeng0830.stockdividend.web;

import com.maeng0830.stockdividend.model.Auth;
import com.maeng0830.stockdividend.persist.entity.MemberEntity;
import com.maeng0830.stockdividend.security.TokenProvider;
import com.maeng0830.stockdividend.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Auth.SignUp request) {
        MemberEntity result = this.memberService.register(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody Auth.SignIn request) {
        MemberEntity member = this.memberService.authenticate(request);
        String token = this.tokenProvider.generateToken(member.getUsername(), member.getRoles());

        return ResponseEntity.ok(token);
    }
}
