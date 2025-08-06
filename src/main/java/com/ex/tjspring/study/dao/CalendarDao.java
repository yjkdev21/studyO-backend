package com.ex.tjspring.study.dao;

import com.ex.tjspring.study.dto.CalendarDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface CalendarDao {
    // 스터디 ID 기준 일정 전체 조회
    List<CalendarDto> getCalendarList (@Param("groupId") Long studyId);

    // 상세조회
    CalendarDto getCalendarById(@Param("id") Long id);

    // 등록
    void calendarInsert(CalendarDto calendarDto);

    // 수정
    void calendarUpdate(CalendarDto calendarDto);

    // 삭제
    void calendarDelete(@Param("id") Long id);

    // 스터디장인지 확인
    int countStudyAdmin(@Param("userId") Long userId, @Param("groupId") Long studyId);



}
