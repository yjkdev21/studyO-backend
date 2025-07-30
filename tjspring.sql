DROP TABLE ex_dto;
CREATE TABLE EX_DTO (
    id NUMBER(19, 0) PRIMARY KEY, -- Long 타입에 해당, 19자리 숫자 (Java의 Long 최대값 고려)
    title VARCHAR2(255) NOT NULL, -- String 타입, 최대 255자, NULL 허용 안함
    content CLOB,                 -- String 타입 (긴 텍스트), CLOB은 대용량 텍스트 저장에 적합
    reg_date TIMESTAMP DEFAULT SYSTIMESTAMP -- LocalDateTime 타입에 해당, 날짜와 시간 정보 저장, 시스템 시각으로 자동 등록
);

-- 시퀀스 생성 (id 컬럼의 자동 증가를 위해)
DROP SEQUENCE EX_DTO_SEQ;
CREATE SEQUENCE EX_DTO_SEQ
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;

-- 트리거 생성 (시퀀스를 사용하여 id 자동 증가)
DROP TRIGGER EX_DTO_TRG;
CREATE OR REPLACE TRIGGER EX_DTO_TRG
BEFORE INSERT ON EX_DTO
FOR EACH ROW
BEGIN
    IF :NEW.id IS NULL THEN
        SELECT EX_DTO_SEQ.NEXTVAL INTO :NEW.id FROM DUAL;
    END IF;
END;
/


INSERT INTO EX_DTO (title, content) VALUES ('첫 번째 게시글 제목', '이것은 첫 번째 테스트 게시글의 내용입니다. ');
INSERT INTO EX_DTO (title, content) VALUES ('두 번째 게시물 제목', '두 번째 게시물은 좀 더 짧은 내용으로 구성되어 있습니다.');
INSERT INTO EX_DTO (title, content) VALUES ('세 번째 글입니다', '데이터베이스에 저장될 세 번째 글의 내용입니다. CLOB 타입이므로 매우 긴 내용을 담을 수 있습니다.');
INSERT INTO EX_DTO (title, content) VALUES ('네 번째 테스트', '네 번째 테스트 데이터 삽입을 위한 내용입니다.');
INSERT INTO EX_DTO (title, content) VALUES ('다섯 번째 이야기', '다섯 번째 게시글은 특정 주제에 대한 심층적인 분석을 담고 있습니다. 여러 문단으로 구성될 수 있습니다.');
INSERT INTO EX_DTO (title, content) VALUES ('여섯 번째 기록', '간단한 여섯 번째 기록입니다.');
INSERT INTO EX_DTO (title, content) VALUES ('일곱 번째 정보', '일곱 번째 게시글은 다양한 정보를 포함하고 있습니다.');
INSERT INTO EX_DTO (title, content) VALUES ('여덟 번째 노트', '개인적인 여덟 번째 노트입니다.');
INSERT INTO EX_DTO (title, content) VALUES ('아홉 번째 샘플', '아홉 번째 샘플 데이터입니다.');
INSERT INTO EX_DTO (title, content) VALUES ('마지막 열 번째', '이것은 열 번째이자 마지막 테스트 게시글의 내용입니다. ');

COMMIT;

SELECT * FROM EX_DTO;


DROP TABLE EX_DTO_EDITOR;
CREATE TABLE EX_DTO_EDITOR (
    id NUMBER(19, 0) PRIMARY KEY, -- Long 타입에 해당, 19자리 숫자 (Java의 Long 최대값 고려)
    title VARCHAR2(255) NOT NULL, -- String 타입, 최대 255자, NULL 허용 안함
    content CLOB,                 -- String 타입 (긴 텍스트), CLOB은 대용량 텍스트 저장에 적합
    reg_date TIMESTAMP DEFAULT SYSTIMESTAMP -- LocalDateTime 타입에 해당, 날짜와 시간 정보 저장, 시스템 시각으로 자동 등록
);

-- 시퀀스 생성 (id 컬럼의 자동 증가를 위해)
DROP SEQUENCE EX_DTO_EDITOR_SEQ;
CREATE SEQUENCE EX_DTO_EDITOR_SEQ
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;

-- 트리거 생성 (시퀀스를 사용하여 id 자동 증가)
DROP TRIGGER EX_DTO_EDITOR_TRG;
CREATE OR REPLACE TRIGGER EX_DTO_EDITOR_TRG
BEFORE INSERT ON EX_DTO_EDITOR
FOR EACH ROW
BEGIN
    IF :NEW.id IS NULL THEN
        SELECT EX_DTO_EDITOR_SEQ.NEXTVAL INTO :NEW.id FROM DUAL;
    END IF;
END;
/


select * from ex_dto_editor;

