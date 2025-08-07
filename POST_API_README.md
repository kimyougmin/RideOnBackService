# Post API 문서

커뮤니티 게시글 CRUD 기능과 댓글 기능을 제공하는 API입니다.

## 인증 요구사항

### 인증이 필요한 기능
- 게시글 작성 (`POST /api/posts`)
- 게시글 수정 (`PUT /api/posts/{postId}`)
- 게시글 삭제 (`DELETE /api/posts/{postId}`)
- 댓글 작성 (`POST /api/posts/{postId}/comments`)
- 댓글 수정 (`PUT /api/posts/{postId}/comments/{commentId}`)
- 댓글 삭제 (`DELETE /api/posts/{postId}/comments/{commentId}`)

### 인증이 필요하지 않은 기능
- 게시글 조회 (`GET /api/posts/{postId}`)
- 게시글 목록 조회 (`GET /api/posts`)
- 사용자별 게시글 조회 (`GET /api/posts/user/{userId}`)
- 댓글 조회 (`GET /api/posts/{postId}/comments/{commentId}`)
- 댓글 목록 조회 (`GET /api/posts/{postId}/comments`)

## 게시글 API 엔드포인트

### 1. 게시글 작성
- **URL**: `POST /api/posts`
- **Description**: 새로운 게시글을 작성합니다. **인증이 필요합니다.**
- **Request Body**:
```json
{
    "title": "게시글 제목",
    "content": "게시글 내용",
    "image": "이미지 URL"
}
```
- **Response**: `201 Created`
```json
{
    "id": 1,
    "userId": 1,
    "title": "게시글 제목",
    "content": "게시글 내용",
    "image": "이미지 URL",
    "viewCount": 0,
    "likeCount": 0,
    "createdAt": "2024-01-01T12:00:00",
    "updatedAt": "2024-01-01T12:00:00"
}
```

### 2. 게시글 조회
- **URL**: `GET /api/posts/{postId}`
- **Description**: 특정 게시글을 조회합니다. 조회 시 조회수가 증가합니다.
- **Response**: `200 OK`
```json
{
    "id": 1,
    "userId": 1,
    "title": "게시글 제목",
    "content": "게시글 내용",
    "image": "이미지 URL",
    "viewCount": 1,
    "likeCount": 0,
    "createdAt": "2024-01-01T12:00:00",
    "updatedAt": "2024-01-01T12:00:00"
}
```

### 3. 게시글 목록 조회
- **URL**: `GET /api/posts`
- **Description**: 모든 게시글을 최신순으로 조회합니다.
- **Query Parameters**:
  - `page`: 페이지 번호 (기본값: 0)
  - `size`: 페이지 크기 (기본값: 10)
  - `sort`: 정렬 기준 (예: `createdAt,desc`)
- **Response**: `200 OK`
```json
{
    "content": [
        {
            "id": 1,
            "userId": 1,
            "title": "게시글 제목",
            "content": "게시글 내용",
            "image": "이미지 URL",
            "viewCount": 1,
            "likeCount": 0,
            "createdAt": "2024-01-01T12:00:00",
            "updatedAt": "2024-01-01T12:00:00"
        }
    ],
    "pageable": {
        "sort": {
            "empty": false,
            "sorted": true,
            "unsorted": false
        },
        "offset": 0,
        "pageNumber": 0,
        "pageSize": 10,
        "paged": true,
        "unpaged": false
    },
    "last": true,
    "totalElements": 1,
    "totalPages": 1,
    "size": 10,
    "number": 0,
    "sort": {
        "empty": false,
        "sorted": true,
        "unsorted": false
    },
    "first": true,
    "numberOfElements": 1,
    "empty": false
}
```

### 4. 사용자별 게시글 조회
- **URL**: `GET /api/posts/user/{userId}`
- **Description**: 특정 사용자가 작성한 게시글을 최신순으로 조회합니다.
- **Query Parameters**: (게시글 목록 조회와 동일)
- **Response**: `200 OK` (게시글 목록 조회와 동일)

### 5. 게시글 수정
- **URL**: `PUT /api/posts/{postId}`
- **Description**: 게시글을 수정합니다. 작성자만 수정할 수 있습니다. **인증이 필요합니다.**
- **Request Body**:
```json
{
    "title": "수정된 제목",
    "content": "수정된 내용",
    "image": "수정된 이미지 URL"
}
```
- **Response**: `200 OK` (게시글 조회와 동일)

### 6. 게시글 삭제
- **URL**: `DELETE /api/posts/{postId}`
- **Description**: 게시글을 삭제합니다. 작성자만 삭제할 수 있습니다. **인증이 필요합니다.**
- **Response**: `204 No Content`

## 댓글 API 엔드포인트

### 1. 댓글 작성
- **URL**: `POST /api/posts/{postId}/comments`
- **Description**: 특정 게시글에 댓글을 작성합니다. **인증이 필요합니다.**
- **Request Body**:
```json
{
    "content": "댓글 내용"
}
```
- **Response**: `201 Created`
```json
{
    "id": 1,
    "postId": 1,
    "content": "댓글 내용",
    "createdAt": "2024-01-01T12:00:00"
}
```

### 2. 댓글 조회
- **URL**: `GET /api/posts/{postId}/comments/{commentId}`
- **Description**: 특정 댓글을 조회합니다.
- **Response**: `200 OK`
```json
{
    "id": 1,
    "postId": 1,
    "content": "댓글 내용",
    "createdAt": "2024-01-01T12:00:00"
}
```

### 3. 댓글 목록 조회
- **URL**: `GET /api/posts/{postId}/comments`
- **Description**: 특정 게시글의 댓글을 생성 시간 순으로 조회합니다.
- **Query Parameters**:
  - `page`: 페이지 번호 (기본값: 0)
  - `size`: 페이지 크기 (기본값: 20)
- **Response**: `200 OK`
```json
{
    "content": [
        {
            "id": 1,
            "postId": 1,
            "content": "첫 번째 댓글",
            "createdAt": "2024-01-01T12:00:00"
        },
        {
            "id": 2,
            "postId": 1,
            "content": "두 번째 댓글",
            "createdAt": "2024-01-01T12:01:00"
        }
    ],
    "totalPages": 1,
    "totalElements": 2,
    "pageable": {
        "pageNumber": 0,
        "pageSize": 20,
        "sort": [],
        "offset": 0,
        "paged": true,
        "unpaged": false
    },
    "first": true,
    "last": true,
    "numberOfElements": 2,
    "size": 20,
    "number": 0,
    "sort": [],
    "empty": false
}
```

### 4. 댓글 수정
- **URL**: `PUT /api/posts/{postId}/comments/{commentId}`
- **Description**: 댓글을 수정합니다. **인증이 필요합니다.**
- **Request Body**:
```json
{
    "content": "수정된 댓글 내용"
}
```
- **Response**: `200 OK` (댓글 조회와 동일)

### 5. 댓글 삭제
- **URL**: `DELETE /api/posts/{postId}/comments/{commentId}`
- **Description**: 댓글을 삭제합니다. **인증이 필요합니다.**
- **Response**: `204 No Content`

## 에러 응답

### 401 Unauthorized (인증 실패)
```json
{
    "error": "Unauthorized",
    "message": "Full authentication is required to access this resource"
}
```

### 400 Bad Request
```json
{
    "status": 400,
    "message": "게시글을 찾을 수 없습니다. ID: 999"
}
```

### 400 Bad Request (Validation Error)
```json
{
    "status": 400,
    "message": "입력값이 올바르지 않습니다.",
    "errors": {
        "title": "제목은 필수입니다.",
        "content": "내용은 필수입니다."
    }
}
```

### 500 Internal Server Error
```json
{
    "status": 500,
    "message": "서버 내부 오류가 발생했습니다."
}
```

## 인증

현재 구현에서는 Spring Security를 사용하여 인증을 처리합니다. 인증이 필요한 API를 호출할 때는 적절한 인증 토큰이나 세션 정보가 필요합니다.

## Swagger UI

API 문서는 Swagger UI를 통해 확인할 수 있습니다:
- **URL**: `http://localhost:8080/swagger-ui/index.html`

## 데이터베이스 스키마

게시글과 댓글은 다음 테이블을 사용합니다:
- `posts`: 게시글 정보
- `post_comment`: 게시글 댓글
- `post_like`: 게시글 좋아요 (향후 구현 예정)
- `category`: 카테고리 (향후 구현 예정)
- `post_category`: 게시글-카테고리 연결 (향후 구현 예정) 