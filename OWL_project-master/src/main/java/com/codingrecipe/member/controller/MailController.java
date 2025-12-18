package com.codingrecipe.member.controller;

import com.codingrecipe.member.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class MailController {

    private final EmailService emailService;

    @PostMapping("/mail/send")
    @ResponseBody
    public String sendMail(@RequestParam("email") String email, HttpSession session) {
        System.out.println("Sending email to: " + email);
        String code = emailService.sendVerificationCode(email);
        session.setAttribute("verificationCode", code);
        return "success";
    }

    @PostMapping("/mail/verify")
    @ResponseBody
    public String verifyCode(@RequestParam("code") String code, HttpSession session) {
        String serverCode = (String) session.getAttribute("verificationCode");
        if (serverCode != null && serverCode.equals(code)) {
            session.setAttribute("isVerified", true);
            return "ok";
        }
        return "fail";
    }
}
