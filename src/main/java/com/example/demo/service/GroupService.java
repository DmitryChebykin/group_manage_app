package com.example.demo.service;

import com.example.demo.dto.GroupDto;
import com.example.demo.entity.Group;
import com.example.demo.repository.GroupInfo;
import com.example.demo.repository.GroupRepository;
import com.example.demo.service.exception.GroupException;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;

    @Cacheable(cacheNames = {"group"})
    @Transactional(readOnly = true)
    public Page<GroupDto> getGroupDtosByPagingCondition(Pageable paging) {
        Page<GroupInfo> groupInfoPage = groupRepository.getGroupInfoListPageable(paging);

        return groupInfoPage.map(e -> GroupDto.builder().groupNumber(e.getGroupNumber()).studentsQuantity(e.getTotalStudent()).build());
    }

    @CacheEvict(cacheNames = {"group"}, allEntries = true)
    @Transactional
    public void addGroup(String groupNumber) {
        if (groupRepository.existsByGroupNumber(groupNumber)) {
            throw new GroupException("Group already exist");
        }
        groupRepository.save(Group.builder().groupNumber(groupNumber).build());
    }

    @Transactional(readOnly = true)
    public boolean existsByGroupNumber(String groupNumber) {
        return groupRepository.existsByGroupNumber(groupNumber);
    }
}