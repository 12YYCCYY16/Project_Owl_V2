package com.codingrecipe.member;

import com.codingrecipe.member.dto.MemberDTO;
import com.codingrecipe.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInit {

    private final MemberService memberService;

    @EventListener(ApplicationReadyEvent.class)
    public void initData() {
        // Check if admin exists, if not create
        try {
            MemberDTO admin = new MemberDTO();
            admin.setMemberEmail("ADMIN_EMAIL_HERE");
            admin.setMemberPassword("ADMIN_PASSWORD_HERE");
            admin.setMemberName("Administrator");
            memberService.save(admin);
            System.out.println("=========================================");
            System.out.println("ADMIN ACCOUNT CREATED");
            System.out.println("=========================================");
        } catch (Exception e) {
            // Probably already exists or DB error
            System.out.println("Admin account setup skipped: " + e.getMessage());
        }
    }
}
