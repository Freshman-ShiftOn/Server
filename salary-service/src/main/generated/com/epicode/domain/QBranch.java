package com.epicode.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBranch is a Querydsl query type for Branch
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBranch extends EntityPathBase<Branch> {

    private static final long serialVersionUID = -693140260L;

    public static final QBranch branch = new QBranch("branch");

    public final StringPath adress = createString("adress");

    public final StringPath basic_cost = createString("basic_cost");

    public final StringPath contents = createString("contents");

    public final StringPath dial_numbers = createString("dial_numbers");

    public final StringPath endTime = createString("endTime");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath images = createString("images");

    public final StringPath name = createString("name");

    public final StringPath openTime = createString("openTime");

    public final BooleanPath weekly_allowance = createBoolean("weekly_allowance");

    public QBranch(String variable) {
        super(Branch.class, forVariable(variable));
    }

    public QBranch(Path<? extends Branch> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBranch(PathMetadata metadata) {
        super(Branch.class, metadata);
    }

}

