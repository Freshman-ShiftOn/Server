package com.epicode.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserBranch is a Querydsl query type for UserBranch
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserBranch extends EntityPathBase<UserBranch> {

    private static final long serialVersionUID = 682224519L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserBranch userBranch = new QUserBranch("userBranch");

    public final QBranch branch;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> joinedAt = createDateTime("joinedAt", java.time.LocalDateTime.class);

    public final NumberPath<java.math.BigDecimal> personal_cost = createNumber("personal_cost", java.math.BigDecimal.class);

    public final StringPath roles = createString("roles");

    public final StringPath status = createString("status");

    public final QUser user;

    public QUserBranch(String variable) {
        this(UserBranch.class, forVariable(variable), INITS);
    }

    public QUserBranch(Path<? extends UserBranch> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserBranch(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserBranch(PathMetadata metadata, PathInits inits) {
        this(UserBranch.class, metadata, inits);
    }

    public QUserBranch(Class<? extends UserBranch> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.branch = inits.isInitialized("branch") ? new QBranch(forProperty("branch")) : null;
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

