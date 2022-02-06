package com.example.demo.repository;

import com.example.demo.entity.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    boolean existsByGroupNumber(String s);

    Optional<Group> findByGroupNumber(String groupNumber);

    @Query(value = "SELECT _groups.number as groupNumber,  _groups.created_time as createdAt, COUNT( _student_group_link.student_id) AS totalStudent FROM _groups LEFT JOIN _student_group_link ON _student_group_link.group_id = _groups.id GROUP BY _groups.id", nativeQuery = true, countQuery = "SELECT COUNT(*) FROM _groups")
    Page<GroupInfo> getGroupInfoListPageable(Pageable pageable);
}