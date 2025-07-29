package com.ex.tjspring.common.service;

public enum S3DirKey {

    // S3Storage 에 file upload 필요한 부분있으시면 enum 추가해서 사용 바랍니다.( 이름은 본인이 알아보고 의미 있게 )
    ATTACHFILE("attachfile") , // 게시글 첨부파일 올리는 디렉토리 키
    STUDYGROUPIMG("studygroupimg") ,// 스터디 그룹 대표 썸네일 올리는 디렉토리 키
    MYPROFILEIMG( "myprofileimg"); // 프로필 썸네일 올리는 디렉토리 키

    private final String dirKeyName;

    S3DirKey(String dirKeyName) {
        this.dirKeyName = dirKeyName;
    }

    public String getDirKeyName() {
        return dirKeyName;
    }

}
