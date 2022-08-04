package hello.core.order;

import hello.core.discount.DiscountPolicy;
import hello.core.discount.FixDiscountPolicy;
import hello.core.discount.RateDiscountPolicy;
import hello.core.member.Member;
import hello.core.member.MemberRepository;
import hello.core.member.MemoryMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor //final이 붙은거는 required 요구되는 것임 요거에 대해서 생성자를 자동으로 만들어줌
public class OrderServiceImpl implements OrderService{

    private final MemberRepository memberRepository;// = new MemoryMemberRepository();
    private final DiscountPolicy discountPolicy;





    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId);
        int discountPrice = discountPolicy.discount(member,itemPrice);

        return new Order(memberId, itemName,itemPrice,discountPrice);
    }
}
