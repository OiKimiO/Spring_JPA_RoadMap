package study.datajpa.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import study.datajpa.entity.Member;

import javax.persistence.criteria.*;

public class MemberSpec {
    public static Specification<Member> teamName(final String teamName){
        return new Specification<Member>(){

            @Override
            public Predicate toPredicate(Root<Member> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                if(StringUtils.isEmpty(teamName)){
                    return null;
                }
                Join<Object, Object> t = root.join("team", JoinType.INNER);// 회원과 조인
                return builder.equal(t.get("name"),teamName);
            }
        };
    }

    public static Specification<Member> username(final String username){
        return new Specification<Member>(){

            @Override
            public Predicate toPredicate(Root<Member> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                return builder.equal(root.get("username"), username);
            }
        };
    }
}
