package com.ex.tjspring.study.controller;

import com.ex.tjspring.study.dto.CalendarDto;
import com.ex.tjspring.study.service.CalendarServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // 컨트롤러에서 리턴하는 값이 자동으로 JSON으로 변환되어 응답
@RequestMapping("/api/study/calendar") // 공통 URL 경로 설정
public class CalendarController {

    // CalendarServiceImpl을 스프링이 자동으로 주입(DI)
    @Autowired
    private CalendarServiceImpl calendarService;

    // 전체 조회
    // 예: GET/api/study/calendar/study/1
    @GetMapping("/study/{studyId}")
    public List<CalendarDto> getCalendarList(@PathVariable Long studyId) {
        // 서비스 계층에 studyId 전달하여 일정 목록 반환
        return calendarService.getCalendarList(studyId);
    }

    // 상세 조회
    // 예: GET/api/study/calendar/5
    @GetMapping("/{id}")
    public CalendarDto getCalendarById(@PathVariable Long id) {
        // 서비스 계층에서 id로 일정 상세정보 가져오기
        return calendarService.getCalendarById(id);
    }

    // 일정 등록: 프론트에서 보낸 일정 데이터를 담아 DB에 저장
    // 예: POST/api/study/calendar
    @PostMapping
    public void insetCalendar(@RequestBody CalendarDto dto) {
        // 프론트에서 JSON 형태로 받은 dto를 DB에 저장
        calendarService.calendarInsert(dto);
    }

    // 수정
    // 예: PUT/api/study/calendar
    @PutMapping
    public void updateCalendar(@RequestBody CalendarDto dto) {
        calendarService.calendarUpdate(dto);
    }

    // 삭제
    @DeleteMapping("/{id}")
    public void  deleteCalendar(@PathVariable Long id) {
        calendarService.calendarDelete(id);
    }
}
