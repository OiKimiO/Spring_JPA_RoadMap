package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;
import java.util.Set;

public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em     = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try{
            Member member = new Member();
            member.setUsername("member1");
            member.setHomeAddress(new Address("city1","street","10000"));


            member.getFavoritesFoods().add("치킨");
            member.getFavoritesFoods().add("피자");
            member.getFavoritesFoods().add("족발");

            // Update가 되는데 관련 영상을 보려면 1:N 영상을 참고
            member.getAddressHistory().add(new AddressEntity("old1","street","10000"));
            member.getAddressHistory().add(new AddressEntity("old2","street","10000"));

            em.persist(member);
            em.flush();
            em.clear();

            Member findMember = em.find(Member.class, member.getId());

            // Address oldHomeAddress = findMember.getHomeAddress();
            // findMember.setHomeAddress(new Address("newCity",oldHomeAddress.getStreet(), oldHomeAddress.getZipCode()));

            // 치킨 -> 한식
            // findMember.getFavoritesFoods().remove("치킨");
            // findMember.getFavoritesFoods().add("한식");

            // equals와 hashcode 구현을 제대로 해야합니다.
            // 제대로 못하면 제거가 안됩니다.
            //findMember.getAddressHistory().remove(new AddressEntity("old1","street","10000"));
            //findMember.getAddressHistory().add(new AddressEntity("newCity1","street","10000"));

            tx.commit();
        }catch(Exception e){
            tx.rollback();
        }finally {
            em.close();
        }
    }
}
