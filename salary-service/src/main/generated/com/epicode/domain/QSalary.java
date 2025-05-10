package com.epicode.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSalary is a Querydsl query type for Salary
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSalary extends EntityPathBase<Salary> {

    private static final long serialVersionUID = -221828860L;

    public static final QSalary salary = new QSalary("salary");

    public final NumberPath<java.math.BigDecimal> basicSalary = createNumber("basicSalary", java.math.BigDecimal.class);

    public final NumberPath<Long> branchId = createNumber("branchId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Integer> paymentDate = createNumber("paymentDate", Integer.class);

    public final ListPath<SpecificTimeSalary, QSpecificTimeSalary> specificTimeSalaries = this.<SpecificTimeSalary, QSpecificTimeSalary>createList("specificTimeSalaries", SpecificTimeSalary.class, QSpecificTimeSalary.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final BooleanPath weeklyAllowance = createBoolean("weeklyAllowance");

    public QSalary(String variable) {
        super(Salary.class, forVariable(variable));
    }

    public QSalary(Path<? extends Salary> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSalary(PathMetadata metadata) {
        super(Salary.class, metadata);
    }

}

