package hello.core.member;

public class MemberServiceImpl implements MemberService{

    private MemberRepository memberRepository;// 추상화에도 의존하고 구체화에도 의존하는 DIP와 OCP를 위반하는 부분

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void join(Member member) {
        memberRepository.save(member);
    }

    @Override
    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId);
    }
}
