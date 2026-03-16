package com.csc2920.group_project;

import com.csc2920.group_project.service.MemberService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements CommandLineRunner {

    private final MemberService memberService;

    public StartupRunner(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    public void run(String... args) {
//        memberService.syncMembersToDatabase();
    }
}
