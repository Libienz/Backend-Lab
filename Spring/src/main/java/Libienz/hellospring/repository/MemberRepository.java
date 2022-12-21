package Libienz.hellospring.repository;

import Libienz.hellospring.domain.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    Member save(Member member);
    Optional<Member> findById(Long id);
    //옵셔널이란? findById 이런거 하는데 널이 나올 수 있다
    //널을 처리하는 방법 중 하나로 감싸서 반환하는 방법이 잘 쓰이는 듯 뒤에서 자세하게 배운다.
    Optional<Member> findByName(String name);
    List<Member> findAll();

}
