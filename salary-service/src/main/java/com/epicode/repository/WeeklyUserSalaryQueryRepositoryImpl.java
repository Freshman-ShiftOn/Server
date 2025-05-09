package com.epicode.repository;
import com.epicode.domain.QUser;
import com.epicode.domain.QUserBranch;
import com.epicode.domain.QWeeklyUserSalary;
import com.epicode.dto.WeeklySalaryDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WeeklyUserSalaryQueryRepositoryImpl implements WeeklyUserSalaryQueryRepository {
    private final JPAQueryFactory queryFactory;
    public List<WeeklySalaryDto> findWeeklySalary(Long branchId, int month) {
        QWeeklyUserSalary wus = QWeeklyUserSalary.weeklyUserSalary;
        QUser u = QUser.user;
        QUserBranch ub = QUserBranch.userBranch;

        return queryFactory
                .select(Projections.fields(WeeklySalaryDto.class,
                        wus.userId.as("userId"),
                        u.name.as("name"),
                        wus.branchId.as("branchId"),
                        wus.week.as("week"),
                        wus.totalMinutes.as("totalMinutes"),
                        ub.personal_cost.as("personal_cost"),
                        wus.calculatedSalary.as("calculatedSalary"),
                        wus.weeklyAllowanceEligible.as("weeklyAllowanceEligible")
                ))
                .from(wus)
                .join(u).on(wus.userId.eq(u.id))
                .join(ub).on(
                        wus.userId.eq(ub.user.id),
                        wus.branchId.eq(ub.branch.id)
                )
                .where(
                        wus.branchId.eq(branchId),
                        wus.month.eq(month)
                )
                .fetch();
    }
}
