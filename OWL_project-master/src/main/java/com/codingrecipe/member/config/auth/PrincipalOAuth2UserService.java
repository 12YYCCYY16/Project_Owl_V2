package com.codingrecipe.member.config.auth;

import com.codingrecipe.member.entity.MemberEntity;
import com.codingrecipe.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Will need this bean in SecurityConfig
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PrincipalOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("USER REQUEST: " + userRequest.getClientRegistration()); // kakao, naver

        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println("OAUTH2 USER: " + oAuth2User.getAttributes());

        // Parse Provider Info
        String provider = userRequest.getClientRegistration().getClientId(); // google, naver... wait, registrationId is better
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        
        String providerId = null;
        String email = null;
        String name = null;

        if (registrationId.equals("kakao")) {
            Map<String, Object> attributes = oAuth2User.getAttributes();
            // Kakao returns id as Long, and account info in kakao_account
            providerId = String.valueOf(attributes.get("id"));
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            
            email = (String) kakaoAccount.get("email");
            name = (String) profile.get("nickname");
            
        } else if (registrationId.equals("naver")) {
            Map<String, Object> response = (Map<String, Object>) oAuth2User.getAttributes().get("response");
            providerId = (String) response.get("id");
            email = (String) response.get("email");
            name = (String) response.get("name");
        }

        if (email == null) {
            // Fallback unique email if provided email is missing
            email = registrationId + "_" + providerId + "@social.login"; 
        }

        // Auto Login / Sign Up logic
        Optional<MemberEntity> memberOptional = memberRepository.findByMemberEmail(email);
        MemberEntity memberEntity;

        if (memberOptional.isEmpty()) {
            // Sign Up
            memberEntity = new MemberEntity();
            memberEntity.setMemberEmail(email);
            memberEntity.setMemberName(name);
            memberEntity.setMemberPassword(UUID.randomUUID().toString()); // Random Password
            memberEntity.setProvider(registrationId);
            memberEntity.setProviderId(providerId);
            
            memberRepository.save(memberEntity);
        } else {
            memberEntity = memberOptional.get();
        }

        return new PrincipalDetails(memberEntity, oAuth2User.getAttributes());
    }
}
