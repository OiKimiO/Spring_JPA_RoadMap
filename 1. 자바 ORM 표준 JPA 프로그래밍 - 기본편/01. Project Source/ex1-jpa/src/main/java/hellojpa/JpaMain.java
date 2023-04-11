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

        try{
            Team team = new Team();
            team.setName("TeamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("member1");
            member.changeTeam(team);
            em.persist(member);

            // 영속성 컨텍스트의 특징에 따라 1차 캐시에 있는 내용을 가지고 오기때문에
            // 쿼리 로그가 따로 나오지 않음
            // 쿼리 로그가 나오게 만들고 싶으면 아래의 flush와 clear를 하면 됨
            // flush는 영속성 컨텍스트와 DB의 데이터 싱크를 맞추고 영속성 컨텍스트의 내용을 비운다.
            // em.flush();
            // em.clear();
            System.out.println("team 조회 전 = " + team.toString());

            Team findTeam1 = member.getTeam();
            Team findTeam2 = em.find(Team.class, team.getId());// 1차 캐시
            List<Member> members = findTeam2.getMembers();

            System.out.println("team 조회 후 = " + team.toString());
            System.out.println("findTeam2 조회 후 = " + findTeam2.toString());

            System.out.println("team.members.get(0).username = " + members.get(0).getUsername());

            tx.commit();
        }catch(Exception e){
            tx.rollback();
        }finally {
            em.close();
        }
        emf.close();
    }
}
