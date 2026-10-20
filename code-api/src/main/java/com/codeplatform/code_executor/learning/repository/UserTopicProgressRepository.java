package com.codeplatform.code_executor.learning.repository;

import com.codeplatform.code_executor.learning.entity.TopicProgressStatus;
import com.codeplatform.code_executor.learning.entity.UserTopicProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTopicProgressRepository extends JpaRepository<UserTopicProgress, Long> {

    Optional<UserTopicProgress> findByUserIdAndTopicId(Long userId, Long topicId);

    Optional<UserTopicProgress> findByUserIdAndTopicSlug(Long userId, String topicSlug);

    List<UserTopicProgress> findByUserId(Long userId);

    @Query("SELECT utp FROM UserTopicProgress utp JOIN FETCH utp.topic t WHERE utp.user.id = :userId AND t.phase.careerPath.slug = :trackSlug")
    List<UserTopicProgress> findByUserIdAndTrackSlug(@Param("userId") Long userId, @Param("trackSlug") String trackSlug);

    @Query("SELECT COUNT(utp) FROM UserTopicProgress utp WHERE utp.user.id = :userId AND utp.topic.phase.careerPath.slug = :trackSlug AND utp.status = :status")
    long countByUserIdAndTrackSlugAndStatus(@Param("userId") Long userId, @Param("trackSlug") String trackSlug, @Param("status") TopicProgressStatus status);
}
