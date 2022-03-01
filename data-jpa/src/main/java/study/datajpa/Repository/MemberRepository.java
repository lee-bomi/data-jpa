package study.datajpa.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    //일반값조회
    @Query("select m.username from Member m")
    List<Member> findUsernameList();

    //DTO조회
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    //파라미터바인딩(콜렉션바인딩)
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    List<Member> findListByUsername(String username); //컬렉션반환

    Member findMemberByUsername(String username); //단건반환

    Optional<Member> findOptionalByUsername(String username);

    Page<Member> findByAge(int age, Pageable pageable);

    @Modifying(clearAutomatically = true) //executeUpdate기능(업데이트하면서 그 결과값 리턴하는역할)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();
        //fetch조인을 쓰기위해 항상 JPQL을 써야만 할까? 그냥 메서드이름으로 가져오는걸 못하는걸까

    //JPQL귀차나! (엔티티그래프는 페치조인으로 가져옴 = 성능최적화)
    @Override
    @EntityGraph(attributePaths = {"team"})  //member조회하면서 team도 함께 조회하고싶어 근데 jpql은 귀차나
    List<Member> findAll();

    //이렇게도 가능 jpql + fetchjoin
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    //queryHint(성능최적화를 해서 readonly , 스냅샷을 안찍어둔다)
    @QueryHints(value = @QueryHint(name="org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    //남이 건들지못하게 일부 lock을 걸어둘 수 있따
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);
}
