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

### 회원 수정 API
- 회원 수정 api를 다음과 같이 만들어보자 
```java
/**
 * 수정 API
 */
@PutMapping("/api/v2/members/{id}")
public UpdateMemberResponse updateMemberV2(
        @PathVariable("id") Long id,
        @RequestBody @Valid UpdateMemberRequest request) {
      memberService.update(id, request.getName());
      Member findMember = memberService.findOne(id);
      return new UpdateMemberResponse(findMember.getId(), findMember.getName());
      } 
```
- 회원 수정도 DTO를 요청 파라미터에 매핑한 것을 확인할 수 있다.
- 다음으로 변경 감지를 이용한 회원 수정 서비스 코드를 살펴보자 
```java
public class MemberService {
  private final MemberRepository memberRepository;
  /**
   * 회원 수정
   */
  @Transactional
  public void update(Long id, String name) {
    Member member = memberRepository.findOne(id);
    member.setName(name);
  }
} 
```
- 명시적인 영속화나 update 쿼리 없이 변경 감지 (Dirty checking)을 통하여 데이터를 수정하고 있다
- 수정은 변경감지를 이용하라고 했다!

### 회원 조회 API V1
- 회원조회 API V1을 살펴보자
```java
 @GetMapping("/api/v1/members")
 public List<Member> membersV1() {
    return memberService.findMembers();
 }
```
- 위와 같이 naive하게 하면 다음의 문제점들이 있따.
  - 엔티티에 프레젠테이션 계층을 위한 로직이 추가된다.
  - 기본적으로 엔티티의 모든 값이 노출된다.
  - 응답 스펙을 맞추기 위한 로직이 추가된다(@JsonIgnore, 별도의 뷰 로직 등등)
  - 실무에서는 같은 엔티티에 대해 API가 용도에 따라 다양하게 만들어지는데 하나의 엔티티는 이 모든 것을 감당할 수 없다.
  - 컬렉션을 직접 반환하면 향후 API 스펙을 변경하기 어렵다.

### 회원 조회 V2: 응답 값으로 엔티티가 아닌 별도의 DTO 사용
- 회원 조회 V2를 살펴보자 
```java
/**
 * 조회 V2: 응답 값으로 엔티티가 아닌 별도의 DTO를 반환한다.
 */
@GetMapping("/api/v2/members")
public Result membersV2() {
      List<Member> findMembers = memberService.findMembers();
      //엔티티 -> DTO 변환
      List<MemberDto> collect = findMembers.stream()
          .map(m -> new MemberDto(m.getName()))
          .collect(Collectors.toList());
      return new Result(collect);
}
@Data
@AllArgsConstructor
static class Result<T> {
  private T data;
}
@Data
@AllArgsConstructor
static class MemberDto {
  private String name;
}
```
- 엔티티를 DTO로 변환해서 반환한다. 
- 엔티티가 변해도 API 스펙이 변경되지 않는다.
- 추가로 Result 클래스로 컬렉션을 감싸서 향후 필요한 필드를 추가할 수 있다.(count라던지 이런 것들 무조건 추가된다! 유지 보수를 위해서는 감싸라)


</div>
</details>

