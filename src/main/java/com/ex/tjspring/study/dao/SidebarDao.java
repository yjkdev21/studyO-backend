package com.ex.tjspring.study.dao;

import com.ex.tjspring.study.dto.SidebarDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SidebarDao {
    /**
     * 스터디 ID로 사이드바에 필요한 스터디 정보 조회
     * @param studyId: 조회할 스터디의 고유 식별자(study_groups.id)
     * @return SidebarDto: 사이드바 표시용 스터디 정보(id, category, name, contact)
     * 해당 ID의 스터디가 없으면 null 값 반환
     * XML MAPPER 파일의 <select id='getStudyInfo /> 와 매핑됨
     * SQL: SELECT id, category, group_name as name, contact
     *      FROM study_groups where id = #{studyId}
    **/
    SidebarDto getStudyInfo(Long studyId);

}
