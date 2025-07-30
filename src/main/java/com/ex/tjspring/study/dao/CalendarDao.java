package com.ex.tjspring.study.dao;

import com.ex.tjspring.study.dto.CalendarDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface CalendarDao {
    // 스터디 ID 기준 일정 전체 조회
    List<CalendarDto> getCalendarList (@Param("studyId") Long studyId);

    // 상세조회
    CalendarDto getCalendarById(@Param("id") Long id);

    void calendarInsert(CalendarDto calendarDto);

    void calendarUpdate(CalendarDto calendarDto);

    // 논리삭제
    void calendarDelete(@Param("id") Long id);



}
