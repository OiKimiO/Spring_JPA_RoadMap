package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em     = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        emf.close();
        try{
            Address address = new Address("city", "street", "10000");
            Member member = new Member();
                   member.setUsername("member1");
                   member.setHomeAddress(address);
            em.persist(member);

            Address newAddress = new Address("newCity", address.getStreet(), address.getZipCode());
            member.setHomeAddress(newAddress);


            tx.commit();
        }catch(Exception e){
            tx.rollback();
        }finally {
            em.close();
        }
    }
}
