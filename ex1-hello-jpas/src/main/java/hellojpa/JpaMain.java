package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        //save member
        tx.begin();

        try {
            //비영속
            Member member = new Member();
            member.setId(1L);
            //영속화 (아직 db에 저장 안함)
            em.persist(member);
            //영속 상태가 된 후에 commit을 해야 영속 컨텍스트에 있는 애들의 쿼리가 나간다.
            //em.detach로 영속성 컨텍스트에서 지울 수 있다. em.remove는 db에서의 삭제를 이야기 하는 것

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
