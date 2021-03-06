package com.hariansyah.templatewithjwt.entities;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class AbstractEntity<ID> {

    @Column(name = "created_date")
    private LocalDateTime createDate;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    public abstract ID getId();

    public abstract void setId(ID id);

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(LocalDateTime modifedDate) {
        this.modifiedDate = modifedDate;
    }

    @PrePersist
    public void prePersist() {
        createDate = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        modifiedDate = LocalDateTime.now();
    }
}
