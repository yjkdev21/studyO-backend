package com.ex.tjspring.study.controller;


import com.ex.tjspring.study.service.StudyMembershipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/membership")
public class StudyMembershipController {

	private final StudyMembershipService studyMembershipService;


}
