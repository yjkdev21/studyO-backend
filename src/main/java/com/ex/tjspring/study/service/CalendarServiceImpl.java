package com.ex.tjspring.study.service;

import com.ex.tjspring.study.dao.CalendarDao;
import com.ex.tjspring.study.dto.CalendarDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalendarServiceImpl {
    @Autowired
    private CalendarDao calendarDao;

    // 전체 조회
    public List<CalendarDto> getCalendarList(Long studyId) {
        return calendarDao.getCalendarList(studyId);
    }

    // 상세 조회
    public CalendarDto getCalendarById(Long id) {
        return calendarDao.getCalendarById(id);
    }

    // 일정 등록
   public void calendarInsert(CalendarDto dto) {
       calendarDao.calendarInsert(dto);
   }

   // 수정
    public void calendarUpdate(CalendarDto dto) {
        calendarDao.calendarUpdate(dto);
    }

    //삭제
    public  void calendarDelete(Long id) {
        calendarDao.calendarDelete(id);
    }

}
