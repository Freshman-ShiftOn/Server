package com.epicode.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSpecificTimeSalary is a Querydsl query type for SpecificTimeSalary
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSpecificTimeSalary extends EntityPathBase<SpecificTimeSalary> {

    private static final long serialVersionUID = -1243627005L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSpecificTimeSalary specificTimeSalary = new QSpecificTimeSalary("specificTimeSalary");

    public final TimePath<java.sql.Time> endTime = createTime("endTime", java.sql.Time.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QSalary salary;

    public final StringPath specificDays = createString("specificDays");

    public final NumberPath<java.math.BigDecimal> specificSalary = createNumber("specificSalary", java.math.BigDecimal.class);

    public final TimePath<java.sql.Time> startTime = createTime("startTime", java.sql.Time.class);

    public QSpecificTimeSalary(String variable) {
        this(SpecificTimeSalary.class, forVariable(variable), INITS);
    }

    public QSpecificTimeSalary(Path<? extends SpecificTimeSalary> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSpecificTimeSalary(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSpecificTimeSalary(PathMetadata metadata, PathInits inits) {
        this(SpecificTimeSalary.class, metadata, inits);
    }

    public QSpecificTimeSalary(Class<? extends SpecificTimeSalary> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.salary = inits.isInitialized("salary") ? new QSalary(forProperty("salary")) : null;
    }

}

