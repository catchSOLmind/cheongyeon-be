package com.catchsolmind.cheongyeonbe.domain.task.service;

import com.catchsolmind.cheongyeonbe.domain.group.entity.Group;
import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import com.catchsolmind.cheongyeonbe.domain.group.repository.GroupMemberRepository;
import com.catchsolmind.cheongyeonbe.domain.task.dto.request.MyTaskCreateRequest;
import com.catchsolmind.cheongyeonbe.domain.task.dto.request.MyTaskStatusUpdateRequest;
import com.catchsolmind.cheongyeonbe.domain.task.dto.response.MyTaskCreateResponse;
import com.catchsolmind.cheongyeonbe.domain.task.dto.response.MyTaskStatusUpdateResponse;
import com.catchsolmind.cheongyeonbe.domain.task.entity.*;
import com.catchsolmind.cheongyeonbe.domain.task.repository.*;
import com.catchsolmind.cheongyeonbe.global.enums.TaskStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class MyTaskCommandService {

    private final TaskTypeRepository taskTypeRepository;
    private final TaskRepository taskRepository;
    private final TaskOccurrenceRepository occurrenceRepository;
    private final TaskLogRepository taskLogRepository;
    private final GroupMemberRepository groupMemberRepository;

    public MyTaskCreateResponse createMyTasks(Long myMemberId, MyTaskCreateRequest req) {

        GroupMember me = groupMemberRepository.findById(myMemberId)
                .orElseThrow(() -> new IllegalArgumentException("GroupMember not found"));

        Group group = me.getGroup(); // ✅ GroupRepository 필요 없음

        List<TaskType> types = taskTypeRepository.findAllById(req.getTaskTypeIds());
        if (types.size() != req.getTaskTypeIds().size()) {
            throw new IllegalArgumentException("Some taskTypeIds are invalid");
        }

        List<MyTaskCreateResponse.CreatedMyTaskDto> created = new ArrayList<>();

        for (TaskType type : types) {
            Task task = Task.builder()
                    .group(group)
                    .taskType(type)
                    .title(type.getName())
                    .creatorMember(me)
                    .repeatRule(null)
                    .status(TaskStatus.UNCOMPLETED)
                    .build();
            taskRepository.save(task);

            TaskOccurrence occ = TaskOccurrence.builder()
                    .task(task)
                    .group(group)
                    .occurDate(req.getDate())
                    .primaryAssignedMember(me)
                    .status(TaskStatus.UNCOMPLETED)
                    .build();
            occurrenceRepository.save(occ);

            created.add(MyTaskCreateResponse.CreatedMyTaskDto.builder()
                    .taskId(task.getTaskId())
                    .occurrenceId(occ.getOccurrenceId())
                    .taskTypeId(type.getTaskTypeId())
                    .taskName(type.getName())
                    .point(type.getPoint())
                    .build());
        }

        return MyTaskCreateResponse.builder()
                .createdCount(created.size())
                .created(created)
                .build();
    }

    public MyTaskStatusUpdateResponse updateStatus(
            Long myMemberId,
            Long groupId,
            Long occurrenceId,
            MyTaskStatusUpdateRequest req
    ) {
        TaskOccurrence occ = occurrenceRepository
                .findByOccurrenceIdAndGroup_GroupId(occurrenceId, groupId)
                .orElseThrow(() -> new IllegalArgumentException("Occurrence not found"));

        GroupMember me = groupMemberRepository.findById(myMemberId)
                .orElseThrow(() -> new IllegalArgumentException("GroupMember not found"));

        LocalDateTime now = LocalDateTime.now();

        TaskStatus target = TaskStatus.valueOf(String.valueOf(req.getStatus()));

        Long doneByMemberId = null;
        LocalDateTime doneAt = null;

        if (target == TaskStatus.COMPLETED) {
            occ.setStatus(TaskStatus.COMPLETED);

            if (!taskLogRepository.existsByOccurrence_OccurrenceId(occurrenceId)) {
                taskLogRepository.save(
                        TaskLog.builder()
                                .occurrence(occ)
                                .doneByMember(me)
                                .memo(null)
                                .build()
                );
            }

            doneByMemberId = myMemberId;
            doneAt = now;

        } else {
            occ.setStatus(TaskStatus.UNCOMPLETED);
            taskLogRepository.deleteByOccurrence_OccurrenceId(occurrenceId);
        }

        return MyTaskStatusUpdateResponse.builder()
                .occurrenceId(occurrenceId)
                .status(TaskStatus.valueOf(occ.getStatus().name()))
                .doneByMemberId(doneByMemberId)
                .doneAt(doneAt)
                .updatedAt(now)
                .build();
    }
}

