-- EX_DTO_EDITOR 테이블 생성 쿼리 (Oracle)
-- 이 테이블은 게시글의 제목과 Quill 에디터로 작성된 내용을 저장합니다.

DROP TRIGGER ATTACHMENTS_TRG;
DROP SEQUENCE ATTACHMENTS_SEQ;
DROP TABLE ATTACHMENTS;

DROP SEQUENCE EX_DTO_EDITOR_SEQ;
DROP TABLE EX_DTO_EDITOR;


CREATE TABLE EX_DTO_EDITOR (
    id NUMBER(19, 0) PRIMARY KEY, -- 게시글 고유 ID (Java의 Long 타입에 해당)
    title VARCHAR2(255) NOT NULL, -- 게시글 제목 (최대 255자, NULL 허용 안함)
    content CLOB,                 -- 게시글 내용 (Quill 에디터 HTML 포함, 대용량 텍스트 저장에 적합)
    reg_date TIMESTAMP DEFAULT SYSTIMESTAMP -- 게시글 등록일 (날짜와 시간 정보 저장, 시스템 시각으로 자동 등록)
);

-- EX_DTO_EDITOR 테이블의 id 컬럼 자동 증가를 위한 시퀀스 생성
CREATE SEQUENCE EX_DTO_EDITOR_SEQ
  START WITH 1
  INCREMENT BY 1
  CACHE 20
  NOCYCLE;



-- ATTACHMENTS 테이블 생성 쿼리 (Oracle)
-- 이 테이블은 EX_DTO_EDITOR 테이블의 게시글에 첨부된 파일의 메타데이터를 저장합니다.

CREATE TABLE ATTACHMENTS (
    id NUMBER(19, 0) PRIMARY KEY, -- 첨부파일 고유 ID
    post_id NUMBER(19, 0) NOT NULL, -- 참조하는 게시글의 ID (EX_DTO_EDITOR 테이블의 id)
    file_name VARCHAR2(255) NOT NULL, -- 원본 파일명 (예: document.pdf, image.jpg)
    stored_file_name VARCHAR2(255) NOT NULL, -- 서버에 저장된 UUID 파일명 (예: a1b2c3d4-e5f6-7890-1234-567890abcdef.jpg)
    file_size NUMBER(19, 0), -- 파일 크기 (바이트)
    file_type VARCHAR2(100), -- 파일 MIME 타입 (예: application/pdf, image/jpeg)
    reg_date TIMESTAMP DEFAULT SYSTIMESTAMP, -- 첨부파일 등록일

    -- EX_DTO_EDITOR 테이블의 id 컬럼을 참조하는 외래 키 제약 조건
    CONSTRAINT fk_ex_dto_editor
        FOREIGN KEY (post_id)
        REFERENCES EX_DTO_EDITOR(id) ON DELETE CASCADE -- 게시글 삭제 시 첨부파일도 자동 삭제
);

-- ATTACHMENTS 테이블의 id 컬럼 자동 증가를 위한 시퀀스 생성
CREATE SEQUENCE ATTACHMENTS_SEQ
  START WITH 1
  INCREMENT BY 1
  CACHE 20
  NOCYCLE;

-- ATTACHMENTS 테이블의 id 자동 증가를 위한 트리거 생성
CREATE OR REPLACE TRIGGER ATTACHMENTS_TRG
BEFORE INSERT ON ATTACHMENTS
FOR EACH ROW
BEGIN
    IF :NEW.id IS NULL THEN
        SELECT ATTACHMENTS_SEQ.NEXTVAL INTO :NEW.id FROM DUAL;
    END IF;
END;
/