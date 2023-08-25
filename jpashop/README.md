## 실전 스프링 부트와 JPA 활용2 - API 개발과 성능 최적화
<details>
<summary>Section 01: API 개발 기본 </summary>
<div markdown="1">

### 회원 등록 API V1
- 회원 등록 API를 다음과 같이 만들어보자
```java
    @PostMapping("api/v1/members") 
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    } 
```
- 엔티티를 RequestBody에 직접 매핑했다
  - 문제점 
    - 엔티티에 프레젠테이션 계층을 위한 로직이 추가된다. (프로젝션 얼마나 할지!)
    - 엔티티에 API 검증을 위한 로직이 들어간다.(@NotEmpty 등등)
    - 실무에서는 회원 엔티티를 위한 API가 다양하게 만들어지는데, 한 엔티티에 각각의 API를 위한 모든 요구사항을 담기는 어렵다.
    - 엔티티가 변경되면 API 스펙이 변한다.
  - 결론
    - API 요청 스펙에 맞추어 별도의 DTO를 파라미터로 받자!
    - 실무에서는 엔티티를 API 스펙에 노출하지 말자!

### 회원 등록 API V2
- 회원 등록 API를 다음과 같이 만들어보자
```java
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    } 
```
- CreateMemberRequest를 Member 엔티티 대신에 RequestBody와 매핑한다.
- 엔티티와 프레젠테이션 계층을 위한 로직을 분리할 수 있다.
- 에닡티와 API 스펙을 명확하게 분리할 수 잇다.
- 엔티티가 변해도 API 스펙이 변하지 않는다.



</div>
</details>

