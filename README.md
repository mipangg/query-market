# QueryMarket

## 📌 프로젝트 소개

> 조회 성능 병목을 분석하고 Cursor Pagination, Redis Cache, FullText Index, 인덱스 최적화를 통해 개선한 이커머스 API 서버

상품 조회 기능을 중심으로 구현한 프로젝트입니다.

단순 CRUD 구현에 그치지 않고, 대량 데이터 환경에서 발생할 수 있는 조회 성능 문제를 직접 분석하고 개선하는 과정에 집중했습니다.

---

## 🎯 프로젝트 목표

* 상품 조회 API 구현
* 페이지네이션 전략 비교
* 검색 성능 최적화
* 캐싱을 통한 DB 부하 감소
* 실행 계획(EXPLAIN ANALYZE) 기반 성능 분석

---

## 🛠 기술 스택

### Backend

* Java 21
* Spring Boot 3.5.10
* Spring Data JPA
* Hibernate

### Database

* MySQL 

### Cache

* Redis

### Test

* JUnit5
* AssertJ

### Build

* Gradle

---

## 🏗 아키텍처

```text
Client
   ↓
Controller
   ↓
Service
   ↓
Repository
   ↓
MySQL
```

### 인기 상품 조회

```text
Client
   ↓
Redis
   ↓ (Cache Miss)
MySQL
```

---

## 📋 주요 기능

### 상품 등록

* 상품 등록
* 판매자 이메일 기반 생성
* Seller 중복 생성 방지

### 상품 삭제

* 상품 삭제

### 상품 조회

* 상품 상세 조회
* 조회수 증가

### 상품 목록 조회

* 카테고리 필터링
* 최신순 정렬
* 가격순 정렬
* 인기순 정렬

### 페이지네이션

* Offset Pagination - 가격순, 인기순
* Cursor Pagination - 최신순

### 인기 상품 조회

* 조회수 기반 Top 10 조회
* Redis 캐싱 적용

### 상품 검색

* FullText Index 기반 검색

---

# 🧩ERD
<img width="756" height="716" alt="Image" src="https://github.com/user-attachments/assets/5dc9aca6-9ea0-464a-b13a-c38c0283de0b" />

---

# 📡 API

| Method | URL                       | 설명       |
| ------ |---------------------------| -------- |
| POST   | /api/products             | 상품 등록    |
| DELETE | /api/products/{productId} | 상품 삭제    |
| GET    | /api/products/{productId} | 상품 상세 조회 |
| GET    | /api/products             | 상품 목록 조회 |
| GET    | /api/products/popular     | 인기 상품 조회 |
| GET    | /api/products/search      | 상품 검색    |

---

# ⚠️ 동시성 문제 고려

## Seller 생성 시 중복 생성 문제

### 문제

동시에 여러 요청이 같은 이메일로 상품 등록을 수행할 경우 Seller가 중복 생성될 수 있음

### 해결

* email Unique 제약 조건 추가
* 저장 실패 시 재조회

```java
@Transactional
public Seller getOrCreateSeller(String email) {
    return sellerRepository.findByEmail(email)
            .orElseGet(() -> {
                try {
                    return sellerRepository.save(
                            Seller.builder()
                                    .email(email)
                                    .build()
                    );

                } catch (DataIntegrityViolationException e) {
                    return sellerRepository.findByEmail(email)
                            .orElseThrow();
                }
            });
}
```

---

# 🚀 성능 개선

## 1. Offset → Cursor Pagination

### 문제

기존 Offset Pagination은 페이지 번호가 커질수록 이전 데이터를 계속 스캔해야 함

```sql
LIMIT 20 OFFSET 100000
```

---

### Before

```sql
-> Limit/Offset: 20/100000 row(s)
    -> Index scan on product using PRIMARY
```

실행 시간

```text
약 52ms
```

---

### After

```sql
SELECT *
FROM product
WHERE id < 100000
ORDER BY id DESC
LIMIT 20;
```

```sql
-> Index range scan on product using PRIMARY
```

실행 시간

```text
약 0.5ms
```

---

### 결과

| 항목     | Offset  | Cursor |
| ------ | ------- | ------ |
| 스캔 Row | 100,000 | 20     |
| 실행 시간  | 52ms    | 0.5ms  |

* 불필요한 데이터 스캔 제거
* PK Index Range Scan 활용
* 대량 데이터 환경에 적합한 구조로 개선

---

## 2. FullText Index 검색 최적화

### 문제

기존 검색

```sql
WHERE name LIKE '%Coffee%'
```

선행 와일드카드로 인해 인덱스 사용 불가

---

### Before

```sql
Table Scan
```

```text
조회 Rows: 100,000
평균 응답시간: 66.4ms
```

---

### 개선

```sql
ALTER TABLE product
ADD FULLTEXT INDEX ft_product_name (name);
```

```sql
MATCH(name)
AGAINST('Coffee' IN NATURAL LANGUAGE MODE)
```

---

### After

```sql
Full-text index search on product using ft_product_name
```

```text
조회 Rows: 14,151
평균 응답시간: 38.1ms
```

---

### 결과

| 항목      | LIKE       | FullText              |
| ------- | ---------- | --------------------- |
| 실행 계획   | Table Scan | FullText Index Search |
| 조회 Rows | 100,000    | 14,151                |
| 인덱스 사용  | X          | O                     |
| 평균 응답시간 | 66.4ms     | 38.1ms                |

* Full Scan 제거
* FullText Index 활용
* 평균 응답시간 약 42.6% 개선

---

### 한계

MySQL 기본 FullText Parser는 영어 검색에 최적화되어 있음

```sql
AGAINST('Coffee')
```

검색 성공

```sql
AGAINST('커피')
```

검색 실패

---

### 향후 개선 방향

* MySQL Ngram Parser
* Elasticsearch
* OpenSearch

---

### 주의

FullText Index는 MySQL DDL로 생성됩니다.

프로젝트 실행 후 아래 SQL을 1회 적용해야 합니다.

```sql
ALTER TABLE product
ADD FULLTEXT INDEX ft_product_name (name);
```

---

## 3. Redis Cache Aside 적용

### 문제

인기 상품 조회 API는 동일한 조회가 반복적으로 발생

```text
Client
 ↓
DB
```

모든 요청이 DB로 전달됨

---

### 개선

Cache Aside 패턴 적용

```text
Client
 ↓
Redis
 ↓ Miss
DB
```

TTL

```text
10분
```

---

### Before

```text
1회차: 248ms
2회차: 108ms
...
10회차: 88ms
```

평균

```text
91.0ms
```

DB 조회

```text
10회
```

---

### After

```text
Cache Miss: 1회
Cache Hit: 9회
```

평균

```text
18.1ms
```

DB 조회

```text
1회
```

---

### 결과

| 항목        | 캐싱 전   | 캐싱 후   |
| --------- | ------ | ------ |
| 평균 응답시간   | 91.0ms | 18.1ms |
| DB 조회     | 10회    | 1회     |
| Cache Hit | 0      | 9      |
| TTL       | -      | 10분    |

* 평균 응답시간 약 80% 개선
* DB 부하 감소
* Cache Aside 패턴 적용

---

## 4. 인기 상품 조회 인덱스 최적화

### 문제

조회수 기준 정렬 시 전체 데이터를 스캔 후 정렬 수행

```sql
ORDER BY view_count DESC
LIMIT 10
```

---

### Before

```sql
Table Scan + Sort
```

```text
약 108ms
```

---

### 개선

```java
@Table(
    indexes = {
        @Index(
            name = "idx_product_view_count",
            columnList = "view_count"
        )
    }
)
```

---

### After

```sql
Index Scan
```

```text
약 0.35ms
```

---

### 결과

| 항목       | Before            | After      |
| -------- | ----------------- | ---------- |
| 실행 계획    | Table Scan + Sort | Index Scan |
| 읽은 데이터 수 | 100,001           | 10         |
| 응답 시간    | 108ms             | 0.35ms     |

* Filesort 제거
* Full Table Scan 제거
* 실행 시간 약 300배 개선

---

# 🔍 트러블 슈팅

## FullText Index 적용 후 검색 결과가 조회되지 않는 문제

### 문제

FullText Index 생성 후에도 검색 결과가 조회되지 않음

### 원인

더미 데이터 재생성 이후 FullText Index가 갱신되지 않은 상태에서 테스트 수행

### 해결

* 인덱스 재생성
* 데이터 적재 후 재검증

---

## Redis 캐싱 적용 시 SerializationException 발생

### 문제

```text
Cannot serialize
```

### 원인

캐시 저장 대상 DTO가 직렬화를 지원하지 않음

### 해결

```java
implements Serializable
```

추가 후 해결

---

# 📚 배운 점

* Cursor Pagination이 대량 데이터 조회에 유리한 이유
* EXPLAIN ANALYZE 기반 실행 계획 분석 방법
* Redis Cache Aside 패턴 적용 경험
* FullText Index 적용 및 한계 분석
* 인덱스 설계가 조회 성능에 미치는 영향

---

# 📈 향후 개선 방향

* Elasticsearch 기반 검색 시스템 구축
* Redis INCR 기반 조회수 증가 처리
* k6 기반 부하 테스트
* Redis Sorted Set 기반 실시간 인기 상품 랭킹
* 조회 API Read/Write 분리 구조 검토

