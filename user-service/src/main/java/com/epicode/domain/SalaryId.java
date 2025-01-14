package com.epicode.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class SalaryId implements Serializable {
    private Long userId;
    private Long branchId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SalaryId salaryId = (SalaryId) o;
        return userId.equals(salaryId.userId) &&
                branchId.equals(salaryId.branchId);
    }

    @Override
    public int hashCode() {
        return userId.hashCode() + branchId.hashCode();
    }
}