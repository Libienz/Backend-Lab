package hellojpa;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
public class Member {

    /**
     * @Id
     * PK mapping
     * @GeneratedValue : id 자동 생성
     * Strategy AUTO : 디폴트 값, 데이터베이스 방언에 맞추어 Id가 생성된다.
     * Strategy IDENTITY: 기본 키 생성을 데이터베이스에 위임 null로 인서트 쿼리를 날리고 DB에서 셋팅하게 한다 (영속성 컨텍스트에서 관리할 때 문제가 생길 수 있다 )
     *      DB에 쿼리를 날리기 전까지 pk값을 알 수 없기 떄문
     *      이러한 문제를 해결하기 위해 울며 겨자먹기로 persist를 할 때 insert query를 날려버린다 (flush 호출 전인데도!)
     *      이에 IDENTITY 전략에서는 모아서 Insert하는 장점을 누릴 수 없다 (아주 큰 메리트를 놓치는 개념이 아니긴 하지만)
     * Strategy SEQUENCE: 시퀀스 오브젝트의 call next value이용
     * Strategy TABLE: 키 생성 전용 테이블을 만들어서 데이터베이스 시퀀스를 흉내내는 전략
     */
    @Id
    private Long id;

    /**
     * @Column 속성들
     * name: 필드와 매핑할 테이블의 컬럼 이름
     * insertable, updateable: 등록, 변경 가능한지 여부 default = true;
     * nullable: not null 제약조건 default가 true
     * unique: 유니크 제약조건을 걸 대 사용한다 but 컬럼단에서 잘 사용하지 않음 Talble 단에서 유니크 제약조건을 거는 게 일반적으로 선호 됨
     * length: 가변 길이 정한다.
     * precision, scale: 아주 큰 숫자나 floating number 사용할 때 제약조건을 걸 수 있음
     */
    @Column(name = "name")
    private String username;
    //Box class 써도 됨 DB에 매핑될 때는 적절한 숫자 타입으로 작동한다
    private Integer age;
    /**
     * @Enumerated
     * DB에는 Enum type이 없는 경우가 일반적, 매핑을 가능하게 하는 어노테이션
     * 두가지를 선택할 수 있음 ORDINAL, STRING. ORDINAL은 순서 저장, STRING은 문자 저장
     * ORDINAL 쓰는 것은 굉장히 위험! 0으로 저장되어있는데 필드 추가하면 데이터 망가짐 String을 사용하는 것이 필수다!
     */
    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    /**
     * 날짜 타입 매핑정보 제공 Temporal
     * @Temporal
     * 하이버네이트 버젼이 충분히 높다면 그냥 LocalDateTime사용하면 됨
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    /**
     * @Lob
     * 큰 사이즈의 컨텐츠를 위한 @Lob
     * 지정할 수 있는 속성 없음
     * 문자면 CLOB으로 매핑되고 나머지는 BLOB으로 매핑 된다
     */
    @Lob
    private String description;
    //메모리에서만 사용하고 DB에 매핑하지 않는다
    /**
     * @Transient
     * 메모리에서만 사용 DB에 매핑 X
     */
    @Transient
    private int temp;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
