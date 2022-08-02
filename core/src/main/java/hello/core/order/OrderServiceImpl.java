package hello.core.order;

import hello.core.discount.DiscountPolicy;
import hello.core.discount.FixDiscountPolicy;
import hello.core.discount.RateDiscountPolicy;
import hello.core.member.Member;
import hello.core.member.MemberRepository;
import hello.core.member.MemoryMemberRepository;

public class OrderServiceImpl implements OrderService{

    private final MemberRepository memberRepository;// = new MemoryMemberRepository();
    //private final DiscountPolicy discountPolicy = new FixDiscountPolicy();

    public MemberRepository getMemberRepository() {
        return memberRepository;
    }

    public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }
    //private final DiscountPolicy discountPolicy = new RateDiscountPolicy();
    //수정하는 과정에서 DIP와 OCP 위반 이거를 어떻게 해결할 수 있을까?

    //인터페이스만 의존하도록 설정했다. but 당연히 구현체가 없으니 널포인트익셉션 발생
    private DiscountPolicy discountPolicy;
    //해결하기 위해 누군가 구현 객체를 꽂아주어야 한다.


    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId);
        int discountPrice = discountPolicy.discount(member,itemPrice);

        return new Order(memberId, itemName,itemPrice,discountPrice);
    }
}
