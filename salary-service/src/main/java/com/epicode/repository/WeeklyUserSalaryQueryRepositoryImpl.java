package com.epicode.repository;
import com.epicode.domain.QUser;
import com.epicode.domain.QUserBranch;
import com.epicode.domain.QWeeklyUserSalary;
import com.epicode.dto.WeeklySalaryDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WeeklyUserSalaryQueryRepositoryImpl implements WeeklyUserSalaryQueryRepository {
    private final JPAQueryFactory queryFactory;
    public Optional<WeeklySalaryDto> findWeeklySalary(Long userId, Long branchId, int month, int week) {
        QWeeklyUserSalary wus = QWeeklyUserSalary.weeklyUserSalary;
        QUser u = QUser.user;
        QUserBranch ub = QUserBranch.userBranch;

        return Optional.ofNullable(
                queryFactory
                        .select(Projections.constructor(WeeklySalaryDto.class,
                                wus.userId,
                                u.name,
                                wus.branchId,
                                wus.totalMinutes,
                                ub.personal_cost,
                                wus.calculatedSalary,
                                wus.weeklyAllowanceEligible
                        ))
                        .from(wus)
                        .join(u).on(wus.userId.eq(u.id))
                        .join(ub).on(wus.userId.eq(ub.user.id), wus.branchId.eq(ub.branch.id))
                        .where(
                                wus.userId.eq(userId),
                                wus.branchId.eq(branchId),
                                wus.month.eq(month),
                                wus.week.eq(week)
                        )
                        .fetchOne()
        );
    }
}
