package com.codingrecipe.member.controller;

import com.codingrecipe.member.dto.MemberDTO;
import com.codingrecipe.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final MemberService memberService;

    @GetMapping
    public String adminPage(HttpSession session, Model model) {
        String loginEmail = (String) session.getAttribute("loginEmail");
        // Update admin check to the requested email
        if (loginEmail == null || !loginEmail.equals("ADMIN_EMAIL")) {
            return "redirect:/";
        }

        List<MemberDTO> memberList = memberService.findAll();
        model.addAttribute("memberList", memberList);
        return "admin";
    }

    @GetMapping("/delete/{id}")
    public String deleteMember(@PathVariable Long id, HttpSession session) {
        String loginEmail = (String) session.getAttribute("loginEmail");
        if (loginEmail == null || !loginEmail.equals("ADMIN_EMAIL")) {
            return "redirect:/";
        }
        memberService.deleteById(id);
        return "redirect:/admin";
    }
}
