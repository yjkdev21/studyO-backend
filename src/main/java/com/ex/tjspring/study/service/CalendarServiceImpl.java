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
    public List<CalendarDto> getCalendarList(Long groupId) {
        return calendarDao.getCalendarList(groupId);
    }

    // 상세 조회
    public CalendarDto getCalendarById(Long id) {
        return calendarDao.getCalendarById(id);
    }

    public CalendarDto calendarInsert(CalendarDto dto, Long loginUserId) {
        int adminCount = calendarDao.countStudyAdmin(loginUserId, dto.getGroupId());
        if (adminCount == 0) {
            throw new RuntimeException("일정 등록 권한이 없습니다.");
        }
        dto.setWriterId(loginUserId);
        calendarDao.calendarInsert(dto);

        // insert 후 생성된 ID로 완전한 데이터 조회해서 반환
        return calendarDao.getCalendarById(dto.getId());
    }

    public CalendarDto calendarUpdate(CalendarDto dto, Long loginUserId) {
        int adminCount = calendarDao.countStudyAdmin(loginUserId, dto.getGroupId());
        if (adminCount == 0) {
            throw new RuntimeException("일정 수정 권한이 없습니다.");
        }
        dto.setWriterId(loginUserId);
        calendarDao.calendarUpdate(dto);

        // update 후 최신 데이터 조회해서 반환
        return calendarDao.getCalendarById(dto.getId());
    }

    //삭제 (권한 체크)
    public  void calendarDelete(Long id, Long loginUserId) {
        // 일정 id로 groupId 가져오기
        CalendarDto existing = calendarDao.getCalendarById(id);
        if (existing == null) {
            throw new RuntimeException("해당 일정이 존재하지 않습니다.");
        }

        Long groupId = existing.getGroupId();
        int adminCount = calendarDao.countStudyAdmin(loginUserId, groupId);
        if (adminCount == 0) {
            throw new RuntimeException("일정 삭제 권한이 없습니다.");
        }
        calendarDao.calendarDelete(id);
    }

}
