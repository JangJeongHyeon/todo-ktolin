package com.tistory.johnmarc.todo.domain

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "todos")
class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Lob
    @Column(name = "content")
    var content: String? = null;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    var status: Status = Status.TODO

    @Column(name = "created_at")
    var createdAt: LocalDateTime = LocalDateTime.now()

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = createdAt
}
