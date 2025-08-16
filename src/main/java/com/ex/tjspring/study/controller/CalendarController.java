package com.ex.tjspring.study.controller;

import com.ex.tjspring.study.dto.CalendarDto;
import com.ex.tjspring.study.service.CalendarServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/study/calendar") // 공통 URL 경로 설정
public class CalendarController {

    @Autowired
    private CalendarServiceImpl calendarService;

    // 전체 조회
    @GetMapping("/study/{groupId}")
    public List<CalendarDto> getCalendarList(@PathVariable Long groupId) {
        return calendarService.getCalendarList(groupId);
    }

    // 상세 조회
    @GetMapping("/{id}")
    public CalendarDto getCalendarById(@PathVariable Long id) {
        return calendarService.getCalendarById(id);
    }

    @PostMapping
    public CalendarDto insertCalendar(@RequestBody CalendarDto dto,
                                      @RequestHeader("X-USER-ID") Long userId) {
        return calendarService.calendarInsert(dto, userId);
    }

    @PutMapping
    public CalendarDto updateCalendar(@RequestBody CalendarDto dto,
                                      @RequestHeader("X-USER-ID") Long userId) {
        return calendarService.calendarUpdate(dto, userId);
    }

    // 삭제
    @DeleteMapping("/{id}")
    public void  deleteCalendar(@PathVariable Long id,
                                @RequestHeader("X-USER-ID") Long userId) {
        calendarService.calendarDelete(id, userId);
    }
}