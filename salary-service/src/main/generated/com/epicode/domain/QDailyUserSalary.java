package com.epicode.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDailyUserSalary is a Querydsl query type for DailyUserSalary
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDailyUserSalary extends EntityPathBase<DailyUserSalary> {

    private static final long serialVersionUID = -2033308044L;

    public static final QDailyUserSalary dailyUserSalary = new QDailyUserSalary("dailyUserSalary");

    public final NumberPath<Long> branchId = createNumber("branchId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<java.math.BigDecimal> dailySalary = createNumber("dailySalary", java.math.BigDecimal.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final DatePath<java.time.LocalDate> workDate = createDate("workDate", java.time.LocalDate.class);

    public final NumberPath<Integer> workedMinutes = createNumber("workedMinutes", Integer.class);

    public final StringPath workTime = createString("workTime");

    public final ListPath<String, StringPath> workType = this.<String, StringPath>createList("workType", String.class, StringPath.class, PathInits.DIRECT2);

    public QDailyUserSalary(String variable) {
        super(DailyUserSalary.class, forVariable(variable));
    }

    public QDailyUserSalary(Path<? extends DailyUserSalary> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDailyUserSalary(PathMetadata metadata) {
        super(DailyUserSalary.class, metadata);
    }

}

