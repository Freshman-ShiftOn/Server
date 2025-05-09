package com.epicode.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QWeeklyUserSalary is a Querydsl query type for WeeklyUserSalary
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWeeklyUserSalary extends EntityPathBase<WeeklyUserSalary> {

    private static final long serialVersionUID = 310176656L;

    public static final QWeeklyUserSalary weeklyUserSalary = new QWeeklyUserSalary("weeklyUserSalary");

    public final NumberPath<Long> branchId = createNumber("branchId", Long.class);

    public final NumberPath<java.math.BigDecimal> calculatedSalary = createNumber("calculatedSalary", java.math.BigDecimal.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> month = createNumber("month", Integer.class);

    public final NumberPath<Integer> totalMinutes = createNumber("totalMinutes", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final NumberPath<Integer> week = createNumber("week", Integer.class);

    public final BooleanPath weeklyAllowanceEligible = createBoolean("weeklyAllowanceEligible");

    public final NumberPath<Integer> year = createNumber("year", Integer.class);

    public QWeeklyUserSalary(String variable) {
        super(WeeklyUserSalary.class, forVariable(variable));
    }

    public QWeeklyUserSalary(Path<? extends WeeklyUserSalary> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWeeklyUserSalary(PathMetadata metadata) {
        super(WeeklyUserSalary.class, metadata);
    }

}

